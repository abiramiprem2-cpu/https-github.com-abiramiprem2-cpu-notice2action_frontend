package com.simats.notice2action.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import java.util.UUID

data class ReminderItemModel(
    val id: String = UUID.randomUUID().toString(),
    val taskId: String = UUID.randomUUID().toString(),
    val userId: String = UserSession.currentUser?.id?.toString() ?: "1",
    val title: String,
    val scheduledTime: String,
    val deadline: String,
    var initialEnabled: Boolean = true,
    var status: String = "active",
    val isPast: Boolean = false,
    val sourceDocument: String = "Notice Document",
    val timingPreset: String = "1 day before (9:00 AM)"
)

object ReminderManager {
    val reminders = mutableStateListOf<ReminderItemModel>()

    fun addFromActionTask(
        task: ActionTask,
        documentTitle: String,
        preset: String = "1 day before (9:00 AM)",
        context: Context? = null
    ): ReminderItemModel {
        val existingIndex = reminders.indexOfFirst { it.title.equals(task.title, ignoreCase = true) }
        if (existingIndex != -1) {
            val existingItem = reminders[existingIndex]
            val updatedItem = existingItem.copy(initialEnabled = true, status = "active")
            reminders[existingIndex] = updatedItem
            if (context != null) {
                NotificationHelper.scheduleTaskReminder(context, updatedItem)
            }
            return updatedItem
        }

        val calculatedScheduled = if (task.dateTime.contains("•")) {
            val parts = task.dateTime.split("•")
            "${parts.first().trim()} • 9:00 AM"
        } else {
            "${task.dateTime} • 9:00 AM"
        }

        val newReminder = ReminderItemModel(
            id = UUID.randomUUID().toString(),
            taskId = UUID.randomUUID().toString(),
            title = task.title,
            scheduledTime = calculatedScheduled,
            deadline = task.dateTime,
            initialEnabled = true,
            status = "active",
            isPast = false,
            sourceDocument = documentTitle,
            timingPreset = preset
        )
        reminders.add(0, newReminder)
        if (context != null) {
            NotificationHelper.scheduleTaskReminder(context, newReminder)
        }
        return newReminder
    }

    fun isReminderActive(taskTitle: String): Boolean {
        return reminders.any { it.title.equals(taskTitle, ignoreCase = true) && it.initialEnabled && it.status == "active" }
    }

    fun isReminderActiveById(reminderId: String): Boolean {
        return reminders.any { it.id == reminderId && it.initialEnabled && it.status == "active" }
    }

    fun addCustomReminder(
        title: String,
        deadline: String,
        scheduled: String,
        source: String = "Custom Reminder",
        preset: String = "1 day before (9:00 AM)",
        context: Context? = null
    ) {
        val newReminder = ReminderItemModel(
            id = UUID.randomUUID().toString(),
            taskId = UUID.randomUUID().toString(),
            title = title,
            scheduledTime = scheduled,
            deadline = deadline,
            initialEnabled = true,
            status = "active",
            isPast = false,
            sourceDocument = source,
            timingPreset = preset
        )
        reminders.add(0, newReminder)
        if (context != null) {
            NotificationHelper.scheduleTaskReminder(context, newReminder)
        }
    }

    fun editReminder(
        id: String,
        newTitle: String,
        newDeadline: String,
        newScheduled: String,
        preset: String = "1 day before (9:00 AM)",
        context: Context? = null
    ) {
        val index = reminders.indexOfFirst { it.id == id }
        if (index != -1) {
            val oldItem = reminders[index]
            if (context != null) {
                NotificationHelper.cancelTaskReminder(context, oldItem.id, oldItem.title)
            }
            val updated = oldItem.copy(
                title = newTitle,
                deadline = newDeadline,
                scheduledTime = newScheduled,
                timingPreset = preset,
                status = "active",
                initialEnabled = true
            )
            reminders[index] = updated
            if (context != null) {
                NotificationHelper.scheduleTaskReminder(context, updated)
            }
        }
    }

    fun completeTaskReminders(taskTitle: String, context: Context? = null) {
        reminders.forEachIndexed { index, item ->
            if (item.title.equals(taskTitle, ignoreCase = true) || item.id == taskTitle) {
                val updated = item.copy(status = "completed", initialEnabled = false)
                reminders[index] = updated
                if (context != null) {
                    NotificationHelper.cancelTaskReminder(context, item.id, item.title)
                }
            }
        }
    }

    fun toggleReminder(id: String, context: Context? = null) {
        val index = reminders.indexOfFirst { it.id == id }
        if (index != -1) {
            val current = reminders[index]
            val newStatus = !current.initialEnabled
            val newStatusStr = if (newStatus) "active" else "cancelled"
            reminders[index] = current.copy(initialEnabled = newStatus, status = newStatusStr)
            if (context != null) {
                if (newStatus) {
                    NotificationHelper.scheduleTaskReminder(context, reminders[index])
                } else {
                    NotificationHelper.cancelTaskReminder(context, current.id, current.title)
                }
            }
        }
    }

    fun removeReminder(id: String, context: Context? = null) {
        val reminder = reminders.find { it.id == id }
        if (reminder != null) {
            if (context != null) {
                NotificationHelper.cancelTaskReminder(context, reminder.id, reminder.title)
            }
            reminder.status = "deleted"
            reminders.remove(reminder)
        }
    }

    fun reschedulePendingReminders(context: Context) {
        reminders.filter { it.status == "active" && it.initialEnabled && !it.isPast }.forEach { item ->
            NotificationHelper.scheduleTaskReminder(context, item)
        }
    }

    fun getUpcomingReminders(): List<ReminderItemModel> = reminders.filter { !it.isPast && it.status == "active" }

    fun getPastReminders(): List<ReminderItemModel> = reminders.filter { it.isPast && it.status == "active" }

    fun getActiveCount(): Int = reminders.count { !it.isPast && it.initialEnabled && it.status == "active" }

    fun getScheduledCount(): Int = reminders.count { !it.isPast && it.status == "active" }
}
