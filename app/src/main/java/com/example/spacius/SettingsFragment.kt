package com.example.spacius

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    // Se declaran las vistas necesarias para la lógica del desplegable
    private lateinit var headerEditarPerfil: LinearLayout
    private lateinit var contentEditarPerfil: LinearLayout
    private lateinit var iconExpandir: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // 1. Vistas Existentes (Mantienen su lógica)
        val rowHistorial: LinearLayout = view.findViewById(R.id.rowHistorial)
        val rowCerrarSesion: LinearLayout = view.findViewById(R.id.rowCerrarSesion)
        val btnGuardar: Button = view.findViewById(R.id.btnGuardar)
        val btnCambiarFoto: Button = view.findViewById(R.id.btnCambiarFoto)

        // 2. Vistas Nuevas para el Despliegue
        headerEditarPerfil = view.findViewById(R.id.headerEditarPerfil)
        contentEditarPerfil = view.findViewById(R.id.contentEditarPerfil)
        iconExpandir = view.findViewById(R.id.iconExpandir)


        // LÓGICA DE DESPLIEGUE (NUEVA FUNCIÓN)
        headerEditarPerfil.setOnClickListener {
            toggleProfileContent()
        }


        // LÓGICA EXISTENTE (sin cambios)
        btnCambiarFoto.setOnClickListener {
            Toast.makeText(requireContext(), "Función de cambiar foto próximamente", Toast.LENGTH_SHORT).show()
        }

        btnGuardar.setOnClickListener {
            Toast.makeText(requireContext(), "Cambios guardados correctamente", Toast.LENGTH_SHORT).show()
        }

        rowHistorial.setOnClickListener {
            Toast.makeText(requireContext(), "Ir al historial (pendiente)", Toast.LENGTH_SHORT).show()
        }

        rowCerrarSesion.setOnClickListener {
            // Asumiendo que 'LoginActivity' existe y es el destino de cierre de sesión
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }

    /**
     * Alterna la visibilidad del contenido de edición de perfil y rota el icono de flecha.
     */
    private fun toggleProfileContent() {
        if (contentEditarPerfil.visibility == View.GONE) {
            // Mostrar contenido
            contentEditarPerfil.visibility = View.VISIBLE
            // Rotar el icono 180 grados (flecha hacia arriba)
            iconExpandir.animate().rotation(180f).setDuration(200).start()
        } else {
            // Ocultar contenido
            contentEditarPerfil.visibility = View.GONE
            // Rotar el icono a 0 grados (flecha hacia abajo)
            iconExpandir.animate().rotation(0f).setDuration(200).start()
        }
    }
}