package com.example.spacius

import android.app.Application
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
    
    override fun onCreate() {
        super.onCreate()
        
        // Inicializar Firebase explícitamente
        FirebaseApp.initializeApp(this)
        
        // Inicializar Firestore y datos predefinidos
        inicializarFirestore()
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