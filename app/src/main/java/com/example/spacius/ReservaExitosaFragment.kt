package com.example.spacius

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.spacius.preferences.NotificationPreferences
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class ReservaExitosaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reserva_exitosa, container, false)

        val txtNombre: TextView = view.findViewById(R.id.btnNombreLugar)
        val txtDescripcion: TextView = view.findViewById(R.id.btnDescripcionLugar)
        val txtCapacidad: TextView = view.findViewById(R.id.btnCapacidad)
        val txtFecha: TextView = view.findViewById(R.id.btnFecha)
        val txtHoraInicio: TextView = view.findViewById(R.id.btnHoraInicio)
        val txtHoraFin: TextView = view.findViewById(R.id.btnHoraFin)
        val btnVolver: Button = view.findViewById(R.id.btnVolverInicio)

        arguments?.let { bundle ->
            val nombreLugar = bundle.getString("nombreLugar") ?: ""
            val descripcion = bundle.getString("descripcion") ?: ""
            val capacidad = bundle.getString("capacidad") ?: ""
            val fecha = bundle.getString("fecha") ?: ""
            val horaInicio = bundle.getString("horaInicio") ?: ""
            val horaFin = bundle.getString("horaFin") ?: ""
            val bookingId = bundle.getString("bookingId") ?: ""

            txtNombre.text = nombreLugar
            txtDescripcion.text = descripcion
            txtCapacidad.text = "Capacidad: $capacidad personas"
            txtFecha.text = fecha
            txtHoraInicio.text = horaInicio
            txtHoraFin.text = horaFin

            val notificationPrefs = NotificationPreferences(requireContext())
            if (notificationPrefs.isBookingConfirmationsEnabled()) {
                showBookingConfirmationNotification(nombreLugar, horaInicio)
            }
            scheduleBookingReminder(bookingId, nombreLugar, fecha, horaInicio)
        }

        btnVolver.setOnClickListener {
            (activity as? MainActivity)?.setSelectedBottomNav(R.id.nav_inicio)
        }

        return view
    }

    private fun showBookingConfirmationNotification(spaceName: String, time: String) {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(requireContext(), SpaciusApplication.BOOKING_CONFIRMATIONS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("¡Reserva Confirmada!")
            .setContentText("Tu reserva en $spaceName a las $time ha sido confirmada.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Handle permission request
            return
        }
        with(NotificationManagerCompat.from(requireContext())) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    private fun scheduleBookingReminder(bookingId: String, spaceName: String, date: String, time: String) {
        val notificationPrefs = NotificationPreferences(requireContext())
        if (!notificationPrefs.isBookingRemindersEnabled()) {
            return
        }

        try {
            val dateTimeStr = "$date $time"
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val bookingDateTime = format.parse(dateTimeStr)

            val calendar = Calendar.getInstance()
            calendar.time = bookingDateTime!!
            calendar.add(Calendar.MINUTE, -15) // 15-minute reminder
            val reminderTime = calendar.timeInMillis

            val currentTime = System.currentTimeMillis()
            if (reminderTime > currentTime) {
                val delay = reminderTime - currentTime

                val data = Data.Builder()
                    .putString("bookingId", bookingId)
                    .putString("spaceName", spaceName)
                    .putString("date", date)
                    .putString("time", time)
                    .build()

                val reminderWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .build()

                WorkManager.getInstance(requireContext()).enqueue(reminderWorkRequest)
                Log.d("Reminder", "Reminder scheduled for booking $bookingId at $date $time")
            }
        } catch (e: Exception) {
            Log.e("Reminder", "Error scheduling reminder", e)
        }
    }
}
