
package com.example.spacius

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import kotlin.jvm.java

class SettingsActivity : AppCompatActivity() {

    private lateinit var rowEditarPerfil: LinearLayout
    private lateinit var rowEspacios: LinearLayout
    private lateinit var rowHistorial: LinearLayout
    private lateinit var rowCerrarSesion: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Vincular vistas
        rowEditarPerfil = findViewById(R.id.rowEditarPerfil)
        rowEspacios = findViewById(R.id.rowEspacios)
        rowHistorial = findViewById(R.id.rowHistorial)
        rowCerrarSesion = findViewById(R.id.rowCerrarSesion)

        // Click en Editar Perfil
        rowEditarPerfil.setOnClickListener {
            // aquí abrir tu pantalla de editar perfil
        }

        // Click en Espacios Públicos Existentes
        rowEspacios.setOnClickListener {
            //  aquí abrir tu pantalla de espacios
        }

        // Click en Historial
        rowHistorial.setOnClickListener {
            // aquí abrir tu pantalla de historial
        }

        // Click en Cerrar Sesión → ir a LoginActivity
        rowCerrarSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // evita volver atrás a ajustes
        }
    }
}
