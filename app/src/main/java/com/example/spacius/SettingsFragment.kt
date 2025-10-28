// SettingsFragment.kt - ACTUALIZADO

package com.example.spacius

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

// NOTA: La clase User fue movida a UserDatabaseHelper.kt

class SettingsFragment : Fragment() {

    // Vistas del perfil
    private lateinit var headerEditarPerfil: LinearLayout
    private lateinit var contentEditarPerfil: LinearLayout
    private lateinit var iconExpandir: ImageView

    private lateinit var etNombre: EditText
    private lateinit var etIdentificacion: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText

    // Instancias de la base de datos y sesión
    private lateinit var dbHelper: UserDatabaseHelper
    private lateinit var sessionManager: SessionManager

    // Almacena la instancia del usuario actual para facilitar la actualización.
    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Inicializar helpers
        dbHelper = UserDatabaseHelper(requireContext())
        sessionManager = SessionManager(requireContext())

        // 1. Inicialización de Vistas
        headerEditarPerfil = view.findViewById(R.id.headerEditarPerfil)
        contentEditarPerfil = view.findViewById(R.id.contentEditarPerfil)
        iconExpandir = view.findViewById(R.id.iconExpandir)
        etNombre = view.findViewById(R.id.etNombre)
        etIdentificacion = view.findViewById(R.id.etIdentificacion)
        etFechaNacimiento = view.findViewById(R.id.etFechaNacimiento)
        etCorreo = view.findViewById(R.id.etCorreo)
        etContrasena = view.findViewById(R.id.etContrasena)

        val btnGuardar: Button = view.findViewById(R.id.btnGuardar)
        val rowCerrarSesion: LinearLayout = view.findViewById(R.id.rowCerrarSesion)
        val rowHistorial: LinearLayout = view.findViewById(R.id.rowHistorial)
        val btnCambiarFoto: Button = view.findViewById(R.id.btnCambiarFoto)

        // 2. Cargar datos iniciales del usuario
        loadUserData()

        // 3. LÓGICA DE EVENTOS
        headerEditarPerfil.setOnClickListener {
            toggleProfileContent()
        }

        btnGuardar.setOnClickListener {
            updateUserData()
        }

        // Otras lógicas
        btnCambiarFoto.setOnClickListener {
            Toast.makeText(requireContext(), "Función de cambiar foto próximamente", Toast.LENGTH_SHORT).show()
        }

        rowHistorial.setOnClickListener {
            Toast.makeText(requireContext(), "Ir al historial (pendiente)", Toast.LENGTH_SHORT).show()
        }

        rowCerrarSesion.setOnClickListener {
            sessionManager.clearSession()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }

    /**
     * Obtiene el ID del usuario de la sesión y carga los datos desde la DB.
     */
    private fun loadUserData() {
        val userId = sessionManager.getUserId()

        if (userId != null) {
            // Consulta la base de datos usando el ID de la sesión
            currentUser = dbHelper.getUserById(userId)

            if (currentUser != null) {
                // Campos NO editables (Identificación y Fecha de Nacimiento)
                etIdentificacion.setText(currentUser!!.identification)
                etFechaNacimiento.setText(currentUser!!.birthDate)

                // Campos editables (Nombre y Correo Electrónico)
                etNombre.setText(currentUser!!.name)
                etCorreo.setText(currentUser!!.email)
            } else {
                Toast.makeText(requireContext(), "Error: Datos de perfil no encontrados.", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(requireContext(), "Error: No hay sesión activa. Redirigiendo...", Toast.LENGTH_LONG).show()
            // Opcional: Redirigir a login si no hay sesión
        }
    }

    /**
     * Valida los campos y actualiza el perfil del usuario en la DB.
     */
    private fun updateUserData() {
        val user = currentUser ?: return

        val nombre = etNombre.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val nuevaContrasena = etContrasena.text.toString()

        if (nombre.isEmpty() || correo.isEmpty()) {
            Toast.makeText(requireContext(), "El nombre y el correo no pueden estar vacíos.", Toast.LENGTH_LONG).show()
            return
        }

        // 1. Determinar el hash de la contraseña
        val hashParaGuardar = if (nuevaContrasena.isNotEmpty()) {
            // NOTA: En la práctica, llama aquí a tu función de hashing segura
            nuevaContrasena // Si el usuario escribe, usa el nuevo valor (asumiendo que tu DB helper lo hasheará o lo está guardando en texto plano)
        } else {
            user.passwordHash // Si el campo está vacío, mantiene el hash antiguo
        }

        // 2. Crear objeto User con los nuevos datos
        val userToUpdate = user.copy(
            name = nombre,
            email = correo,
            passwordHash = hashParaGuardar
        )

        // 3. Ejecutar la actualización en el DB Helper
        val success = dbHelper.updateProfile(userToUpdate)

        if (success) {
            currentUser = userToUpdate
            Toast.makeText(requireContext(), "Cambios guardados correctamente", Toast.LENGTH_SHORT).show()
            etContrasena.setText("")
        } else {
            Toast.makeText(requireContext(), "Error al guardar los cambios.", Toast.LENGTH_LONG).show()
        }
    }

    private fun toggleProfileContent() {
        if (contentEditarPerfil.visibility == View.GONE) {
            contentEditarPerfil.visibility = View.VISIBLE
            iconExpandir.animate().rotation(180f).setDuration(200).start()
        } else {
            contentEditarPerfil.visibility = View.GONE
            iconExpandir.animate().rotation(0f).setDuration(200).start()
        }
    }
}