package com.simats.notice2action.data

import androidx.compose.runtime.mutableStateListOf
import java.util.UUID

data class ReminderItemModel(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val scheduledTime: String,
    val deadline: String,
    var initialEnabled: Boolean = true,
    val isPast: Boolean = false,
    val sourceDocument: String = "Notice Document",
    val timingPreset: String = "1 day before (9:00 AM)"
)

object ReminderManager {
    // Initial dynamic state seeded with default action reminders
    val reminders = mutableStateListOf<ReminderItemModel>(
        ReminderItemModel(
            id = "1",
            title = "Submit project proposal and abstract",
            scheduledTime = "19 Oct 2026 • 9:00 AM",
            deadline = "20 Oct • 5:00 PM",
            initialEnabled = true,
            isPast = false,
            sourceDocument = "Academic Affairs Notice",
            timingPreset = "1 day before (9:00 AM)"
        ),
        ReminderItemModel(
            id = "2",
            title = "Attend team briefing session",
            scheduledTime = "22 Oct 2026 • 9:00 AM",
            deadline = "22 Oct • 10:00 AM",
            initialEnabled = true,
            isPast = false,
            sourceDocument = "Academic Affairs Notice",
            timingPreset = "1 hour before"
        ),
        ReminderItemModel(
            id = "3",
            title = "Submit expense report & dues",
            scheduledTime = "22 Oct 2026 • 9:00 AM",
            deadline = "23 Oct • 6:00 PM",
            initialEnabled = false,
            isPast = false,
            sourceDocument = "Finance & Accounts Notice",
            timingPreset = "1 day before (9:00 AM)"
        ),
        ReminderItemModel(
            id = "4",
            title = "Innovation Week Orientation",
            scheduledTime = "12 Oct 2026 • 10:00 AM",
            deadline = "12 Oct • 11:30 AM",
            initialEnabled = true,
            isPast = true,
            sourceDocument = "Academic Affairs Notice",
            timingPreset = "At event"
        )
    )

    fun addFromActionTask(
        task: ActionTask,
        documentTitle: String,
        preset: String = "1 day before (9:00 AM)"
    ): ReminderItemModel {
        // Prevent duplicate reminders for the same task
        val existing = reminders.find { it.title.equals(task.title, ignoreCase = true) }
        if (existing != null) {
            existing.initialEnabled = true
            return existing
        }

        val calculatedScheduled = if (task.dateTime.contains("•")) {
            val parts = task.dateTime.split("•")
            "${parts.first().trim()} • 9:00 AM"
        } else {
            "${task.dateTime} • 9:00 AM"
        }

        val newReminder = ReminderItemModel(
            id = UUID.randomUUID().toString(),
            title = task.title,
            scheduledTime = calculatedScheduled,
            deadline = task.dateTime,
            initialEnabled = true,
            isPast = false,
            sourceDocument = documentTitle,
            timingPreset = preset
        )
        reminders.add(0, newReminder)
        return newReminder
    }

    fun isReminderActive(taskTitle: String): Boolean {
        return reminders.any { it.title.equals(taskTitle, ignoreCase = true) && it.initialEnabled }
    }

    fun addCustomReminder(
        title: String,
        deadline: String,
        scheduled: String,
        source: String = "Custom Reminder",
        preset: String = "1 day before (9:00 AM)"
    ) {
        val newReminder = ReminderItemModel(
            id = UUID.randomUUID().toString(),
            title = title,
            scheduledTime = scheduled,
            deadline = deadline,
            initialEnabled = true,
            isPast = false,
            sourceDocument = source,
            timingPreset = preset
        )
        reminders.add(0, newReminder)
    }

    fun toggleReminder(id: String) {
        val index = reminders.indexOfFirst { it.id == id }
        if (index != -1) {
            val current = reminders[index]
            reminders[index] = current.copy(initialEnabled = !current.initialEnabled)
        }
    }

    fun removeReminder(id: String) {
        reminders.removeAll { it.id == id }
    }

    fun getUpcomingReminders(): List<ReminderItemModel> = reminders.filter { !it.isPast }

    fun getPastReminders(): List<ReminderItemModel> = reminders.filter { it.isPast }

    fun getActiveCount(): Int = reminders.count { !it.isPast && it.initialEnabled }

    fun getScheduledCount(): Int = reminders.count { !it.isPast }
}

