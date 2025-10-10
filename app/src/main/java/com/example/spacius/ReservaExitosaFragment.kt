package com.example.spacius.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.spacius.R

class ReservaExitosaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reserva_exitosa, container, false)

        // Vincular TextViews con los IDs del XML de reserva exitosa
        val txtNombre: TextView = view.findViewById(R.id.btnNombreLugar)
        val txtDescripcion: TextView = view.findViewById(R.id.btnDescripcionLugar)
        val txtCapacidad: TextView = view.findViewById(R.id.btnCapacidad)
        val txtFecha: TextView = view.findViewById(R.id.btnFecha)
        val txtHoraInicio: TextView = view.findViewById(R.id.btnHoraInicio)
        val txtHoraFin: TextView = view.findViewById(R.id.btnHoraFin)

        val btnConfirmar: Button = view.findViewById(R.id.btnReservar)

        // Recibir datos enviados desde ReservaFragment
        arguments?.let { bundle ->
            txtNombre.text = bundle.getString("nombreLugar")
            txtDescripcion.text = bundle.getString("descripcion")
            txtCapacidad.text = bundle.getString("capacidad")
            txtFecha.text = bundle.getString("fecha")
            txtHoraInicio.text = bundle.getString("horaInicio")
            txtHoraFin.text = bundle.getString("horaFin")
        }

        btnConfirmar.setOnClickListener {
            // Volver al HomeFragment o cerrar este fragmento
            requireActivity().supportFragmentManager.popBackStack(
                null,
                androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }

        return view
    }
}




