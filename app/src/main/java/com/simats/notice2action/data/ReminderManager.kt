package com.simats.notice2action.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
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
    private const val PREF_NAME = "notice2action_reminders_pref"
    private const val KEY_REMINDERS_JSON = "saved_reminders_json"

    val reminders = mutableStateListOf<ReminderItemModel>()
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized && reminders.isNotEmpty()) return
        isInitialized = true

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_REMINDERS_JSON, null)

        reminders.clear()

        if (!jsonStr.isNullOrBlank()) {
            try {
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    reminders.add(
                        ReminderItemModel(
                            id = obj.optString("id", UUID.randomUUID().toString()),
                            taskId = obj.optString("taskId", UUID.randomUUID().toString()),
                            userId = obj.optString("userId", "1"),
                            title = obj.optString("title", ""),
                            scheduledTime = obj.optString("scheduledTime", ""),
                            deadline = obj.optString("deadline", ""),
                            initialEnabled = obj.optBoolean("initialEnabled", true),
                            status = obj.optString("status", "active"),
                            isPast = obj.optBoolean("isPast", false),
                            sourceDocument = obj.optString("sourceDocument", "Notice Document"),
                            timingPreset = obj.optString("timingPreset", "1 day before (9:00 AM)")
                        )
                    )
                }
            } catch (_: Exception) {}
        }

        if (reminders.isEmpty()) {
            reminders.addAll(createStarterReminders())
            saveToPrefs(context)
        }
    }

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
                saveToPrefs(context)
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
            taskId = task.id,
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
            saveToPrefs(context)
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
            saveToPrefs(context)
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
                saveToPrefs(context)
            }
        }
    }

    fun completeTaskReminders(taskTitle: String, context: Context? = null) {
        var changed = false
        reminders.forEachIndexed { index, item ->
            if (item.title.equals(taskTitle, ignoreCase = true) || item.id == taskTitle) {
                val updated = item.copy(status = "completed", initialEnabled = false)
                reminders[index] = updated
                changed = true
                if (context != null) {
                    NotificationHelper.cancelTaskReminder(context, item.id, item.title)
                }
            }
        }
        if (changed && context != null) {
            saveToPrefs(context)
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
                saveToPrefs(context)
            }
        }
    }

    fun removeReminder(id: String, context: Context? = null) {
        val reminder = reminders.find { it.id == id || it.taskId == id }
        if (reminder != null) {
            if (context != null) {
                NotificationHelper.cancelTaskReminder(context, reminder.id, reminder.title)
            }
            reminder.status = "deleted"
            reminders.remove(reminder)
            if (context != null) {
                saveToPrefs(context)
            }
        }
    }

    fun reschedulePendingReminders(context: Context) {
        init(context)
        reminders.filter { it.status == "active" && it.initialEnabled && !it.isPast }.forEach { item ->
            NotificationHelper.scheduleTaskReminder(context, item)
        }
    }

    fun getUpcomingReminders(): List<ReminderItemModel> = reminders.filter { !it.isPast && it.status == "active" }

    fun getPastReminders(): List<ReminderItemModel> = reminders.filter { it.isPast && it.status == "active" }

    fun getActiveCount(): Int = reminders.count { !it.isPast && it.initialEnabled && it.status == "active" }

    fun getScheduledCount(): Int = reminders.count { !it.isPast && it.status == "active" }

    private fun saveToPrefs(context: Context) {
        try {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val array = JSONArray()
            reminders.forEach { r ->
                val obj = JSONObject().apply {
                    put("id", r.id)
                    put("taskId", r.taskId)
                    put("userId", r.userId)
                    put("title", r.title)
                    put("scheduledTime", r.scheduledTime)
                    put("deadline", r.deadline)
                    put("initialEnabled", r.initialEnabled)
                    put("status", r.status)
                    put("isPast", r.isPast)
                    put("sourceDocument", r.sourceDocument)
                    put("timingPreset", r.timingPreset)
                }
                array.put(obj)
            }
            prefs.edit().putString(KEY_REMINDERS_JSON, array.toString()).apply()

            // Sync with backend
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val body = JSONObject().apply {
                        put("user_id", UserSession.currentUser?.id ?: 1)
                        put("reminders", array)
                    }
                    ApiClient.postJson("reminders/sync_reminders.php", body)
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }

    private fun createStarterReminders(): List<ReminderItemModel> {
        return listOf(
            ReminderItemModel(
                id = "rem_init_1",
                taskId = "task_init_1",
                title = "Submit project proposal",
                scheduledTime = "19 Oct 2026 • 9:00 AM",
                deadline = "20 Oct 2026 • 5:00 PM",
                initialEnabled = true,
                status = "active",
                isPast = false,
                sourceDocument = "Innovation Week 2026",
                timingPreset = "1 day before (9:00 AM)"
            ),
            ReminderItemModel(
                id = "rem_init_2",
                taskId = "task_init_2",
                title = "Attend team briefing",
                scheduledTime = "21 Oct 2026 • 9:00 AM",
                deadline = "22 Oct 2026 • 10:00 AM",
                initialEnabled = true,
                status = "active",
                isPast = false,
                sourceDocument = "Innovation Week 2026",
                timingPreset = "1 day before (9:00 AM)"
            )
        )
    }
}
