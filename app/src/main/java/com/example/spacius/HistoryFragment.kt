package com.example.spacius

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.spacius.data.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var firestoreRepository: FirestoreRepository
    private lateinit var auth: FirebaseAuth
    
    private val historyEvents = mutableListOf<HistoryEvent>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view_history)
        emptyTextView = view.findViewById(R.id.textViewEmptyHistory)
        progressBar = view.findViewById(R.id.progressBarHistory)
        swipeRefresh = view.findViewById(R.id.swipeRefreshHistory)
        
        auth = FirebaseAuth.getInstance()
        firestoreRepository = FirestoreRepository()
        
        setupRecyclerView()
        setupSwipeRefresh()
        loadHistoryEvents()
    }
    
    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        historyAdapter = HistoryAdapter(historyEvents)
        recyclerView.adapter = historyAdapter
    }
    
    private fun setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener {
            loadHistoryEvents()
        }
        swipeRefresh.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light
        )
    }

    private fun loadHistoryEvents() {
        progressBar.isVisible = true
        swipeRefresh.isRefreshing = false
        
        lifecycleScope.launch {
            try {
                // Cargar reservas del usuario desde Firebase
                val reservas = firestoreRepository.obtenerReservasUsuario()
                
                historyEvents.clear()
                
                // Convertir reservas a eventos del historial
                reservas.forEach { reserva ->
                    val eventType = when {
                        reserva.estado == "cancelada" -> "Reserva Cancelada"
                        reserva.estado == "completada" -> "Reserva Completada"
                        else -> "Reserva Creada"
                    }
                    
                    val fecha = reserva.fecha.ifEmpty { "Fecha desconocida" }
                    historyEvents.add(
                        HistoryEvent(
                            eventType = eventType,
                            spaceName = reserva.lugarNombre,
                            date = fecha
                        )
                    )
                }
                
                // Ordenar por fecha (más recientes primero)
                historyEvents.sortByDescending { it.date }
                
                if (historyEvents.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyTextView.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    emptyTextView.visibility = View.GONE
                }
                
                historyAdapter.notifyDataSetChanged()
                
            } catch (e: Exception) {
                Toast.makeText(context, "Error al cargar historial: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.isVisible = false
            }
        }
    }
}
