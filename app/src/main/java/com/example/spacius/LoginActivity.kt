package com.example.spacius

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Vincular los elementos del XML con sus IDs correctos
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegistro = findViewById<TextView>(R.id.tvRegistro)

        val db = UserDatabaseHelper(this)

        // Botón Ingresar
        btnLogin.setOnClickListener {
            val correo = etCorreo.text.toString().trim().lowercase()
            val contrasena = etContrasena.text.toString().trim()

            if (correo.isNotEmpty() && contrasena.isNotEmpty()) {
                if (db.loginUsuario(correo, contrasena)) {
                    Toast.makeText(this, "Bienvenido $correo", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Cierra la pantalla de login
                } else {
                    Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor completa los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Texto "Registrarse" → abre RegisterActivity
        tvRegistro.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
