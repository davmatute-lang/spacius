package com.example.spacius.preferences

import android.content.Context

class NotificationPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)

    fun isBookingRemindersEnabled(): Boolean {
        return sharedPreferences.getBoolean("booking_reminders_enabled", true)
    }

    fun isBookingConfirmationsEnabled(): Boolean {
        return sharedPreferences.getBoolean("booking_confirmations_enabled", true)
    }
}
