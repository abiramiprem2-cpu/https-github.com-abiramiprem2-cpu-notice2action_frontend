package com.simats.notice2action.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

object AuthRepository {

    private fun JSONObject.optNullableString(name: String): String? {
        return if (has(name) && !isNull(name)) {
            val v = getString(name)
            if (v.isBlank()) null else v
        } else null
    }

    private fun resolveName(rawName: String?, email: String): String {
        val trimmed = rawName?.trim() ?: ""
        if (trimmed.isNotBlank()) return trimmed
        return email.substringBefore("@").replaceFirstChar { it.uppercase() }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val body = JSONObject().apply {
                put("email", email.trim())
                put("password", password)
            }
            val (_, json) = ApiClient.postJson("auth/login.php", body)
            if (json.optBoolean("success", false)) {
                val userObj = json.getJSONObject("user")
                val userEmail = userObj.getString("email")
                val user = User(
                    id = userObj.getInt("id"),
                    name = resolveName(userObj.optString("name", ""), userEmail),
                    email = userEmail,
                    role = userObj.optString("role", "Student - Northbridge College"),
                    imagePath = userObj.optNullableString("image_path"),
                    phone = userObj.optNullableString("phone"),
                    address = userObj.optNullableString("address")
                )
                withContext(Dispatchers.Main) {
                    UserSession.currentUser = user
                }
                Result.success(user)
            } else {
                val msg = json.optString("message", "Login failed")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signup(name: String, email: String, password: String, role: String = "Student - Northbridge College"): Result<User> {
        return try {
            val body = JSONObject().apply {
                put("name", name.trim())
                put("email", email.trim())
                put("password", password)
                put("role", role.trim())
            }
            val (_, json) = ApiClient.postJson("auth/signup.php", body)
            if (json.optBoolean("success", false)) {
                val userObj = json.getJSONObject("user")
                val userEmail = userObj.getString("email")
                val user = User(
                    id = userObj.getInt("id"),
                    name = resolveName(userObj.optString("name", name), userEmail),
                    email = userEmail,
                    role = userObj.optString("role", role)
                )
                withContext(Dispatchers.Main) {
                    UserSession.currentUser = user
                }
                Result.success(user)
            } else {
                val msg = json.optString("message", "Registration failed")
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String, password: String): Result<String> {
        return try {
            val body = JSONObject().apply {
                put("email", email.trim())
                put("password", password)
            }
            val (_, json) = ApiClient.postJson("auth/reset_password.php", body)
            if (json.optBoolean("success", false)) {
                Result.success(json.optString("message", "Password reset successfully"))
            } else {
                Result.failure(Exception(json.optString("message", "Failed to reset password")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchProfile(userId: Int): Result<User> {
        return try {
            val body = JSONObject().apply {
                put("user_id", userId)
            }
            val (_, json) = ApiClient.postJson("profile/profile_fetch.php", body)
            if (json.optBoolean("success", false)) {
                val prof = json.getJSONObject("profile")
                val profEmail = prof.getString("email")
                val user = User(
                    id = prof.getInt("id"),
                    name = resolveName(prof.optString("name", ""), profEmail),
                    email = profEmail,
                    role = prof.optString("role", "Student - Northbridge College"),
                    imagePath = prof.optNullableString("image_path"),
                    phone = prof.optNullableString("phone"),
                    address = prof.optNullableString("address")
                )
                withContext(Dispatchers.Main) {
                    UserSession.currentUser = user
                }
                Result.success(user)
            } else {
                Result.failure(Exception(json.optString("message", "Profile fetch failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(
        userId: Int,
        name: String,
        email: String,
        role: String,
        phone: String? = null,
        address: String? = null,
        imageBase64: String? = null
    ): Result<User> {
        return try {
            val body = JSONObject().apply {
                put("user_id", userId)
                put("name", name.trim())
                put("email", email.trim())
                put("role", role.trim())
                if (phone != null) put("phone", phone.trim())
                if (address != null) put("address", address.trim())
                if (!imageBase64.isNullOrBlank()) put("image_base64", imageBase64)
            }
            val (_, json) = ApiClient.postJson("profile/profile_edit.php", body)
            if (json.optBoolean("success", false)) {
                val prof = json.getJSONObject("profile")
                val profEmail = prof.getString("email")
                val user = User(
                    id = prof.getInt("id"),
                    name = resolveName(prof.optString("name", name), profEmail),
                    email = profEmail,
                    role = prof.optString("role", role),
                    imagePath = prof.optNullableString("image_path"),
                    phone = prof.optNullableString("phone"),
                    address = prof.optNullableString("address")
                )
                withContext(Dispatchers.Main) {
                    UserSession.currentUser = user
                }
                Result.success(user)
            } else {
                Result.failure(Exception(json.optString("message", "Profile update failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(userId: Int): Result<Unit> {
        return try {
            val body = JSONObject().apply {
                put("user_id", userId)
            }
            val (_, json) = ApiClient.postJson("auth/delete_account.php", body)
            if (json.optBoolean("success", false)) {
                withContext(Dispatchers.Main) {
                    UserSession.clear()
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception(json.optString("message", "Failed to delete account")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
