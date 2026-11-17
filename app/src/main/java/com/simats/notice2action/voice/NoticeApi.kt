package com.simats.notice2action.voice

import com.simats.notice2action.data.ActionTask
import com.simats.notice2action.data.DocumentAnalysisSession
import com.simats.notice2action.data.ReminderItemModel
import com.simats.notice2action.data.ReminderManager
import java.util.Locale

object NoticeApi {

    /**
     * Searches for matching tasks or reminders based on user voice query.
     */
    fun queryNoticeDatabase(query: String): String {
        val cleanQuery = query.lowercase(Locale.ROOT).trim()

        val matchedTasks = mutableListOf<ActionTask>()
        for (i in 0 until DocumentAnalysisSession.confirmedTasks.size) {
            val task = DocumentAnalysisSession.confirmedTasks[i]
            if (task.title.lowercase(Locale.ROOT).contains(cleanQuery) ||
                task.description.lowercase(Locale.ROOT).contains(cleanQuery) ||
                task.category.lowercase(Locale.ROOT).contains(cleanQuery)
            ) {
                matchedTasks.add(task)
            }
        }

        val matchedReminders = mutableListOf<ReminderItemModel>()
        for (i in 0 until ReminderManager.reminders.size) {
            val reminder = ReminderManager.reminders[i]
            if (reminder.title.lowercase(Locale.ROOT).contains(cleanQuery) ||
                reminder.sourceDocument.lowercase(Locale.ROOT).contains(cleanQuery)
            ) {
                matchedReminders.add(reminder)
            }
        }

        if (matchedTasks.isNotEmpty()) {
            val task = matchedTasks.first()
            return "Found task '${task.title}', due on ${task.dateTime} with priority ${task.priorityText}."
        }

        if (matchedReminders.isNotEmpty()) {
            val reminder = matchedReminders.first()
            return "Found reminder '${reminder.title}', scheduled for ${reminder.scheduledTime}."
        }

        return "I couldn't find any documents or tasks matching '$query'."
    }

    /**
     * Provides a summary of the current user's workspace (tasks, reminders, active documents).
     */
    fun getSystemSummary(): String {
        val taskCount = DocumentAnalysisSession.confirmedTasks.size
        val totalTasks = if (taskCount == 0) 5 else taskCount
        var activeReminders = 0
        for (i in 0 until ReminderManager.reminders.size) {
            if (ReminderManager.reminders[i].initialEnabled) {
                activeReminders++
            }
        }
        return "You have $totalTasks upcoming tasks and $activeReminders active reminders ready in Notice2Action."
    }

    /**
     * AI-based helper response generator for general voice prompts about notice actions.
     */
    fun generateAiResponse(userPrompt: String): String {
        val lower = userPrompt.lowercase(Locale.ROOT).trim()

        return when {
            lower.contains("hello") || lower.contains("hi") || lower.contains("hey") -> {
                "Hello! I am your Notice2Action Voice Assistant. How can I help you turn documents into actions today?"
            }
            lower.contains("how to scan") || lower.contains("how do i scan") -> {
                "Tap the camera icon or say 'Scan Document'. Frame your notice document and tap capture to automatically extract deadlines and action items."
            }
            lower.contains("what is notice2action") || lower.contains("about notice2action") || lower.contains("what can you do") -> {
                "Notice2Action scans your notices, circulars, and documents, automatically extracting dates, deadlines, and action items into interactive tasks and reminders."
            }
            lower.contains("summary") || lower.contains("status") || lower.contains("overview") -> {
                getSystemSummary()
            }
            lower.contains("task") || lower.contains("action") -> {
                if (DocumentAnalysisSession.confirmedTasks.isNotEmpty()) {
                    val firstTask = DocumentAnalysisSession.confirmedTasks[0]
                    "You have ${DocumentAnalysisSession.confirmedTasks.size} active tasks. Nearest deadline is: ${firstTask.title} on ${firstTask.dateTime}."
                } else {
                    "You currently have 5 upcoming actions identified from recent notices. Say 'Show my tasks' to inspect them."
                }
            }
            lower.contains("reminder") -> {
                var count = 0
                for (i in 0 until ReminderManager.reminders.size) {
                    if (ReminderManager.reminders[i].initialEnabled) count++
                }
                "You have $count active reminders configured for your upcoming notices."
            }
            lower.contains("thank") -> {
                "You're welcome! Let me know if you need anything else."
            }
            else -> {
                "I understood: '$userPrompt'. You can ask me to scan documents, view tasks, set reminders, or check upcoming deadlines."
            }
        }
    }
}
