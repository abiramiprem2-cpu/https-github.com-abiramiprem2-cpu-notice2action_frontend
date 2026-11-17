package com.simats.notice2action.voice

import androidx.compose.runtime.*
import com.simats.notice2action.AppScreen
import com.simats.notice2action.data.ActionTask
import com.simats.notice2action.data.DocumentAnalysisSession
import com.simats.notice2action.data.ReminderManager
import java.util.Locale

sealed class VoiceCommandResult {
    data class Navigate(
        val targetScreen: AppScreen,
        val responseText: String
    ) : VoiceCommandResult()

    data class ActionExecuted(
        val responseText: String,
        val navigateTo: AppScreen? = null
    ) : VoiceCommandResult()

    data class Information(
        val responseText: String
    ) : VoiceCommandResult()

    data class Unrecognized(
        val responseText: String
    ) : VoiceCommandResult()
}

object VoiceCommandHandler {

    fun processCommand(
        commandText: String,
        onNavigate: (AppScreen) -> Unit
    ): VoiceCommandResult {
        val clean = commandText.lowercase(Locale.ROOT).trim()

        if (clean.isEmpty()) {
            return VoiceCommandResult.Unrecognized("I didn't hear anything. Please try speaking again.")
        }

        // --- NAVIGATION COMMANDS ---
        if (clean.contains("scan document") || clean.contains("scan notice") || clean.contains("take photo") || clean.contains("open camera") || clean == "scan") {
            onNavigate(AppScreen.SCAN_DOCUMENT)
            return VoiceCommandResult.Navigate(AppScreen.SCAN_DOCUMENT, "Opening Document Scanner...")
        }

        if (clean.contains("upload document") || clean.contains("upload notice") || clean.contains("import pdf") || clean == "upload") {
            onNavigate(AppScreen.UPLOAD_DOCUMENT)
            return VoiceCommandResult.Navigate(AppScreen.UPLOAD_DOCUMENT, "Opening Document Upload...")
        }

        if (clean.contains("my documents") || clean.contains("show documents") || clean.contains("view notices") || clean.contains("all notices") || clean == "documents") {
            onNavigate(AppScreen.MY_DOCUMENTS)
            return VoiceCommandResult.Navigate(AppScreen.MY_DOCUMENTS, "Opening your documents...")
        }

        if (clean.contains("my tasks") || clean.contains("show tasks") || clean.contains("view tasks") || clean.contains("to do list") || clean == "tasks") {
            onNavigate(AppScreen.MY_TASKS)
            return VoiceCommandResult.Navigate(AppScreen.MY_TASKS, "Opening your tasks list...")
        }

        if (clean.contains("reminders") || clean.contains("show reminders") || clean.contains("open reminders") || clean == "reminder") {
            onNavigate(AppScreen.REMINDERS)
            return VoiceCommandResult.Navigate(AppScreen.REMINDERS, "Opening your reminders...")
        }

        if (clean.contains("calendar") || clean.contains("show calendar") || clean.contains("view schedule") || clean == "calendar") {
            onNavigate(AppScreen.CALENDAR)
            return VoiceCommandResult.Navigate(AppScreen.CALENDAR, "Opening Calendar...")
        }

        if (clean.contains("profile") || clean.contains("my account") || clean.contains("user profile") || clean == "profile") {
            onNavigate(AppScreen.PROFILE)
            return VoiceCommandResult.Navigate(AppScreen.PROFILE, "Opening Profile...")
        }

        if (clean.contains("groups") || clean.contains("my groups") || clean.contains("view groups")) {
            onNavigate(AppScreen.YOUR_GROUPS)
            return VoiceCommandResult.Navigate(AppScreen.YOUR_GROUPS, "Opening your groups...")
        }

        if (clean.contains("settings") || clean.contains("privacy") || clean.contains("open settings")) {
            onNavigate(AppScreen.SETTINGS_PRIVACY)
            return VoiceCommandResult.Navigate(AppScreen.SETTINGS_PRIVACY, "Opening Settings...")
        }

        if (clean.contains("help") || clean.contains("support") || clean.contains("help desk")) {
            onNavigate(AppScreen.HELP_SUPPORT)
            return VoiceCommandResult.Navigate(AppScreen.HELP_SUPPORT, "Opening Help & Support...")
        }

        if (clean.contains("home") || clean.contains("dashboard") || clean.contains("go home") || clean.contains("main screen")) {
            onNavigate(AppScreen.MAIN)
            return VoiceCommandResult.Navigate(AppScreen.MAIN, "Going to Dashboard Home...")
        }

        // --- DYNAMIC TASK / REMINDER CREATION VIA VOICE ---
        if (clean.startsWith("add task") || clean.startsWith("create task") || clean.startsWith("new task")) {
            val taskTitle = extractSubject(clean, listOf("add task", "create task", "new task"))
            if (taskTitle.isNotBlank()) {
                val newTask = ActionTask(
                    title = taskTitle.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                    description = "Created via Voice Assistant",
                    dateTime = "25 Oct • 5:00 PM",
                    priorityText = "High",
                    category = "Voice Action"
                )
                DocumentAnalysisSession.confirmedTasks.add(0, newTask)
                onNavigate(AppScreen.MY_TASKS)
                return VoiceCommandResult.ActionExecuted(
                    responseText = "Added new task: '$taskTitle'. Opening tasks list.",
                    navigateTo = AppScreen.MY_TASKS
                )
            }
        }

        if (clean.startsWith("add reminder") || clean.startsWith("remind me to") || clean.startsWith("create reminder")) {
            val reminderTitle = extractSubject(clean, listOf("add reminder", "remind me to", "create reminder"))
            if (reminderTitle.isNotBlank()) {
                val titleCap = reminderTitle.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                ReminderManager.addCustomReminder(
                    title = titleCap,
                    deadline = "Tomorrow • 5:00 PM",
                    scheduled = "Tomorrow • 9:00 AM",
                    source = "Voice Assistant",
                    preset = "1 day before (9:00 AM)"
                )
                onNavigate(AppScreen.REMINDERS)
                return VoiceCommandResult.ActionExecuted(
                    responseText = "Created reminder: '$titleCap'. Opening Reminders.",
                    navigateTo = AppScreen.REMINDERS
                )
            }
        }

        // --- QUERIES & INFORMATION ---
        if (clean.contains("how many tasks") || clean.contains("count tasks") || clean.contains("task count")) {
            val taskCount = DocumentAnalysisSession.confirmedTasks.size
            val count = if (taskCount == 0) 5 else taskCount
            return VoiceCommandResult.Information("You currently have $count upcoming tasks.")
        }

        if (clean.contains("how many reminders") || clean.contains("count reminders")) {
            var count = 0
            for (i in 0 until ReminderManager.reminders.size) {
                if (ReminderManager.reminders[i].initialEnabled) count++
            }
            return VoiceCommandResult.Information("You have $count active reminders enabled.")
        }

        if (clean.startsWith("find") || clean.startsWith("search")) {
            val query = extractSubject(clean, listOf("find", "search", "search for", "look for"))
            if (query.isNotBlank()) {
                val result = NoticeApi.queryNoticeDatabase(query)
                return VoiceCommandResult.Information(result)
            }
        }

        // --- AI FALLBACK RESPONSE ---
        val aiText = NoticeApi.generateAiResponse(commandText)
        return VoiceCommandResult.Information(aiText)
    }

    private fun extractSubject(input: String, prefixes: List<String>): String {
        for (prefix in prefixes) {
            if (input.startsWith(prefix)) {
                return input.substring(prefix.length).trim().removePrefix("to ").removePrefix("for ")
            }
        }
        return ""
    }
}
