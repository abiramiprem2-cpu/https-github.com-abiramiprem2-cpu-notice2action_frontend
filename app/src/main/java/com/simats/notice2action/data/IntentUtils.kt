package com.simats.notice2action.data

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast

object IntentUtils {
    fun addToDeviceCalendar(
        context: Context,
        title: String,
        description: String,
        location: String = "Campus / Online"
    ) {
        try {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, title)
                putExtra(CalendarContract.Events.DESCRIPTION, description)
                putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis() + 3600 * 1000)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open calendar app: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareText(context: Context, title: String, content: String) {
        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, content)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Share via")
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not share: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareNotice(context: Context, doc: NoticeAnalysisResult) {
        val actionsFormatted = doc.actions.joinToString("\n") { "• ${it.title} (${it.dateTime})" }
        val body = """
            📢 Notice2Action: ${doc.documentTitle}
            🏛 Issued By: ${doc.issuingAuthority}
            📅 Primary Deadline: ${doc.primaryDeadline} (${doc.daysRemaining})
            
            📝 Summary:
            ${if (doc.simpleExplanation.isNotBlank()) doc.simpleExplanation else doc.keySummary}
            
            ✅ Action Items:
            ${if (actionsFormatted.isNotBlank()) actionsFormatted else "No actions listed"}
            
            Shared via Notice2Action AI
        """.trimIndent()
        shareText(context, doc.documentTitle, body)
    }

    fun shareTask(context: Context, task: ActionTask) {
        val body = """
            📌 Notice2Action Task: ${task.title}
            ⏰ Due Date: ${task.dateTime}
            ⚡ Priority: ${task.priorityText}
            📝 Description: ${task.description}
            📄 Document: ${task.documentTitle}
            
            Shared via Notice2Action AI
        """.trimIndent()
        shareText(context, task.title, body)
    }
}
