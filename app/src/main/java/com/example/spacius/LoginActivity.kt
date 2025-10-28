// LoginActivity.kt - ACTUALIZADO

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

        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegistro = findViewById<TextView>(R.id.tvRegistro)

        val db = UserDatabaseHelper(this)
        val sessionManager = SessionManager(this) // Inicializar el gestor de sesión

        // Botón Ingresar
        btnLogin.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()
            // NOTA: DEBES usar el mismo hashing aquí que en tu registro si es que hasheas antes de guardar.

            if (correo.isNotEmpty() && contrasena.isNotEmpty()) {
                // Usa el nuevo método que devuelve el ID
                val userId = db.getUserIdByCredentials(correo, contrasena)

                if (userId != null) {
                    // Éxito: Guardar el ID en la sesión
                    sessionManager.saveUserId(userId)

                    Toast.makeText(this, "Bienvenido $correo", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor completa los campos", Toast.LENGTH_SHORT).show()
            }
        }

        tvRegistro.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}