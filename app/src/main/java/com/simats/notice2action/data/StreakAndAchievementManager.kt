package com.simats.notice2action.data

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class BadgeItem(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val isUnlocked: Boolean,
    val category: String = "Productivity"
)

object StreakAndAchievementManager {
    private const val PREFS_NAME = "notice2action_streaks_v1"
    private const val KEY_STREAK_COUNT = "streak_count"
    private const val KEY_LAST_ACTIVE_DATE = "last_active_date"
    private const val KEY_COMPLETED_TASKS = "completed_tasks_count"
    private const val KEY_TOTAL_SCANNED = "total_scanned_count"
    private const val KEY_DOC_SECURED = "doc_secured_unlocked"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    private fun getYesterdayDateString(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(cal.time)
    }

    fun getCurrentStreak(context: Context): Int {
        val prefs = getPrefs(context)
        val lastDate = prefs.getString(KEY_LAST_ACTIVE_DATE, null) ?: return 1
        val today = getTodayDateString()
        val yesterday = getYesterdayDateString()

        val streak = prefs.getInt(KEY_STREAK_COUNT, 1)
        return when (lastDate) {
            today, yesterday -> streak
            else -> 1 // Reset if gap is more than 1 day
        }
    }

    fun getCompletedTasksCount(context: Context): Int {
        return getPrefs(context).getInt(KEY_COMPLETED_TASKS, 0)
    }

    fun recordTaskCompleted(context: Context) {
        val prefs = getPrefs(context)
        val currentCount = prefs.getInt(KEY_COMPLETED_TASKS, 0) + 1
        val today = getTodayDateString()
        val yesterday = getYesterdayDateString()
        val lastDate = prefs.getString(KEY_LAST_ACTIVE_DATE, null)
        var streak = prefs.getInt(KEY_STREAK_COUNT, 1)

        if (lastDate == yesterday) {
            streak += 1
        } else if (lastDate != today) {
            streak = 1
        }

        prefs.edit()
            .putInt(KEY_COMPLETED_TASKS, currentCount)
            .putInt(KEY_STREAK_COUNT, streak)
            .putString(KEY_LAST_ACTIVE_DATE, today)
            .apply()

        // Check for newly unlocked milestone badges
        if (currentCount == 1) {
            Toast.makeText(context, "🏆 Achievement Unlocked: First Action Completed!", Toast.LENGTH_SHORT).show()
        } else if (currentCount == 5) {
            Toast.makeText(context, "⚡ Achievement Unlocked: Productivity Pro (5 Tasks)!", Toast.LENGTH_SHORT).show()
        }
    }

    fun recordTaskUncompleted(context: Context) {
        val prefs = getPrefs(context)
        val currentCount = (prefs.getInt(KEY_COMPLETED_TASKS, 0) - 1).coerceAtLeast(0)
        prefs.edit().putInt(KEY_COMPLETED_TASKS, currentCount).apply()
    }

    fun recordNoticeScanned(context: Context) {
        val prefs = getPrefs(context)
        val current = prefs.getInt(KEY_TOTAL_SCANNED, 0) + 1
        prefs.edit().putInt(KEY_TOTAL_SCANNED, current).apply()

        if (current == 1) {
            Toast.makeText(context, "🌟 Badge Unlocked: Notice Scanner!", Toast.LENGTH_SHORT).show()
        }
    }

    fun recordDocumentSecured(context: Context) {
        val prefs = getPrefs(context)
        prefs.edit().putBoolean(KEY_DOC_SECURED, true).apply()
    }

    fun getAllBadges(context: Context): List<BadgeItem> {
        val prefs = getPrefs(context)
        val completed = prefs.getInt(KEY_COMPLETED_TASKS, 0)
        val streak = getCurrentStreak(context)
        val scanned = prefs.getInt(KEY_TOTAL_SCANNED, 0)
        val isSecured = prefs.getBoolean(KEY_DOC_SECURED, false)

        return listOf(
            BadgeItem(
                id = "first_step",
                title = "First Step",
                description = "Completed your first action from an official notice.",
                emoji = "🚀",
                isUnlocked = completed >= 1
            ),
            BadgeItem(
                id = "streak_warrior",
                title = "Streak Champion",
                description = "Maintained an active task resolution streak of 2+ days.",
                emoji = "🔥",
                isUnlocked = streak >= 2
            ),
            BadgeItem(
                id = "productivity_pro",
                title = "Action Master",
                description = "Successfully resolved 5 or more notice checklist tasks.",
                emoji = "⚡",
                isUnlocked = completed >= 5
            ),
            BadgeItem(
                id = "document_vault",
                title = "Document Guardian",
                description = "Protected sensitive notice records with document security.",
                emoji = "🛡️",
                isUnlocked = isSecured
            ),
            BadgeItem(
                id = "notice_prodigy",
                title = "Notice Archivist",
                description = "Scanned and indexed multiple official notices & circulars.",
                emoji = "🌟",
                isUnlocked = scanned >= 3 || completed >= 3
            ),
            BadgeItem(
                id = "zero_overdue",
                title = "Zero Overdue",
                description = "Kept all task deadlines on schedule without late penalties.",
                emoji = "🎯",
                isUnlocked = true
            )
        )
    }
}
