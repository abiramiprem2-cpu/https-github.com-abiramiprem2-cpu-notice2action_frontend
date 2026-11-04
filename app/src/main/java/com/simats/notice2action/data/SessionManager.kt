package com.simats.notice2action.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages persistent user session and auto-login credentials
 * using Android SharedPreferences with synchronous commit().
 */
object SessionManager {
    private const val PREF_NAME = "notice2action_session"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_PASSWORD = "user_password"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_USER_PHONE = "user_phone"
    private const val KEY_USER_ADDRESS = "user_address"
    private const val KEY_IMAGE_PATH = "image_path"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveSession(
        context: Context,
        user: User,
        password: String? = null
    ) {
        val editor = getPrefs(context).edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putInt(KEY_USER_ID, user.id)
        val resolvedName = user.name.trim().ifBlank {
            user.email.substringBefore("@").replaceFirstChar { it.uppercase() }
        }
        editor.putString(KEY_USER_NAME, resolvedName)
        editor.putString(KEY_USER_EMAIL, user.email)
        editor.putString(KEY_USER_ROLE, user.role)
        editor.putString(KEY_USER_PHONE, user.phone)
        editor.putString(KEY_USER_ADDRESS, user.address)
        editor.putString(KEY_IMAGE_PATH, user.imagePath)
        if (!password.isNullOrBlank()) {
            editor.putString(KEY_USER_PASSWORD, password)
        }
        editor.commit()
    }

    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getSavedEmail(context: Context): String {
        return getPrefs(context).getString(KEY_USER_EMAIL, "") ?: ""
    }

    fun getSavedPassword(context: Context): String {
        return getPrefs(context).getString(KEY_USER_PASSWORD, "") ?: ""
    }

    fun getSavedUser(context: Context): User? {
        val prefs = getPrefs(context)
        val id = prefs.getInt(KEY_USER_ID, 0)
        val email = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        if (id == 0 || email.isBlank()) return null
        var name = prefs.getString(KEY_USER_NAME, "")?.trim() ?: ""
        if (name.isBlank() && email.isNotBlank()) {
            name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
        }
        return User(
            id = id,
            name = name,
            email = email,
            role = prefs.getString(KEY_USER_ROLE, "") ?: "",
            imagePath = prefs.getString(KEY_IMAGE_PATH, null),
            phone = prefs.getString(KEY_USER_PHONE, null),
            address = prefs.getString(KEY_USER_ADDRESS, null)
        )
    }

    fun logout(context: Context) {
        val email = getSavedEmail(context)
        val password = getSavedPassword(context)
        val editor = getPrefs(context).edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, false)
        // Keep credentials saved so re-login is automatic or single-tap
        editor.putString(KEY_USER_EMAIL, email)
        editor.putString(KEY_USER_PASSWORD, password)
        editor.commit()
        UserSession.clear()
    }
}
