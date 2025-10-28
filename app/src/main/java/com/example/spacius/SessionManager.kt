// SessionManager.kt

package com.example.spacius

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        const val PREFS_NAME = "UserSession"
        const val KEY_USER_ID = "user_id"
    }

    /** Guarda el ID del usuario logueado. */
    fun saveUserId(userId: String) {
        prefs.edit().putString(KEY_USER_ID, userId).apply()
    }

    /** Recupera el ID del usuario logueado. Devuelve null si no hay sesión. */
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    /** Borra la sesión al cerrar. */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}