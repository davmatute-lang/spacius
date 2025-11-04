package com.example.spacius

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.spacius.data.FirestoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SpaciusApplication : Application() {

    companion object {
        const val NEW_SPACES_CHANNEL_ID = "new_spaces_channel"
    }

    override fun onCreate() {
        super.onCreate()

        // Inicializar Firebase explícitamente
        FirebaseApp.initializeApp(this)

        // Crear canales de notificación
        createNotificationChannel()

        // Inicializar Firestore y datos predefinidos
        inicializarFirestore()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Nuevos Espacios"
            val descriptionText = "Notificaciones sobre nuevos espacios disponibles"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NEW_SPACES_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Registrar el canal con el sistema
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("SpaciusApp", "Canal de notificación 'Nuevos Espacios' creado.")
        }
    }

    private fun inicializarFirestore() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("SpaciusApp", "Inicializando Firestore...")

                val firestore = Firebase.firestore
                val repository = FirestoreRepository()

                // Forzar inicialización de lugares
                val exito = repository.inicializarLugaresPredefinidos()
                Log.d("SpaciusApp", "Lugares inicializados: $exito")

                // Verificar que Firestore esté funcionando
                val lugares = repository.obtenerLugares()
                Log.d("SpaciusApp", "Lugares cargados: ${lugares.size}")

                // Verificar estado de reservas (solo si hay usuario autenticado)
                val auth = Firebase.auth
                if (auth.currentUser != null) {
                    Log.d("SpaciusApp", "Usuario autenticado: ${auth.currentUser?.email}")

                    // Test simple: obtener todas las reservas de la colección
                    val todasReservas = firestore.collection("reservas").get().await()
                    Log.d("SpaciusApp", "Total reservas en Firestore: ${todasReservas.size()}")
                } else {
                    Log.d("SpaciusApp", "No hay usuario autenticado en Application")
                }

            } catch (e: Exception) {
                Log.e("SpaciusApp", "Error al inicializar Firestore: ${e.message}")
            }
        }
    }
}