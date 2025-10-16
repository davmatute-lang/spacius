package com.example.spacius

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar Firebase Auth
        auth = Firebase.auth

        val rowEditarPerfil = view.findViewById<LinearLayout>(R.id.rowEditarPerfil)
        val rowEspacios = view.findViewById<LinearLayout>(R.id.rowEspacios)
        val rowHistorial = view.findViewById<LinearLayout>(R.id.rowHistorial)
        val rowCerrarSesion = view.findViewById<LinearLayout>(R.id.rowCerrarSesion)

        // Editar Perfil → Próximamente
        rowEditarPerfil.setOnClickListener {
            val user = auth.currentUser
            val userName = user?.displayName ?: user?.email ?: "Usuario"
            Toast.makeText(requireContext(), "Editar Perfil de $userName - Próximamente", Toast.LENGTH_SHORT).show()
        }

        // Espacios → Próximamente
        rowEspacios.setOnClickListener {
            Toast.makeText(requireContext(), "Espacios - Próximamente", Toast.LENGTH_SHORT).show()
        }

        // Historial → Próximamente
        rowHistorial.setOnClickListener {
            Toast.makeText(requireContext(), "Historial - Próximamente", Toast.LENGTH_SHORT).show()
        }

        // Cerrar sesión con Firebase Auth
        rowCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun cerrarSesion() {
        auth.signOut()
        Toast.makeText(requireContext(), "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()
        
        // Ir a LoginActivity y cerrar MainActivity
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }
}
