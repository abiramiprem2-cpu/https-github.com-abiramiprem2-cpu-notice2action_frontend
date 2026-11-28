package com.simats.notice2action.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SyncManager {
    var isSyncing = false
    var lastSyncStatus = "⚡ Offline Ready • Synced"

    suspend fun syncAll(context: Context, userId: Int? = null) = withContext(Dispatchers.IO) {
        if (isSyncing) return@withContext
        isSyncing = true
        try {
            DocumentManager.init(context)
            TaskManager.init(context)
            ReminderManager.init(context)
            DocumentManager.syncWithBackend(context)
            TaskManager.syncWithBackend(context)
            lastSyncStatus = "⚡ All Data Synced • Offline Ready"
        } catch (_: Exception) {
            lastSyncStatus = "⚡ Offline Mode • Cached Locally"
        } finally {
            isSyncing = false
        }
    }
}
