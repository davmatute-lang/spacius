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
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth

        val etNombre = findViewById<TextInputLayout>(R.id.tilNombre).editText
        val etCedula = findViewById<TextInputLayout>(R.id.tilCedula).editText
        val etEmail = findViewById<TextInputLayout>(R.id.tilEmail).editText
        val etTelefono = findViewById<TextInputLayout>(R.id.tilTelefono).editText
        val etPassword = findViewById<TextInputLayout>(R.id.tilPassword).editText
        val etConfirmPassword = findViewById<TextInputLayout>(R.id.tilConfirmPassword).editText
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        progressBar = ProgressBar(this).apply {
            visibility = View.GONE
        }

        btnRegistrar.setOnClickListener {
            val nombre = etNombre?.text.toString().trim()
            val cedula = etCedula?.text.toString().trim()
            val email = etEmail?.text.toString().trim()
            val telefono = etTelefono?.text.toString().trim()
            val password = etPassword?.text.toString().trim()
            val confirmPassword = etConfirmPassword?.text.toString().trim()

            if (validarCampos(nombre, cedula, email, telefono, password, confirmPassword)) {
                registrarConFirebase(nombre, cedula, email, telefono, password)
            }
        }
    }

    private fun validarCampos(nombre: String, cedula: String, email: String, telefono: String, pass: String, confirm: String): Boolean {
        if (nombre.isEmpty() || cedula.isEmpty() || email.isEmpty() || telefono.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Por favor ingresa un correo válido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (pass.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }
        if (pass != confirm) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun registrarConFirebase(nombre: String, cedula: String, email: String, telefono: String, password: String) {
        mostrarCargando(true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(nombre).build()

                    firebaseUser?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                        if (profileTask.isSuccessful) {
                            // Ahora, guarda los datos adicionales en Firestore
                            val userMap = hashMapOf(
                                "nombre" to nombre,
                                "email" to email,
                                "cedula" to cedula,
                                "telefono" to telefono
                            )

                            db.collection("users").document(firebaseUser.uid).set(userMap)
                                .addOnSuccessListener {
                                    mostrarCargando(false)
                                    Toast.makeText(this, "¡Cuenta creada exitosamente!", Toast.LENGTH_LONG).show()
                                    auth.signOut()
                                    irALogin()
                                }
                                .addOnFailureListener { e ->
                                    mostrarCargando(false)
                                    Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                             mostrarCargando(false)
                             Toast.makeText(this, "Error al actualizar el perfil.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    mostrarCargando(false)
                    val errorMessage = task.exception?.message ?: "Error desconocido"
                    Toast.makeText(this, "Error al crear la cuenta: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun mostrarCargando(mostrar: Boolean) {
        progressBar.visibility = if (mostrar) View.VISIBLE else View.GONE
        findViewById<Button>(R.id.btnRegistrar).isEnabled = !mostrar
    }

    private fun irALogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
