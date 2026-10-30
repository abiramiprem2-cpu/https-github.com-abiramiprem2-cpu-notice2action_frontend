package com.simats.notice2action.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object ApiClient {
    suspend fun postJson(endpoint: String, json: JSONObject): Pair<Int, JSONObject> = withContext(Dispatchers.IO) {
        val cleanEndpoint = endpoint.trimStart('/')
        val url = URL("${ApiConfig.BASE_URL}/$cleanEndpoint")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            setRequestProperty("Accept", "application/json")
            connectTimeout = 10000
            readTimeout = 10000
            doOutput = true
        }

        try {
            OutputStreamWriter(conn.outputStream, "UTF-8").use { writer ->
                writer.write(json.toString())
                writer.flush()
            }

            val statusCode = conn.responseCode
            val stream = if (statusCode in 200..299) conn.inputStream else (conn.errorStream ?: conn.inputStream)
            val responseText = stream?.let {
                BufferedReader(InputStreamReader(it, "UTF-8")).use { r -> r.readText() }
            } ?: ""

            val responseJson = try {
                JSONObject(responseText.trim())
            } catch (e: Exception) {
                JSONObject().apply {
                    put("success", false)
                    put("message", if (responseText.isNotBlank()) responseText else "Server returned status $statusCode")
                }
            }

            Pair(statusCode, responseJson)
        } finally {
            conn.disconnect()
        }
    }

    suspend fun get(endpoint: String): Pair<Int, JSONObject> = withContext(Dispatchers.IO) {
        val cleanEndpoint = endpoint.trimStart('/')
        val url = URL("${ApiConfig.BASE_URL}/$cleanEndpoint")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("Accept", "application/json")
            connectTimeout = 10000
            readTimeout = 10000
        }

        try {
            val statusCode = conn.responseCode
            val stream = if (statusCode in 200..299) conn.inputStream else (conn.errorStream ?: conn.inputStream)
            val responseText = stream?.let {
                BufferedReader(InputStreamReader(it, "UTF-8")).use { r -> r.readText() }
            } ?: ""

            val responseJson = try {
                JSONObject(responseText.trim())
            } catch (e: Exception) {
                JSONObject().apply {
                    put("success", false)
                    put("message", if (responseText.isNotBlank()) responseText else "Server returned status $statusCode")
                }
            }

            Pair(statusCode, responseJson)
        } finally {
            conn.disconnect()
        }
    }
}

