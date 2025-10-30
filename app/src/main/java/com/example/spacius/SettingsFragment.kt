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
import com.example.spacius.SessionManager
import com.example.spacius.EditProfileFragment

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var auth: FirebaseAuth
    private lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        sessionManager = SessionManager(requireContext())

        val rowEditarPerfil = view.findViewById<LinearLayout>(R.id.rowEditarPerfil)
        val rowEspacios = view.findViewById<LinearLayout>(R.id.rowEspacios)
        val rowHistorial = view.findViewById<LinearLayout>(R.id.rowHistorial)
        val rowNotificaciones = view.findViewById<LinearLayout>(R.id.rowNotificaciones)
        val rowCerrarSesion = view.findViewById<LinearLayout>(R.id.rowCerrarSesion)

        rowEditarPerfil.setOnClickListener {
            openFragment(EditProfileFragment())
        }

        rowEspacios.setOnClickListener {
            // Próximamente: openFragment(FavoriteSpacesFragment())
            Toast.makeText(requireContext(), "Espacios Favoritos - Próximamente", Toast.LENGTH_SHORT).show()
        }

        rowHistorial.setOnClickListener {
            Toast.makeText(requireContext(), "Historial - Próximamente", Toast.LENGTH_SHORT).show()
        }

        rowNotificaciones.setOnClickListener {
            // Próximamente: openFragment(NotificationsFragment())
            Toast.makeText(requireContext(), "Notificaciones - Próximamente", Toast.LENGTH_SHORT).show()
        }

        rowCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun openFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.settings_content_container, fragment)
            .addToBackStack(null) // Opcional: permite volver atrás
            .commit()
    }

    private fun cerrarSesion() {
        sessionManager.clearSession()
        auth.signOut()
        Toast.makeText(requireContext(), "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }
}

