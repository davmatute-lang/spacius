package com.example.spacius

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.spacius.data.FirestoreRepository
import com.example.spacius.data.LugarFirestore
import com.example.spacius.data.ReservaFirestore
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var recyclerLugares: RecyclerView
    private lateinit var recyclerReservas: RecyclerView
    private lateinit var firestoreRepository: FirestoreRepository
    private val lugares = mutableListOf<LugarFirestore>()
    private val reservas = mutableListOf<ReservaFirestore>()
    private var mensajeView: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerLugares = view.findViewById(R.id.recyclerLugares)
        recyclerLugares.layoutManager = LinearLayoutManager(requireContext())

        recyclerReservas = view.findViewById(R.id.recyclerReservas)
        recyclerReservas.layoutManager = LinearLayoutManager(requireContext())

        firestoreRepository = FirestoreRepository()

        return view
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    private fun cargarDatos() {
        lifecycleScope.launch {
            try {
                // Inicializar lugares predefinidos si es la primera vez
                firestoreRepository.inicializarLugaresPredefinidos()
                
                // Cargar lugares y reservas en paralelo
                val lugaresDisponibles = firestoreRepository.obtenerLugaresDisponibles()
                val reservasUsuario = firestoreRepository.obtenerReservasUsuario()

                // Actualizar lista de lugares
                lugares.clear()
                lugares.addAll(lugaresDisponibles)
                if (recyclerLugares.adapter == null) {
                    recyclerLugares.adapter = LugarAdapter(
                        lugares,
                        onReservarClick = { lugar -> lanzarDetalleReserva(lugar) },
                        onFavoritoClick = { lugar -> toggleFavorito(lugar) }
                    )
                } else {
                    recyclerLugares.adapter?.notifyDataSetChanged()
                }
                mostrarEstadoLugares(lugaresDisponibles.isEmpty())

                // Actualizar lista de reservas
                reservas.clear()
                reservas.addAll(reservasUsuario)
                if (recyclerReservas.adapter == null) {
                    recyclerReservas.adapter = ReservaAdapter(reservas) { reserva ->
                        cancelarReserva(reserva)
                    }
                } else {
                    recyclerReservas.adapter?.notifyDataSetChanged()
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar datos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun toggleFavorito(lugar: LugarFirestore) {
        val nuevoEstado = !lugar.esFavorito
        lugar.esFavorito = nuevoEstado

        lifecycleScope.launch {
            val exito = firestoreRepository.actualizarFavorito(lugar.id, nuevoEstado)
            if (exito) {
                val index = lugares.indexOfFirst { it.id == lugar.id }
                if (index != -1) {
                    recyclerLugares.adapter?.notifyItemChanged(index)
                }
            } else {
                lugar.esFavorito = !nuevoEstado
            }
        }
    }

    private fun cancelarReserva(reserva: ReservaFirestore) {
        lifecycleScope.launch {
            try {
                val exito = firestoreRepository.cancelarReserva(reserva.id)
                if (exito) {
                    Toast.makeText(requireContext(), "Reserva cancelada", Toast.LENGTH_SHORT).show()
                    cargarDatos() // Recargar datos para actualizar las listas
                } else {
                    Toast.makeText(requireContext(), "Error al cancelar la reserva", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarEstadoLugares(sinLugares: Boolean) {
        if (sinLugares) {
            recyclerLugares.visibility = View.GONE

            if (mensajeView == null) {
                mensajeView = TextView(requireContext()).apply {
                    text = "😔 No hay lugares disponibles en este momento\n\n" +
                            "Por favor, contacta al administrador o " +
                            "intenta nuevamente más tarde."
                    textSize = 16f
                    setTextColor(resources.getColor(android.R.color.black, null))
                    gravity = android.view.Gravity.CENTER
                    setPadding(32, 64, 32, 64)
                }

                (recyclerLugares.parent as? ViewGroup)?.addView(mensajeView)
            } else {
                mensajeView?.visibility = View.VISIBLE
            }
        } else {
            recyclerLugares.visibility = View.VISIBLE
            mensajeView?.visibility = View.GONE
        }
    }

    private fun lanzarDetalleReserva(lugar: LugarFirestore) {
        val fragment = ReservaFragment()
        fragment.arguments = Bundle().apply {
            putString("lugarId", lugar.id)
            putString("nombreLugar", lugar.nombre)
            putString("descripcion", lugar.descripcion)
            putString("fecha", lugar.fechaDisponible)
            putString("categoria", lugar.categoria)
            putString("imagenUrl", lugar.imagenUrl)
            putDouble("latitud", lugar.latitud)
            putDouble("longitud", lugar.longitud)
            putInt("capacidad", lugar.capacidadMaxima)
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    inner class ReservaAdapter(
        private val reservas: List<ReservaFirestore>,
        private val onCancelarClick: (ReservaFirestore) -> Unit
    ) : RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder>() {

        inner class ReservaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imgLugar: ImageView = itemView.findViewById(R.id.imgLugarReserva)
            val txtNombreLugar: TextView = itemView.findViewById(R.id.txtNombreLugarReserva)
            val txtFecha: TextView = itemView.findViewById(R.id.txtFechaReserva)
            val txtHora: TextView = itemView.findViewById(R.id.txtHoraReserva)
            val btnCancelar: Button = itemView.findViewById(R.id.btnCancelarReservaItem)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
                ReservaViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reserva, parent, false)
            return ReservaViewHolder(view)
        }

        override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
            val reserva = reservas[position]
            holder.txtNombreLugar.text = reserva.lugarNombre
            holder.txtFecha.text = "Fecha: ${reserva.fecha}"
            holder.txtHora.text = "Hora: ${reserva.horaInicio} - ${reserva.horaFin}"
            holder.btnCancelar.setOnClickListener { onCancelarClick(reserva) }

            Glide.with(holder.itemView.context)
                .load(reserva.imagenUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgLugar)
        }

        override fun getItemCount() = reservas.size
    }

    inner class LugarAdapter(
        private val lugares: List<LugarFirestore>,
        private val onReservarClick: (LugarFirestore) -> Unit,
        private val onFavoritoClick: (LugarFirestore) -> Unit
    ) : RecyclerView.Adapter<LugarAdapter.LugarViewHolder>() {

        inner class LugarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imgLugar: ImageView = itemView.findViewById(R.id.imgLugar)
            val txtNombre: TextView = itemView.findViewById(R.id.txtNombreLugar)
            val txtDescripcion: TextView = itemView.findViewById(R.id.txtDescripcionLugar)
            val txtCapacidad: TextView = itemView.findViewById(R.id.txtCapacidad)
            val btnReservar: Button = itemView.findViewById(R.id.btnReservar)
            val btnFavorito: ImageButton = itemView.findViewById(R.id.btnFavorito)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            LugarViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_lugar, parent, false))

        override fun onBindViewHolder(holder: LugarViewHolder, position: Int) {
            val lugar = lugares[position]
            holder.txtNombre.text = lugar.nombre
            holder.txtDescripcion.text = lugar.descripcion

            val capacidadInfo = if (lugar.capacidadMaxima > 0) {
                " Capacidad: ${lugar.capacidadMaxima} personas"
            } else {
                "🏢 Espacio disponible"
            }

            val disponibilidadInfo = lugar.fechaDisponible.takeIf { it.isNotEmpty() } ?: "Disponible"
            holder.txtCapacidad.text = "$capacidadInfo • 📅 $disponibilidadInfo"

            Glide.with(holder.itemView.context)
                .load(lugar.imagenUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgLugar)

            holder.btnReservar.setOnClickListener { onReservarClick(lugar) }

            holder.btnFavorito.setOnClickListener { onFavoritoClick(lugar) }

            if (lugar.esFavorito) {
                holder.btnFavorito.setImageResource(R.drawable.ic_star_filled)
            } else {
                holder.btnFavorito.setImageResource(R.drawable.ic_star_border)
            }
        }

        override fun getItemCount() = lugares.size
    }
}
