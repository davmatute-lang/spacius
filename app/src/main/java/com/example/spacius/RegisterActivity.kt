package com.example.spacius

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.FirebaseApp

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Asegurar que Firebase esté inicializado
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }

        // Inicializar Firebase Auth
        auth = Firebase.auth

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        // Crear ProgressBar programáticamente si no existe en el layout
        progressBar = ProgressBar(this).apply {
            visibility = View.GONE
        }

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validarCampos(nombre, email, password)) {
                registrarConFirebase(nombre, email, password)
            }
        }
    }

    private fun validarCampos(nombre: String, email: String, password: String): Boolean {
        when {
            nombre.isEmpty() -> {
                Toast.makeText(this, "Por favor ingresa tu nombre", Toast.LENGTH_SHORT).show()
                return false
            }
            nombre.length < 2 -> {
                Toast.makeText(this, "El nombre debe tener al menos 2 caracteres", Toast.LENGTH_SHORT).show()
                return false
            }
            email.isEmpty() -> {
                Toast.makeText(this, "Por favor ingresa tu correo", Toast.LENGTH_SHORT).show()
                return false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(this, "Por favor ingresa un correo válido", Toast.LENGTH_SHORT).show()
                return false
            }
            password.isEmpty() -> {
                Toast.makeText(this, "Por favor ingresa una contraseña", Toast.LENGTH_SHORT).show()
                return false
            }
            password.length < 6 -> {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return false
            }
            !password.any { it.isDigit() } -> {
                Toast.makeText(this, "La contraseña debe contener al menos un número", Toast.LENGTH_SHORT).show()
                return false
            }
            else -> return true
        }
    }

    private fun registrarConFirebase(nombre: String, email: String, password: String) {
        mostrarCargando(true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registro exitoso, actualizar el perfil del usuario con el nombre
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(nombre)
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileTask ->
                            mostrarCargando(false)
                            
                            if (profileTask.isSuccessful) {
                                Toast.makeText(this, "¡Cuenta creada exitosamente! Bienvenido $nombre", Toast.LENGTH_LONG).show()
                                irALogin()
                            } else {
                                Toast.makeText(this, "Cuenta creada pero error al guardar el nombre", Toast.LENGTH_SHORT).show()
                                irALogin()
                            }
                        }
                } else {
                    mostrarCargando(false)
                    // Error en el registro
                    val errorMessage = when (task.exception?.message) {
                        "The email address is already in use by another account." -> "Este correo ya está registrado"
                        "The email address is badly formatted." -> "Formato de correo inválido"
                        "The given password is invalid. [ Password should be at least 6 characters ]" -> "La contraseña debe tener al menos 6 caracteres"
                        "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> "Error de conexión. Verifica tu internet"
                        else -> "Error al crear la cuenta: ${task.exception?.message}"
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun mostrarCargando(mostrar: Boolean) {
        progressBar.visibility = if (mostrar) View.VISIBLE else View.GONE
        findViewById<Button>(R.id.btnRegistrar).isEnabled = !mostrar
    }

    private fun irALogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
