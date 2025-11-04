package com.example.spacius

import android.content.Intent
import com.example.spacius.FavoriteSpacesFragment
import com.example.spacius.HistoryFragment
import com.example.spacius.NotificationsFragment
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var auth: FirebaseAuth
    private lateinit var sessionManager: SessionManager

    // Referencias a los contenedores
    private lateinit var containerEditarPerfil: FrameLayout
    private lateinit var containerEspacios: FrameLayout
    private lateinit var containerHistorial: FrameLayout
    private lateinit var containerNotificaciones: FrameLayout

    private var openContainer: FrameLayout? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicialización
        auth = Firebase.auth
        sessionManager = SessionManager(requireContext())

        // Asignar vistas
        containerEditarPerfil = view.findViewById(R.id.containerEditarPerfil)
        containerEspacios = view.findViewById(R.id.containerEspacios)
        containerHistorial = view.findViewById(R.id.containerHistorial)
        containerNotificaciones = view.findViewById(R.id.containerNotificaciones)

        // Configurar listeners
        view.findViewById<LinearLayout>(R.id.rowEditarPerfil).setOnClickListener {
            togglePanel(containerEditarPerfil, EditProfileFragment())
        }
        view.findViewById<LinearLayout>(R.id.rowEspacios).setOnClickListener {
            togglePanel(containerEspacios, FavoriteSpacesFragment())
        }
        view.findViewById<LinearLayout>(R.id.rowHistorial).setOnClickListener {
             togglePanel(containerHistorial, HistoryFragment())
        }
        view.findViewById<LinearLayout>(R.id.rowNotificaciones).setOnClickListener {
             togglePanel(containerNotificaciones, NotificationsFragment())
        }
        view.findViewById<LinearLayout>(R.id.rowCerrarSesion).setOnClickListener {
            cerrarSesion()
        }
    }

    private fun togglePanel(tappedContainer: FrameLayout, fragment: Fragment) {
        // Si hay un panel abierto y no es el que acabamos de tocar, lo cerramos
        if (openContainer != null && openContainer != tappedContainer) {
            openContainer?.visibility = View.GONE
            removeFragment(openContainer!!)
        }

        // Abrimos o cerramos el panel que hemos tocado
        if (tappedContainer.visibility == View.GONE) {
            tappedContainer.visibility = View.VISIBLE
            loadFragment(tappedContainer, fragment)
            openContainer = tappedContainer
        } else {
            tappedContainer.visibility = View.GONE
            removeFragment(tappedContainer)
            openContainer = null
        }
    }

    private fun loadFragment(container: FrameLayout, fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(container.id, fragment)
            .commit()
    }

    private fun removeFragment(container: FrameLayout) {
         val findFragment = childFragmentManager.findFragmentById(container.id)
         if(findFragment != null) {
            childFragmentManager.beginTransaction()
            .remove(findFragment)
            .commit()
         }
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
