package com.example.spacius.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.spacius.MainActivity
import com.example.spacius.R
import com.example.spacius.data.AppDatabase
import com.example.spacius.data.Reserva
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.util.Calendar

class ReservaFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var latLugar: Double = -2.170998
    private var lngLugar: Double = -79.922359

    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reserva_exitosa, container, false)

        val txtNombre: TextView = view.findViewById(R.id.btnNombreLugar)
        val txtDescripcion: TextView = view.findViewById(R.id.btnDescripcionLugar)
        val txtCapacidad: TextView = view.findViewById(R.id.btnCapacidad)
        val imgLugar: ImageView = view.findViewById(R.id.imgLugar)

        val btnSeleccionarFecha: TextView = view.findViewById(R.id.btnFecha)
        val btnHoraInicio: TextView = view.findViewById(R.id.btnHoraInicio)
        val btnHoraFin: TextView = view.findViewById(R.id.btnHoraFin)

        val btnReservar: Button = view.findViewById(R.id.btnReservar)
        val btnCancelar: Button = view.findViewById(R.id.btnCancelar)

        db = AppDatabase.getDatabase(requireContext())

        // Cargar datos del lugar seleccionado
        arguments?.let {
            txtNombre.text = it.getString("nombreLugar")
            txtDescripcion.text = it.getString("descripcion")
            txtCapacidad.text = "Disponibilidad: ${it.getString("fecha")} - ${it.getString("hora")}"

            Glide.with(this).load(it.getString("imagenUrl"))
                .placeholder(R.drawable.ic_launcher_background)
                .into(imgLugar)

            latLugar = it.getDouble("latitud")
            lngLugar = it.getDouble("longitud")
        }

        // Selección de fecha
        btnSeleccionarFecha.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                btnSeleccionarFecha.text = "$d/${m + 1}/$y"
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Selección de hora inicio
        btnHoraInicio.setOnClickListener {
            val c = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, h, m ->
                btnHoraInicio.text = String.format("%02d:%02d", h, m)
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }

        // Selección de hora fin
        btnHoraFin.setOnClickListener {
            val c = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, h, m ->
                btnHoraFin.text = String.format("%02d:%02d", h, m)
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }

        // Botón Reservar
        btnReservar.setOnClickListener {
            val fechaSeleccionada = btnSeleccionarFecha.text.toString()
            val horaInicioSeleccionada = btnHoraInicio.text.toString()
            val horaFinSeleccionada = btnHoraFin.text.toString()

            if (fechaSeleccionada.isBlank() || fechaSeleccionada == "Seleccionar fecha") {
                Toast.makeText(requireContext(), "Por favor, selecciona una fecha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔹 Llamamos a MainActivity para marcar fecha visualmente
            (requireActivity() as MainActivity).marcarFechaEnCalendario(fechaSeleccionada)

            // 🔹 Guardamos la reserva en la base de datos
            lifecycleScope.launch {
                val reserva = Reserva(
                    idLugar = arguments?.getInt("idLugar") ?: 0,
                    fecha = fechaSeleccionada,
                    horaInicio = horaInicioSeleccionada,
                    horaFin = horaFinSeleccionada,
                    nombreUsuario = "UsuarioSimulado"
                )
                db.reservaDao().insertReserva(reserva)
            }

            Toast.makeText(requireContext(), "¡Reserva realizada!", Toast.LENGTH_SHORT).show()
        }

        // Botón Cancelar
        btnCancelar.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Cargar mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragmentContainer) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction()
                    .replace(R.id.mapFragmentContainer, it)
                    .commit()
            }
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val lugar = LatLng(latLugar, lngLugar)
        map.clear()
        map.addMarker(MarkerOptions().position(lugar).title("Ubicación del Lugar"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lugar, 15f))
        map.uiSettings.isZoomControlsEnabled = true
    }
}
