package com.example.spacius

import android.app.Application
import com.google.firebase.FirebaseApp

class SpaciusApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Inicializar Firebase explícitamente
        FirebaseApp.initializeApp(this)
    }
}