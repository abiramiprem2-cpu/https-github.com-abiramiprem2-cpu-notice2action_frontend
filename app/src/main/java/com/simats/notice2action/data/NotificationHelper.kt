package com.simats.notice2action.data

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.simats.notice2action.MainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object NotificationHelper {
    const val CHANNEL_ID = "notice2action_reminders_channel"
    private const val CHANNEL_NAME = "Notice2Action Reminders"
    private const val CHANNEL_DESC = "Notifications for upcoming task deadlines and notice actions."

    var isSoundEnabled: Boolean = true
    var isVibrationEnabled: Boolean = true

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(isVibrationEnabled)
                if (isVibrationEnabled) {
                    vibrationPattern = longArrayOf(0, 500, 200, 500)
                }
                if (!isSoundEnabled) {
                    setSound(null, null)
                } else {
                    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val audioAttributes = AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                        .build()
                    setSound(defaultSoundUri, audioAttributes)
                }
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setShowBadge(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleTaskReminder(context: Context, reminder: ReminderItemModel) {
        if (reminder.status != "active" || !reminder.initialEnabled) return
        createNotificationChannel(context)

        val triggerAtMillis = calculateTriggerMillis(
            scheduledTime = reminder.scheduledTime,
            deadline = reminder.deadline,
            preset = reminder.timingPreset
        )

        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = "com.simats.notice2action.REMINDER_ALARM"
                putExtra("reminderId", reminder.id)
                putExtra("taskId", reminder.taskId)
                putExtra("title", reminder.title)
                putExtra("scheduledTime", reminder.scheduledTime)
                putExtra("sourceDocument", reminder.sourceDocument)
            }

            val requestCode = reminder.id.hashCode()
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }

            val delaySec = ((triggerAtMillis - System.currentTimeMillis()) / 1000).coerceAtLeast(0)
            if (delaySec in 1..60) {
                Toast.makeText(context, "🔔 Reminder scheduled in $delaySec seconds!", Toast.LENGTH_SHORT).show()
            } else {
                val formatted = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(triggerAtMillis))
                Toast.makeText(context, "🔔 Reminder set for $formatted", Toast.LENGTH_SHORT).show()
            }
        } catch (_: Exception) {
            Toast.makeText(context, "🔔 Scheduled: ${reminder.title}", Toast.LENGTH_SHORT).show()
        }
    }

    fun showTaskReminderNotification(
        context: Context,
        taskTitle: String,
        scheduledTime: String,
        sourceDocument: String,
        reminderId: String = ""
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("targetScreen", "MY_TASKS")
            putExtra("targetTaskTitle", taskTitle)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            if (reminderId.isNotBlank()) reminderId.hashCode() else taskTitle.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🔔 Task Reminder: $taskTitle")
            .setContentText("$scheduledTime • $sourceDocument")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Reminder set for '$taskTitle'. Scheduled at $scheduledTime ($sourceDocument)."
                )
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (isVibrationEnabled) {
            builder.setVibrate(longArrayOf(0, 500, 200, 500))
        } else {
            builder.setVibrate(longArrayOf(0))
        }

        if (isSoundEnabled) {
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            builder.setSound(defaultSoundUri)
        } else {
            builder.setSound(null)
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = if (reminderId.isNotBlank()) reminderId.hashCode() else taskTitle.hashCode()
        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (_: Exception) {}
    }

    fun cancelTaskReminder(context: Context, reminderId: String, taskTitle: String) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = "com.simats.notice2action.REMINDER_ALARM"
            }

            val pendingIntentById = PendingIntent.getBroadcast(
                context,
                reminderId.hashCode(),
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntentById != null) {
                alarmManager.cancel(pendingIntentById)
                pendingIntentById.cancel()
            }

            val pendingIntentByTitle = PendingIntent.getBroadcast(
                context,
                taskTitle.hashCode(),
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntentByTitle != null) {
                alarmManager.cancel(pendingIntentByTitle)
                pendingIntentByTitle.cancel()
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(reminderId.hashCode())
            notificationManager.cancel(taskTitle.hashCode())
        } catch (_: Exception) {}
    }

    fun calculateTriggerMillis(
        scheduledTime: String,
        deadline: String = "",
        preset: String = ""
    ): Long {
        val now = System.currentTimeMillis()

        // 1. If preset or scheduled is test mode
        if (scheduledTime.contains("test", ignoreCase = true) ||
            preset.contains("test", ignoreCase = true) ||
            preset.contains("10s", ignoreCase = true)
        ) {
            return now + 10_000L
        }

        // 2. Try parsing scheduledTime directly as an absolute datetime
        val directTime = parseDirectDateTime(scheduledTime)
        if (directTime != null && directTime > now) {
            return directTime
        }

        // 3. Try parsing deadline and adjusting by preset
        val targetDateStr = deadline.ifBlank { scheduledTime }
        val deadlineTime = parseDirectDateTime(targetDateStr)
        if (deadlineTime != null) {
            val cal = Calendar.getInstance().apply { timeInMillis = deadlineTime }
            val p = (preset.ifBlank { scheduledTime }).lowercase()
            when {
                p.contains("1 day before") -> {
                    cal.add(Calendar.DAY_OF_YEAR, -1)
                    cal.set(Calendar.HOUR_OF_DAY, 9)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                }
                p.contains("2 hours before") || p.contains("2 hr before") -> {
                    cal.add(Calendar.HOUR_OF_DAY, -2)
                }
                p.contains("1 hour before") || p.contains("1 hr before") -> {
                    cal.add(Calendar.HOUR_OF_DAY, -1)
                }
                p.contains("day of deadline") -> {
                    cal.set(Calendar.HOUR_OF_DAY, 9)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                }
                p.contains("at event") || p.contains("at deadline") -> {
                    // keep exact deadline time
                }
                else -> {
                    cal.add(Calendar.DAY_OF_YEAR, -1)
                    cal.set(Calendar.HOUR_OF_DAY, 9)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                }
            }
            if (cal.timeInMillis > now) {
                return cal.timeInMillis
            }
        }

        // 4. Fallback: if calculated time is in past or unparseable, set to 15 seconds from now so the user can verify
        return now + 15_000L
    }

    private fun parseDirectDateTime(raw: String): Long? {
        val cleaned = raw.replace("•", " ").replace("alert", "").trim()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val patterns = listOf(
            "dd MMM yyyy h:mm a",
            "dd MMM yyyy hh:mm a",
            "dd MMM yyyy HH:mm",
            "dd MMM yyyy",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd",
            "dd/MM/yyyy h:mm a",
            "dd/MM/yyyy",
            "MMM dd, yyyy h:mm a",
            "MMM dd, yyyy"
        )

        for (p in patterns) {
            try {
                val sdf = SimpleDateFormat(p, Locale.US)
                val d = sdf.parse(cleaned)
                if (d != null) return d.time
            } catch (_: Exception) {}
        }

        // Try adding current year if missing (e.g. "20 Oct 5:00 PM")
        val withYear = "$cleaned $currentYear"
        val patternsWithoutYear = listOf(
            "dd MMM h:mm a yyyy",
            "dd MMM yyyy"
        )
        for (p in patternsWithoutYear) {
            try {
                val sdf = SimpleDateFormat(p, Locale.US)
                val d = sdf.parse(withYear)
                if (d != null) return d.time
            } catch (_: Exception) {}
        }

        return null
    }

    fun parseScheduledTimeMillis(scheduledTime: String): Long {
        return calculateTriggerMillis(scheduledTime)
    }
}
