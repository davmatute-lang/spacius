package com.example.spacius

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // VINCULACIÓN DE VISTAS (ACOPLES A LOS IDs de tu XML)
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etCedula = findViewById<EditText>(R.id.etCedula) // ID de Identificación
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etTelefono = findViewById<EditText>(R.id.etTelefono) // Nuevo campo en el XML
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword) // Nuevo campo en el XML
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        val db = UserDatabaseHelper(this)

        btnRegistrar.setOnClickListener {
            // 1. OBTENCIÓN DE DATOS
            val nombre = etNombre.text.toString().trim()
            val cedula = etCedula.text.toString().trim() // Usado como 'identificacion'
            val email = etEmail.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            // 2. MANEJO DEL CAMPO FALTANTE
            // Como 'fechaNacimiento' no está en el XML, enviamos un placeholder.
            val fechaNacimientoDefault = "2000-01-01"
            val passwordHash = password // Usamos el texto como hash temporalmente.

            // 3. VALIDACIÓN DE CAMPOS Y CONTRASEÑAS
            if (nombre.isEmpty() || cedula.isEmpty() || email.isEmpty() || telefono.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // 4. REGISTRO EN LA BASE DE DATOS

            // Los campos se pasan en el orden que espera el DB Helper:
            // nombre, identificacion, fechaNacimiento, email, passwordHash
            val exito = db.registrarUsuario(
                nombre,
                cedula,                  // Pasado como identificacion
                fechaNacimientoDefault,  // Valor por defecto/placeholder
                email,
                passwordHash
            )

            if (exito) {
                Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                // Redirigir a Login
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Error: El correo o la cédula ya están registrados.", Toast.LENGTH_LONG).show()
            }
        }
    }
}