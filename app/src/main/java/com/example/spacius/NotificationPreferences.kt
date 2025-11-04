package com.example.spacius

import android.content.Context
import android.content.SharedPreferences

class NotificationPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)

    companion object {
        const val PREF_ALL_NOTIFICATIONS = "all_notifications"
        const val PREF_BOOKING_CONFIRMATIONS = "booking_confirmations"
        const val PREF_BOOKING_REMINDERS = "booking_reminders"
        const val PREF_NEW_SPACES = "new_spaces"
    }

    fun setAllNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(PREF_ALL_NOTIFICATIONS, enabled).apply()
    }

    fun areAllNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(PREF_ALL_NOTIFICATIONS, true) // Activado por defecto
    }

    fun setBookingConfirmationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(PREF_BOOKING_CONFIRMATIONS, enabled).apply()
    }

    fun isBookingConfirmationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(PREF_BOOKING_CONFIRMATIONS, true)
    }

    fun setBookingRemindersEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(PREF_BOOKING_REMINDERS, enabled).apply()
    }

    fun isBookingRemindersEnabled(): Boolean {
        return sharedPreferences.getBoolean(PREF_BOOKING_REMINDERS, true)
    }

    fun setNewSpacesEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(PREF_NEW_SPACES, enabled).apply()
    }

    fun isNewSpacesEnabled(): Boolean {
        return sharedPreferences.getBoolean(PREF_NEW_SPACES, true)
    }
}