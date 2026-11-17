package com.simats.notice2action.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra("reminderId") ?: ""
        val title = intent.getStringExtra("title") ?: "Task Deadline Reminder"
        val scheduledTime = intent.getStringExtra("scheduledTime") ?: "Upcoming Deadline"
        val source = intent.getStringExtra("sourceDocument") ?: "Notice Document"

        // Ensure notification channel is created
        NotificationHelper.createNotificationChannel(context)

        // Check if reminder is still active if reminderId is provided
        if (reminderId.isNotBlank()) {
            val isActive = ReminderManager.isReminderActiveById(reminderId)
            if (!isActive && ReminderManager.reminders.any { it.id == reminderId }) {
                return
            }
        }

        // Show the reminder notification with high priority, sound, and vibration
        NotificationHelper.showTaskReminderNotification(context, title, scheduledTime, source, reminderId)
    }
}
