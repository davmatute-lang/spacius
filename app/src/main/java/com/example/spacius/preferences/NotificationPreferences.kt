package com.example.spacius.preferences

import android.content.Context
import android.content.SharedPreferences

class NotificationPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)

    companion object {
        const val PREF_ALL_NOTIFICATIONS = "all_notifications"
        const val PREF_BOOKING_CONFIRMATIONS = "booking_confirmations"
        const val PREF_BOOKING_REMINDERS = "booking_reminders"
        const val PREF_NEW_SPACES = "new_spaces"
    }

    // All Notifications
    fun areAllNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(PREF_ALL_NOTIFICATIONS, true)
    }

    fun setAllNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(PREF_ALL_NOTIFICATIONS, enabled).apply()
    }

    // Booking Confirmations
    fun isBookingConfirmationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(PREF_BOOKING_CONFIRMATIONS, true)
    }

    fun setBookingConfirmationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(PREF_BOOKING_CONFIRMATIONS, enabled).apply()
    }

    // Booking Reminders
    fun isBookingRemindersEnabled(): Boolean {
        return sharedPreferences.getBoolean(PREF_BOOKING_REMINDERS, true)
    }

    fun setBookingRemindersEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(PREF_BOOKING_REMINDERS, enabled).apply()
    }

    // New Spaces
    fun isNewSpacesEnabled(): Boolean {
        return sharedPreferences.getBoolean(PREF_NEW_SPACES, true)
    }

    fun setNewSpacesEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(PREF_NEW_SPACES, enabled).apply()
    }
}
