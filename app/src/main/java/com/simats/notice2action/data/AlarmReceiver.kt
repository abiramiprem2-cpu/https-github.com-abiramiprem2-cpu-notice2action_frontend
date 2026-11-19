package com.simats.notice2action.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra("reminderId") ?: ""
        val title = intent.getStringExtra("title") ?: "Task Deadline Reminder"
        val scheduledTime = intent.getStringExtra("scheduledTime") ?: "Upcoming Deadline"
        val source = intent.getStringExtra("sourceDocument") ?: "Notice Document"

        Log.d("AlarmReceiver", "Alarm triggered for reminderId: $reminderId, title: $title, scheduledTime: $scheduledTime")

        // Ensure notification channel is created
        NotificationHelper.createNotificationChannel(context)

        // Show the reminder notification with high priority, sound, and vibration
        NotificationHelper.showTaskReminderNotification(context, title, scheduledTime, source, reminderId)
    }
}
