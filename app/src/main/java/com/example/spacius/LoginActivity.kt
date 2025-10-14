package com.example.spacius

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar Firebase Auth
        auth = Firebase.auth

        // Vincular los elementos del XML con sus IDs correctos
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegistro = findViewById<TextView>(R.id.tvRegistro)
        
        // Crear ProgressBar programáticamente si no existe en el layout
        progressBar = ProgressBar(this).apply {
            visibility = View.GONE
        }

        // Botón Ingresar con Firebase Auth
        btnLogin.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            if (validarCampos(correo, contrasena)) {
                loginConFirebase(correo, contrasena)
            }
        }

        // Texto "Registrarse" → abre RegisterActivity
        tvRegistro.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        // Verificar si el usuario ya está logueado
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Usuario ya logueado, ir directamente al MainActivity
            irAMainActivity()
        }
    }

    private fun validarCampos(correo: String, contrasena: String): Boolean {
        when {
            correo.isEmpty() -> {
                Toast.makeText(this, "Por favor ingresa tu correo", Toast.LENGTH_SHORT).show()
                return false
            }
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> {
                Toast.makeText(this, "Por favor ingresa un correo válido", Toast.LENGTH_SHORT).show()
                return false
            }
            contrasena.isEmpty() -> {
                Toast.makeText(this, "Por favor ingresa tu contraseña", Toast.LENGTH_SHORT).show()
                return false
            }
            contrasena.length < 6 -> {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return false
            }
            else -> return true
        }
    }

    private fun loginConFirebase(correo: String, contrasena: String) {
        mostrarCargando(true)

        auth.signInWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener(this) { task ->
                mostrarCargando(false)
                
                if (task.isSuccessful) {
                    // Login exitoso
                    val user = auth.currentUser
                    Toast.makeText(this, "Bienvenido ${user?.email}", Toast.LENGTH_SHORT).show()
                    irAMainActivity()
                } else {
                    // Error en el login
                    val errorMessage = when (task.exception?.message) {
                        "The email address is badly formatted." -> "Formato de correo inválido"
                        "There is no user record corresponding to this identifier. The user may have been deleted." -> "No existe una cuenta con este correo"
                        "The password is invalid or the user does not have a password." -> "Contraseña incorrecta"
                        "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> "Error de conexión. Verifica tu internet"
                        else -> "Error al iniciar sesión. Verifica tus credenciales"
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun mostrarCargando(mostrar: Boolean) {
        progressBar.visibility = if (mostrar) View.VISIBLE else View.GONE
        findViewById<Button>(R.id.btnLogin).isEnabled = !mostrar
    }

    private fun irAMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Cierra la pantalla de login
    }
}
