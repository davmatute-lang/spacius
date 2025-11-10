package com.example.spacius

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.spacius.model.Booking
import com.example.spacius.preferences.NotificationPreferences

class ReminderWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val bookingId = inputData.getString("bookingId") ?: return Result.failure()
        val spaceName = inputData.getString("spaceName") ?: return Result.failure()
        val date = inputData.getString("date") ?: return Result.failure()
        val time = inputData.getString("time") ?: return Result.failure()

        val notificationPrefs = NotificationPreferences(applicationContext)
        if (notificationPrefs.isBookingRemindersEnabled()) {
            val booking = Booking(bookingId, spaceName, date, time)
            showBookingReminderNotification(booking)
        }

        return Result.success()
    }

    private fun showBookingReminderNotification(booking: Booking) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(applicationContext, SpaciusApplication.BOOKING_REMINDERS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Recordatorio de Reserva")
            .setContentText("Tu reserva en ${booking.spaceName} es a las ${booking.time}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(applicationContext).notify(booking.bookingId.hashCode(), builder.build())
    }
}
