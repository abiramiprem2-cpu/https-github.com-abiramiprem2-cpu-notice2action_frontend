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
    private fun getCandidateBaseUrls(): List<String> {
        val list = mutableListOf(ApiConfig.BASE_URL)
        for (url in ApiConfig.FALLBACK_URLS) {
            if (!list.contains(url)) {
                list.add(url)
            }
        }
        return list
    }

    suspend fun postJson(endpoint: String, json: JSONObject): Pair<Int, JSONObject> = withContext(Dispatchers.IO) {
        val cleanEndpoint = endpoint.trimStart('/')
        val candidateUrls = getCandidateBaseUrls()
        var lastException: Exception? = null

        for (baseUrl in candidateUrls) {
            var conn: HttpURLConnection? = null
            try {
                val url = URL("$baseUrl/$cleanEndpoint")
                conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                    setRequestProperty("Accept", "application/json")
                    connectTimeout = 4000
                    readTimeout = 7000
                    doOutput = true
                }

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

                ApiConfig.BASE_URL = baseUrl
                return@withContext Pair(statusCode, responseJson)
            } catch (e: Exception) {
                lastException = e
            } finally {
                conn?.disconnect()
            }
        }

        Pair(500, JSONObject().apply {
            put("success", false)
            put("message", lastException?.localizedMessage ?: "Unable to connect to server")
        })
    }

    suspend fun get(endpoint: String): Pair<Int, JSONObject> = withContext(Dispatchers.IO) {
        val cleanEndpoint = endpoint.trimStart('/')
        val candidateUrls = getCandidateBaseUrls()
        var lastException: Exception? = null

        for (baseUrl in candidateUrls) {
            var conn: HttpURLConnection? = null
            try {
                val url = URL("$baseUrl/$cleanEndpoint")
                conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("Accept", "application/json")
                    connectTimeout = 4000
                    readTimeout = 7000
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

                ApiConfig.BASE_URL = baseUrl
                return@withContext Pair(statusCode, responseJson)
            } catch (e: Exception) {
                lastException = e
            } finally {
                conn?.disconnect()
            }
        }

        Pair(500, JSONObject().apply {
            put("success", false)
            put("message", lastException?.localizedMessage ?: "Unable to connect to server")
        })
    }
}
