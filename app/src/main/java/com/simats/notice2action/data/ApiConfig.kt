package com.simats.notice2action.data

/**
 * Configuration for Notice2Action backend API.
 * Automatically connects to emulator loopback, local Wi-Fi, or remote tunnels.
 */
object ApiConfig {
    val FALLBACK_URLS = listOf(
        "http://10.0.2.2/notice2action_backend",
        "http://172.23.176.105/notice2action_backend",
        "https://w84dmqgh-80.inc1.devtunnels.ms/notice2action_backend"
    )

    var BASE_URL: String = "http://10.0.2.2/notice2action_backend"

    fun getFullImageUrl(imagePath: String?): String? {
        if (imagePath.isNullOrBlank()) return null
        return if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            imagePath
        } else {
            "$BASE_URL/$imagePath"
        }
    }
}
