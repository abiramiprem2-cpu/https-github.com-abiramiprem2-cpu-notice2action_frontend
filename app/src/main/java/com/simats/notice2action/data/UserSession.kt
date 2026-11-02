package com.simats.notice2action.data

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String = "Student - Northbridge College",
    val imagePath: String? = null,
    val phone: String? = null,
    val address: String? = null
)

object UserSession {
    var currentUser: User? = null

    val isLoggedIn: Boolean
        get() = currentUser != null

    fun clear() {
        currentUser = null
    }
}
