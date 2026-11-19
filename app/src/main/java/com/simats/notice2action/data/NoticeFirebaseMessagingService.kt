package com.simats.notice2action.data

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NoticeFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Notice2Action_FCM", "New Firebase Cloud Messaging Token: $token")
        try {
            val prefs = getSharedPreferences("notice2action_firebase", Context.MODE_PRIVATE)
            prefs.edit().putString("fcm_token", token).apply()
        } catch (_: Exception) {}
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("Notice2Action_FCM", "Message received from: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "Notice2Action Reminder"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: remoteMessage.data["message"]
            ?: "You have an upcoming task deadline."

        val reminderId = remoteMessage.data["reminderId"] ?: ""
        val scheduledTime = remoteMessage.data["scheduledTime"] ?: body
        val source = remoteMessage.data["sourceDocument"] ?: "Firebase Notice"

        // Display the notification in the Android system tray
        NotificationHelper.createNotificationChannel(applicationContext)
        NotificationHelper.showTaskReminderNotification(
            context = applicationContext,
            taskTitle = title,
            scheduledTime = scheduledTime,
            sourceDocument = source,
            reminderId = reminderId
        )
    }
}

