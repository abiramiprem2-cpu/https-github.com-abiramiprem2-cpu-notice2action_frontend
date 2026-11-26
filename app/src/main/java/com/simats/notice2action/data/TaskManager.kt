package com.simats.notice2action.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

object TaskManager {
    private const val PREF_NAME = "notice2action_tasks_pref"
    private const val KEY_TASKS_JSON = "saved_tasks_json"

    val tasks = mutableStateListOf<ActionTask>()
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized && tasks.isNotEmpty()) return
        isInitialized = true

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_TASKS_JSON, null)

        tasks.clear()

        if (!jsonStr.isNullOrBlank()) {
            try {
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    tasks.add(parseTaskFromJson(obj))
                }
            } catch (_: Exception) {}
        }

        if (tasks.isEmpty()) {
            // Seed starter tasks
            tasks.addAll(createStarterTasks())
            saveToPrefs(context)
        }

        syncWithBackend(context)
    }

    fun addTask(context: Context, task: ActionTask, documentTitle: String = "Notice Document") {
        val existingIndex = tasks.indexOfFirst { it.id == task.id || it.title.equals(task.title, ignoreCase = true) }
        val enrichedTask = if (task.documentTitle.isBlank()) task.copy(documentTitle = documentTitle) else task

        if (existingIndex != -1) {
            tasks[existingIndex] = enrichedTask
        } else {
            tasks.add(0, enrichedTask)
        }

        saveToPrefs(context)

        // Automatically create reminder
        ReminderManager.addFromActionTask(enrichedTask, documentTitle, context = context)

        // Asynchronously save to backend
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val body = JSONObject().apply {
                    put("id", enrichedTask.id)
                    put("user_id", UserSession.currentUser?.id ?: 1)
                    put("title", enrichedTask.title)
                    put("description", enrichedTask.description)
                    put("dateTime", enrichedTask.dateTime)
                    put("priority", enrichedTask.priorityText)
                    put("status", if (enrichedTask.isCompleted) "completed" else "pending")
                    put("category", enrichedTask.category)
                    val checklistArr = JSONArray()
                    enrichedTask.checklistItems.forEach { checklistArr.put(it) }
                    put("checklist", checklistArr)
                }
                ApiClient.postJson("tasks/save_task.php", body)
            } catch (_: Exception) {}
        }
    }

    fun toggleTaskCompletion(context: Context, taskId: String) {
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val oldTask = tasks[index]
            val newCompleted = !oldTask.isCompleted
            val updated = oldTask.copy(
                isCompleted = newCompleted,
                statusText = if (newCompleted) "Completed" else "Pending"
            )
            tasks[index] = updated
            saveToPrefs(context)

            if (newCompleted) {
                ReminderManager.completeTaskReminders(updated.title, context)
            }

            // Sync with backend
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val body = JSONObject().apply {
                        put("task_id", taskId)
                        put("status", if (newCompleted) "completed" else "pending")
                    }
                    ApiClient.postJson("tasks/update_task_status.php", body)
                } catch (_: Exception) {}
            }
        }
    }

    fun deleteTask(context: Context, taskId: String) {
        val task = tasks.find { it.id == taskId }
        if (task != null) {
            tasks.remove(task)
            saveToPrefs(context)
            ReminderManager.removeReminder(task.id, context)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val body = JSONObject().apply { put("task_id", taskId) }
                    ApiClient.postJson("tasks/delete_task.php", body)
                } catch (_: Exception) {}
            }
        }
    }

    fun getPendingTasks(): List<ActionTask> = tasks.filter { !it.isCompleted }

    fun getCompletedTasks(): List<ActionTask> = tasks.filter { it.isCompleted }

    fun getProgressForDocument(docTitle: String): Pair<Int, Int> {
        val docTasks = tasks.filter { it.documentTitle.equals(docTitle, ignoreCase = true) }
        val total = if (docTasks.isNotEmpty()) docTasks.size else 1
        val completed = docTasks.count { it.isCompleted }
        return Pair(completed, total)
    }

    private fun saveToPrefs(context: Context) {
        try {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val array = JSONArray()
            tasks.forEach { t ->
                val obj = JSONObject().apply {
                    put("id", t.id)
                    put("title", t.title)
                    put("description", t.description)
                    put("dateTime", t.dateTime)
                    put("dateNum", t.dateNum)
                    put("dateMonth", t.dateMonth)
                    put("daysLeft", t.daysLeft)
                    put("priorityText", t.priorityText)
                    put("statusText", t.statusText)
                    put("quote", t.quote)
                    put("category", t.category)
                    put("isConfirmed", t.isConfirmed)
                    put("isCompleted", t.isCompleted)
                    put("reminderTime", t.reminderTime)
                    put("documentTitle", t.documentTitle)
                    val clArr = JSONArray()
                    t.checklistItems.forEach { clArr.put(it) }
                    put("checklistItems", clArr)
                }
                array.put(obj)
            }
            prefs.edit().putString(KEY_TASKS_JSON, array.toString()).apply()
        } catch (_: Exception) {}
    }

    private fun parseTaskFromJson(obj: JSONObject): ActionTask {
        val clList = mutableListOf<String>()
        val clArr = obj.optJSONArray("checklistItems") ?: obj.optJSONArray("checklist")
        if (clArr != null) {
            for (k in 0 until clArr.length()) {
                clList.add(clArr.getString(k))
            }
        }

        return ActionTask(
            id = obj.optString("id", UUID.randomUUID().toString()),
            title = obj.optString("title", ""),
            description = obj.optString("description", ""),
            dateTime = obj.optString("dateTime", ""),
            dateNum = obj.optString("dateNum", "20"),
            dateMonth = obj.optString("dateMonth", "NOV"),
            daysLeft = obj.optString("daysLeft", "Due Soon"),
            priorityText = obj.optString("priorityText", "High"),
            statusText = obj.optString("statusText", "Verified date"),
            quote = obj.optString("quote", ""),
            category = obj.optString("category", "Academic"),
            isConfirmed = obj.optBoolean("isConfirmed", true),
            isCompleted = obj.optBoolean("isCompleted", false),
            reminderTime = obj.optString("reminderTime", "19 Oct 2026 • 9:00 AM"),
            checklistItems = clList,
            documentTitle = obj.optString("documentTitle", "Notice Document")
        )
    }

    private fun syncWithBackend(context: Context) {
        val userId = UserSession.currentUser?.id ?: 1
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val (_, json) = ApiClient.get("tasks/fetch_tasks.php?user_id=$userId")
                if (json.optBoolean("success", false)) {
                    val arr = json.optJSONArray("tasks")
                    if (arr != null && arr.length() > 0) {
                        for (i in 0 until arr.length()) {
                            val tObj = arr.getJSONObject(i)
                            val tid = tObj.optString("id", "")
                            if (tasks.none { it.id == tid }) {
                                val parsed = parseTaskFromJson(tObj)
                                tasks.add(parsed)
                            }
                        }
                        saveToPrefs(context)
                    }
                }
            } catch (_: Exception) {}
        }
    }

    private fun createStarterTasks(): List<ActionTask> {
        return listOf(
            ActionTask(
                id = "task_init_1",
                title = "Submit project proposal",
                description = "Upload abstract and team details to the student portal.",
                dateTime = "20 Oct 2026 • 5:00 PM",
                dateNum = "20",
                dateMonth = "OCT",
                daysLeft = "Due Soon",
                priorityText = "High",
                statusText = "Verified date",
                quote = "\"Submit your project proposal by 20 October, 5:00 PM...\" • p.1",
                category = "Academic",
                isCompleted = false,
                documentTitle = "Innovation Week 2026",
                checklistItems = listOf("Form team with 3 peers", "Draft project abstract", "Submit before 5:00 PM")
            ),
            ActionTask(
                id = "task_init_2",
                title = "Attend team briefing",
                description = "Meet the project team at Innovation Lab B.",
                dateTime = "22 Oct 2026 • 10:00 AM",
                dateNum = "22",
                dateMonth = "OCT",
                daysLeft = "In 2 days",
                priorityText = "Medium",
                statusText = "Verified date",
                quote = "\"Attend the briefing on 22 October, 10:00 AM...\" • p.1",
                category = "Meeting",
                isCompleted = false,
                documentTitle = "Innovation Week 2026",
                checklistItems = listOf("Bring student ID card", "Prepare notebook")
            )
        )
    }
}
