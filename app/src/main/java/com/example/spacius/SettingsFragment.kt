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

// No es necesario importar nada más para esta lógica
// SessionManager y Firestore se añadirán cuando implementemos la carga de datos

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var auth: FirebaseAuth
    // Quitamos SessionManager y Firestore por ahora para enfocarnos en la UI
    // private lateinit var sessionManager: SessionManager

    // Referencias a los paneles que se expandirán
    private lateinit var panelEditarPerfil: LinearLayout
    private lateinit var panelEspacios: LinearLayout
    private lateinit var panelHistorial: LinearLayout
    private lateinit var panelNotificaciones: LinearLayout

    // Variable para saber qué panel está actualmente abierto
    private var panelAbierto: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        // sessionManager = SessionManager(requireContext())

        // --- INICIALIZACIÓN DE VISTAS (SIN CAMBIOS) ---

        // Referencias a las filas que actúan como botones
        val rowEditarPerfil: LinearLayout = view.findViewById(R.id.rowEditarPerfil)
        val rowEspacios: LinearLayout = view.findViewById(R.id.rowEspacios)
        val rowHistorial: LinearLayout = view.findViewById(R.id.rowHistorial)
        val rowNotificaciones: LinearLayout = view.findViewById(R.id.rowNotificaciones)
        val rowCerrarSesion: LinearLayout = view.findViewById(R.id.rowCerrarSesion)

        // Referencias a los paneles desplegables
        panelEditarPerfil = view.findViewById(R.id.panelEditarPerfil)
        panelEspacios = view.findViewById(R.id.panelEspacios)
        panelHistorial = view.findViewById(R.id.panelHistorial)
        panelNotificaciones = view.findViewById(R.id.panelNotificaciones)

        // --- CONFIGURACIÓN DE LISTENERS ---

        rowEditarPerfil.setOnClickListener {
            // Aquí, en el futuro, llamaremos a cargarDatosUsuario()
            togglePanel(panelEditarPerfil)
        }

        rowEspacios.setOnClickListener {
            // En el futuro, aquí se cargarán los espacios favoritos
            togglePanel(panelEspacios)
        }

        rowHistorial.setOnClickListener {
            // En el futuro, aquí se cargará el historial
            togglePanel(panelHistorial)
        }

        rowNotificaciones.setOnClickListener {
            // En el futuro, aquí se cargarán las configuraciones de notificación
            togglePanel(panelNotificaciones)
        }

        rowCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    /**
     * Muestra u oculta un panel. Si otro panel está abierto, lo cierra primero.
     * Esta función es el corazón del comportamiento de acordeón.
     */
    private fun togglePanel(panel: View) {
        val estaAbierto = panel.visibility == View.VISIBLE

        // Primero, cierra el panel que esté abierto (si hay alguno)
        panelAbierto?.visibility = View.GONE

        // Si el panel que pulsamos no era el que ya estaba abierto, lo abrimos.
        // Si era el mismo, se quedará cerrado por la acción anterior.
        if (!estaAbierto) {
            panel.visibility = View.VISIBLE
            panelAbierto = panel // Guardamos la referencia al panel que acabamos de abrir
        } else {
            // Si el panel pulsado ya estaba abierto, se ha cerrado, así que reseteamos la referencia.
            panelAbierto = null
        }
    }

    private fun cerrarSesion() {
        auth.signOut()
        Toast.makeText(requireContext(), "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()

        // Redirigir al usuario a la pantalla de Login
        val intent = Intent(requireContext(), LoginActivity::class.java)
        // Estas flags limpian el historial de Activities para que el usuario no pueda volver atrás.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish() // Finaliza la actividad actual
    }
}


