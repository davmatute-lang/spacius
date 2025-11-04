package com.example.spacius

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private lateinit var notificationPrefs: NotificationPreferences

    private lateinit var switchAll: SwitchMaterial
    private lateinit var switchConfirmations: SwitchMaterial
    private lateinit var switchReminders: SwitchMaterial
    private lateinit var switchNewSpaces: SwitchMaterial
    private lateinit var testButton: Button

    // Lanzador para solicitar permisos de notificación
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido, podemos enviar notificaciones
            sendTestNotification()
        } else {
            // Permiso denegado
            Toast.makeText(requireContext(), "Permiso de notificación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationPrefs = NotificationPreferences(requireContext())

        // Encontrar vistas
        switchAll = view.findViewById(R.id.switch_all_notifications)
        switchConfirmations = view.findViewById(R.id.switch_booking_confirmations)
        switchReminders = view.findViewById(R.id.switch_booking_reminders)
        switchNewSpaces = view.findViewById(R.id.switch_new_spaces)
        testButton = view.findViewById(R.id.btn_test_notification)

        loadPreferences()
        setupListeners()
    }

    private fun loadPreferences() {
        switchAll.isChecked = notificationPrefs.areAllNotificationsEnabled()
        switchConfirmations.isChecked = notificationPrefs.isBookingConfirmationsEnabled()
        switchReminders.isChecked = notificationPrefs.isBookingRemindersEnabled()
        switchNewSpaces.isChecked = notificationPrefs.isNewSpacesEnabled()
        updateAllSwitches(switchAll.isChecked)
    }

    private fun setupListeners() {
        switchAll.setOnCheckedChangeListener { _, isChecked ->
            notificationPrefs.setAllNotificationsEnabled(isChecked)
            updateAllSwitches(isChecked)
        }
        switchConfirmations.setOnCheckedChangeListener { _, isChecked ->
            notificationPrefs.setBookingConfirmationsEnabled(isChecked)
        }
        switchReminders.setOnCheckedChangeListener { _, isChecked ->
            notificationPrefs.setBookingRemindersEnabled(isChecked)
        }
        switchNewSpaces.setOnCheckedChangeListener { _, isChecked ->
            notificationPrefs.setNewSpacesEnabled(isChecked)
        }
        testButton.setOnClickListener {
            handleTestNotificationClick()
        }
    }

    private fun updateAllSwitches(isEnabled: Boolean) {
        switchConfirmations.isEnabled = isEnabled
        switchReminders.isEnabled = isEnabled
        switchNewSpaces.isEnabled = isEnabled

        if (!isEnabled) {
            switchConfirmations.isChecked = false
            switchReminders.isChecked = false
            switchNewSpaces.isChecked = false
        }
    }

    private fun handleTestNotificationClick() {
        // Primero, comprobar si la preferencia está activada
        if (notificationPrefs.isNewSpacesEnabled() && notificationPrefs.areAllNotificationsEnabled()) {
            // Segundo, comprobar si tenemos permiso para enviar notificaciones
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Ya tenemos permiso, enviar notificación
                    sendTestNotification()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Mostrar un diálogo explicando por qué necesitamos el permiso (opcional)
                    Toast.makeText(requireContext(), "Se necesitan permisos para mostrar notificaciones", Toast.LENGTH_LONG).show()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Solicitar permiso directamente
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
        } else {
            // La preferencia está desactivada
            Toast.makeText(requireContext(), "Las notificaciones de 'Nuevos Espacios' están desactivadas", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendTestNotification() {
        val builder = NotificationCompat.Builder(requireContext(), SpaciusApplication.NEW_SPACES_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications) // Reemplaza con tu icono
            .setContentTitle("¡Nuevo Espacio Disponible!")
            .setContentText("Una nueva cancha de tenis ha sido añadida cerca de ti.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())) {
            // El ID de la notificación debe ser único
            notify(1, builder.build())
        }
        Toast.makeText(requireContext(), "Enviando notificación de prueba...", Toast.LENGTH_SHORT).show()
    }
}
