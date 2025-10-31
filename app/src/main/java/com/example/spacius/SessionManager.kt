package com.example.spacius

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences
    private val editor: SharedPreferences.Editor

    // Archivo donde se guardarán los datos de sesión
    private val PREF_NAME = "SpaciusAppSession"
    // Clave para saber si el usuario inició sesión
    private val IS_LOGGED_IN = "isLoggedIn"

    init {
        // Inicializa las SharedPreferences para poder guardar y leer datos
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = prefs.edit()
    }

    /**
     * Guarda el estado de "sesión iniciada".
     */
    fun createLoginSession() {
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.apply() // Guarda los cambios de forma asíncrona
    }

    /**
     * Verifica si el usuario ha iniciado sesión.
     * Devuelve 'true' si ha iniciado sesión, 'false' en caso contrario.
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }

    /**
     * Borra todos los datos de la sesión (para cerrar sesión).
     */
    fun clearSession() {
        editor.clear()
        editor.apply()
    }
}
