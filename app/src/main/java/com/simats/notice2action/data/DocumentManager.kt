package com.simats.notice2action.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

object DocumentManager {
    private const val PREF_NAME = "notice2action_docs_pref"
    private const val KEY_DOCS_JSON = "saved_documents_json"

    val documents = mutableStateListOf<NoticeAnalysisResult>()
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized && documents.isNotEmpty()) return
        isInitialized = true

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_DOCS_JSON, null)

        documents.clear()

        if (!jsonStr.isNullOrBlank()) {
            try {
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    documents.add(parseDocFromJson(obj))
                }
            } catch (_: Exception) {}
        }

        // If no saved documents, create default starter documents for instant demonstration
        if (documents.isEmpty()) {
            val starterDoc = createStarterNotice()
            documents.add(starterDoc)
            saveToPrefs(context)
        }

        // Background sync with backend
        syncWithBackend(context)
    }

    fun addDocument(context: Context, result: NoticeAnalysisResult) {
        val existingIndex = documents.indexOfFirst { it.documentTitle.equals(result.documentTitle, ignoreCase = true) }
        if (existingIndex != -1) {
            documents[existingIndex] = result
        } else {
            documents.add(0, result)
        }

        saveToPrefs(context)

        // Asynchronously save to backend database
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val body = JSONObject().apply {
                    put("user_id", UserSession.currentUser?.id ?: 1)
                    put("document_title", result.documentTitle)
                    put("category", result.category)
                    put("simple_explanation", result.simpleExplanation)
                    put("key_summary", result.keySummary)
                    put("primary_deadline", result.primaryDeadline)
                    put("days_remaining", result.daysRemaining)
                    put("issuing_authority", result.issuingAuthority)
                    put("raw_ocr_text", result.rawOcrText)
                    val actionsArr = JSONArray()
                    result.actions.forEach { a ->
                        actionsArr.put(JSONObject().apply {
                            put("id", a.id)
                            put("title", a.title)
                            put("description", a.description)
                            put("dateTime", a.dateTime)
                            put("priorityText", a.priorityText)
                        })
                    }
                    put("actions", actionsArr)
                    put("is_locked", DocumentAnalysisSession.isDocumentLocked)
                }
                ApiClient.postJson("documents/save_document.php", body)
            } catch (_: Exception) {}
        }
    }

    fun deleteDocument(context: Context, title: String) {
        val doc = documents.find { it.documentTitle.equals(title, ignoreCase = true) }
        if (doc != null) {
            documents.remove(doc)
            saveToPrefs(context)
        }
    }

    private fun saveToPrefs(context: Context) {
        try {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val array = JSONArray()
            documents.forEach { doc ->
                val obj = JSONObject().apply {
                    put("documentTitle", doc.documentTitle)
                    put("category", doc.category)
                    put("categoryColorHex", doc.categoryColorHex)
                    put("simpleExplanation", doc.simpleExplanation)
                    put("key_summary", doc.keySummary)
                    put("primaryDeadline", doc.primaryDeadline)
                    put("daysRemaining", doc.daysRemaining)
                    put("issuingAuthority", doc.issuingAuthority)
                    put("referenceNumber", doc.referenceNumber)
                    put("rawOcrText", doc.rawOcrText)
                    put("tamilExplanation", doc.tamilExplanation)

                    val actionsArr = JSONArray()
                    doc.actions.forEach { act ->
                        actionsArr.put(JSONObject().apply {
                            put("id", act.id)
                            put("title", act.title)
                            put("description", act.description)
                            put("dateTime", act.dateTime)
                            put("dateNum", act.dateNum)
                            put("dateMonth", act.dateMonth)
                            put("daysLeft", act.daysLeft)
                            put("priorityText", act.priorityText)
                            put("statusText", act.statusText)
                            put("quote", act.quote)
                            put("category", act.category)
                            put("isConfirmed", act.isConfirmed)
                            put("isCompleted", act.isCompleted)
                        })
                    }
                    put("actions", actionsArr)
                }
                array.put(obj)
            }
            prefs.edit().putString(KEY_DOCS_JSON, array.toString()).apply()
        } catch (_: Exception) {}
    }

    private fun parseDocFromJson(obj: JSONObject): NoticeAnalysisResult {
        val actionsList = mutableListOf<ActionTask>()
        val actionsArr = obj.optJSONArray("actions")
        if (actionsArr != null) {
            for (j in 0 until actionsArr.length()) {
                val a = actionsArr.getJSONObject(j)
                actionsList.add(
                    ActionTask(
                        id = a.optString("id", UUID.randomUUID().toString()),
                        title = a.optString("title", ""),
                        description = a.optString("description", ""),
                        dateTime = a.optString("dateTime", ""),
                        dateNum = a.optString("dateNum", "20"),
                        dateMonth = a.optString("dateMonth", "NOV"),
                        daysLeft = a.optString("daysLeft", "Due Soon"),
                        priorityText = a.optString("priorityText", "Medium"),
                        statusText = a.optString("statusText", "Verified"),
                        quote = a.optString("quote", ""),
                        category = a.optString("category", "Academic"),
                        isConfirmed = a.optBoolean("isConfirmed", true),
                        isCompleted = a.optBoolean("isCompleted", false)
                    )
                )
            }
        }

        return NoticeAnalysisResult(
            documentTitle = obj.optString("documentTitle", "Notice Document"),
            category = obj.optString("category", "Academic"),
            categoryColorHex = obj.optLong("categoryColorHex", 0xFF2563EB),
            simpleExplanation = obj.optString("simpleExplanation", ""),
            keySummary = obj.optString("key_summary", ""),
            primaryDeadline = obj.optString("primaryDeadline", "Upcoming"),
            daysRemaining = obj.optString("daysRemaining", "Due Soon"),
            issuingAuthority = obj.optString("issuingAuthority", "Institution Authority"),
            referenceNumber = obj.optString("referenceNumber", "REF-2026"),
            rawOcrText = obj.optString("rawOcrText", ""),
            actions = actionsList,
            tamilExplanation = obj.optString("tamilExplanation", "")
        )
    }

    private fun syncWithBackend(context: Context) {
        val userId = UserSession.currentUser?.id ?: 1
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val (_, json) = ApiClient.get("documents/fetch_documents.php?user_id=$userId")
                if (json.optBoolean("success", false)) {
                    val docsArr = json.optJSONArray("documents")
                    if (docsArr != null && docsArr.length() > 0) {
                        for (i in 0 until docsArr.length()) {
                            val d = docsArr.getJSONObject(i)
                            val title = d.optString("document_title", "")
                            if (documents.none { it.documentTitle.equals(title, ignoreCase = true) }) {
                                val actions = mutableListOf<ActionTask>()
                                val actJson = d.optJSONArray("actions")
                                if (actJson != null) {
                                    for (k in 0 until actJson.length()) {
                                        val item = actJson.getJSONObject(k)
                                        actions.add(
                                            ActionTask(
                                                title = item.optString("title", "Action Item"),
                                                description = item.optString("description", ""),
                                                dateTime = item.optString("dateTime", "20 Oct 2026 • 5:00 PM"),
                                                priorityText = item.optString("priorityText", "High")
                                            )
                                        )
                                    }
                                }
                                val res = NoticeAnalysisResult(
                                    documentTitle = title,
                                    category = d.optString("category", "Academic"),
                                    simpleExplanation = d.optString("simple_explanation", ""),
                                    keySummary = d.optString("key_summary", ""),
                                    primaryDeadline = d.optString("primary_deadline", "20 Oct 2026"),
                                    daysRemaining = d.optString("days_remaining", "Due Soon"),
                                    issuingAuthority = d.optString("issuing_authority", "College Administration"),
                                    referenceNumber = "REF-2026-09",
                                    rawOcrText = d.optString("raw_ocr_text", ""),
                                    actions = actions
                                )
                                documents.add(res)
                            }
                        }
                        saveToPrefs(context)
                    }
                }
            } catch (_: Exception) {}
        }
    }

    private fun createStarterNotice(): NoticeAnalysisResult {
        val actions = listOf(
            ActionTask(
                title = "Submit project proposal",
                description = "Upload abstract and team details to the student portal.",
                dateTime = "20 Oct 2026 • 5:00 PM",
                dateNum = "20",
                dateMonth = "OCT",
                daysLeft = "Due Soon",
                priorityText = "High",
                statusText = "Verified date",
                quote = "\"Submit your project proposal by 20 October, 5:00 PM...\" • p.1",
                category = "Academic"
            ),
            ActionTask(
                title = "Attend team briefing",
                description = "Meet the project team at Innovation Lab B.",
                dateTime = "22 Oct 2026 • 10:00 AM",
                dateNum = "22",
                dateMonth = "OCT",
                daysLeft = "In 2 days",
                priorityText = "Medium",
                statusText = "Verified date",
                quote = "\"Attend the briefing on 22 October, 10:00 AM...\" • p.1",
                category = "Meeting"
            )
        )

        return NoticeAnalysisResult(
            documentTitle = "Innovation Week 2026",
            category = "Academic",
            categoryColorHex = 0xFF2563EB,
            simpleExplanation = "Innovation Week submission guidelines for student project proposals and lab briefings.",
            keySummary = "Official schedule outlining project proposal submission deadlines and compulsory briefing sessions.",
            primaryDeadline = "20 Oct 2026 • 5:00 PM",
            daysRemaining = "Due Soon",
            issuingAuthority = "Northbridge College • Dept of Engineering",
            referenceNumber = "REF-2026-IW04",
            rawOcrText = "Northbridge College Innovation Week 2026. Submit project proposal by 20 October, 5:00 PM. Team briefing on 22 October 10:00 AM at Innovation Lab B.",
            actions = actions,
            tamilExplanation = "இன்னோவேஷன் வாரம் 2026 திட்ட முன்மொழிவு சமர்ப்பிக்கும் வழிகாட்டுதல்கள்."
        )
    }
}
