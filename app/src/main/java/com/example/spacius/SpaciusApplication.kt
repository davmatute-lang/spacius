package com.example.spacius

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class SpaciusApplication : Application() {

    companion object {
        // ID para el canal de notificaciones de nuevos espacios
        const val NEW_SPACES_CHANNEL_ID = "new_spaces_channel"
        // ID para el canal de notificaciones de recordatorios de reservas
        const val BOOKING_REMINDERS_CHANNEL_ID = "booking_reminders_channel"
        // ID para el canal de notificaciones de confirmaciones de reservas
        const val BOOKING_CONFIRMATIONS_CHANNEL_ID = "booking_confirmations_channel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Canal para nuevos espacios
            val newSpacesChannel = NotificationChannel(
                NEW_SPACES_CHANNEL_ID,
                "Nuevos Espacios",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones sobre nuevos espacios añadidos"
            }

            // Canal para recordatorios de reservas
            val bookingRemindersChannel = NotificationChannel(
                BOOKING_REMINDERS_CHANNEL_ID,
                "Recordatorios de Reservas",
                NotificationManager.IMPORTANCE_HIGH // Más importante que los nuevos espacios
            ).apply {
                description = "Recordatorios para tus próximas reservas"
            }

            // Canal para confirmaciones de reservas
            val bookingConfirmationsChannel = NotificationChannel(
                BOOKING_CONFIRMATIONS_CHANNEL_ID,
                "Confirmaciones de Reserva",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Confirmaciones para tus reservas"
            }

            // Registrar los canales en el sistema
            notificationManager.createNotificationChannel(newSpacesChannel)
            notificationManager.createNotificationChannel(bookingRemindersChannel)
            notificationManager.createNotificationChannel(bookingConfirmationsChannel)
        }
    }
}
