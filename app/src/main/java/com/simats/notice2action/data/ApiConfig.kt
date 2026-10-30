package com.simats.notice2action.data

/**
 * Configuration for Notice2Action backend API.
 *
 * For standard Android Emulator, use: "http://10.0.2.2/notice2action_backend"
 * For physical devices connected via Wi-Fi/LAN, use your computer's IP address:
 * e.g. "http://172.23.176.105/notice2action_backend"
 */
object ApiConfig {
    var BASE_URL: String = "https://w84dmqgh-80.inc1.devtunnels.ms/notice2action_backend"

    fun getFullImageUrl(imagePath: String?): String? {
        if (imagePath.isNullOrBlank()) return null
        return if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            imagePath
        } else {
            "$BASE_URL/$imagePath"
        }
    }
}

