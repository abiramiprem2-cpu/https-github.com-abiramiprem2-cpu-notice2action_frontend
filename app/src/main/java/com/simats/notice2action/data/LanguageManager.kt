package com.simats.notice2action.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class LanguageOption(
    val code: String,
    val nameInEnglish: String,
    val nativeName: String
)

object LanguageManager {
    private const val PREF_NAME = "notice2action_language"
    private const val KEY_LANG = "selected_language"

    val supportedLanguages = listOf(
        LanguageOption("en", "English", "English"),
        LanguageOption("ta", "Tamil", "தமிழ்"),
        LanguageOption("ml", "Malayalam", "മലയാളം"),
        LanguageOption("te", "Telugu", "తెలుగు"),
        LanguageOption("hi", "Hindi", "हिन्दी"),
        LanguageOption("kn", "Kannada", "ಕನ್ನಡ")
    )

    var currentLanguage: String by mutableStateOf("en")

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        currentLanguage = prefs.getString(KEY_LANG, "en") ?: "en"
    }

    fun setLanguage(context: Context, langCode: String) {
        currentLanguage = langCode
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANG, langCode).commit()
    }

    fun getDisplayName(code: String = currentLanguage): String {
        val option = supportedLanguages.firstOrNull { it.code == code } ?: return "English"
        return if (option.code == "en") {
            option.nameInEnglish
        } else {
            "${option.nameInEnglish} (${option.nativeName})"
        }
    }
}
