package com.simats.notice2action.data

object NoticeAiAssistant {
    fun answerQuestion(question: String, document: NoticeAnalysisResult): String {
        val q = question.lowercase().trim()

        return when {
            q.contains("deadline") || q.contains("due date") || q.contains("by when") || q.contains("last date") || q.contains("when") -> {
                "The primary deadline for '${document.documentTitle}' is ${document.primaryDeadline} (${document.daysRemaining}). Please make sure to complete all required submissions or steps beforehand."
            }
            q.contains("action") || q.contains("what should i do") || q.contains("to do") || q.contains("steps") || q.contains("task") -> {
                if (document.actions.isNotEmpty()) {
                    val list = document.actions.joinToString("\n") { "• ${it.title} (Due: ${it.dateTime})" }
                    "Here are the actions identified from this notice:\n$list"
                } else {
                    "No immediate action tasks were detected in this notice."
                }
            }
            q.contains("who") || q.contains("authority") || q.contains("issued") || q.contains("department") || q.contains("office") -> {
                val refStr = if (document.referenceNumber.isNotBlank()) " with reference number ${document.referenceNumber}" else ""
                "This document was issued by ${document.issuingAuthority}$refStr."
            }
            q.contains("fee") || q.contains("cost") || q.contains("amount") || q.contains("pay") || q.contains("fine") || q.contains("price") -> {
                val lines = document.rawOcrText.lines().filter { line ->
                    val lower = line.lowercase()
                    lower.contains("fee") || lower.contains("rs") || lower.contains("inr") || lower.contains("₹") || lower.contains("amount") || lower.contains("pay")
                }
                if (lines.isNotEmpty()) {
                    "Payment & fee details extracted from the notice:\n" + lines.take(3).joinToString("\n") { "• ${it.trim()}" }
                } else {
                    "No specific fee or monetary payment requirement was found in this document."
                }
            }
            q.contains("venue") || q.contains("where") || q.contains("location") || q.contains("room") || q.contains("hall") || q.contains("lab") -> {
                val lines = document.rawOcrText.lines().filter { line ->
                    val lower = line.lowercase()
                    lower.contains("venue") || lower.contains("hall") || lower.contains("room") || lower.contains("lab") || lower.contains("auditorium") || lower.contains("campus") || lower.contains("block")
                }
                if (lines.isNotEmpty()) {
                    "Venue/Location details from the notice:\n" + lines.take(3).joinToString("\n") { "• ${it.trim()}" }
                } else {
                    "The notice does not specify a separate physical venue; submissions and procedures are handled online or via department office."
                }
            }
            q.contains("summary") || q.contains("what is this") || q.contains("about") || q.contains("explain") || q.contains("purpose") -> {
                val exp = if (document.simpleExplanation.isNotBlank()) document.simpleExplanation else document.keySummary
                "Summary of this notice:\n$exp"
            }
            q.contains("tamil") -> {
                if (document.tamilExplanation.isNotBlank()) {
                    "தமிழ் விளக்கம்:\n${document.tamilExplanation}"
                } else {
                    "தமிழ் விளக்கம்: இந்த அறிவிப்பு ${document.documentTitle} பற்றியது. கடைசி தேதி: ${document.primaryDeadline}."
                }
            }
            else -> {
                val words = q.split(" ").filter { it.length > 3 }
                val matchingLines = document.rawOcrText.lines().filter { line ->
                    words.any { w -> line.contains(w, ignoreCase = true) }
                }
                if (matchingLines.isNotEmpty()) {
                    "Relevant excerpt from the document:\n" + matchingLines.take(3).joinToString("\n") { "• ${it.trim()}" }
                } else {
                    val fallback = if (document.simpleExplanation.isNotBlank()) document.simpleExplanation.take(150) else document.keySummary.take(150)
                    "Regarding '${document.documentTitle}', this is a ${document.category} notice issued by ${document.issuingAuthority}. Primary deadline is ${document.primaryDeadline}.\n\n$fallback..."
                }
            }
        }
    }

    suspend fun askNoticeAi(question: String, document: NoticeAnalysisResult, userId: Int = 1): String {
        return try {
            val body = org.json.JSONObject().apply {
                put("question", question)
                put("document_title", document.documentTitle)
                put("document_text", document.rawOcrText)
                put("user_id", userId)
            }
            val (_, json) = ApiClient.postJson("ai/ask_notice.php", body)
            if (json.optBoolean("success", false) && json.has("answer")) {
                json.getString("answer")
            } else {
                answerQuestion(question, document)
            }
        } catch (_: Exception) {
            answerQuestion(question, document)
        }
    }
}
