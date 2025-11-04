package com.example.spacius

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var historyAdapter: HistoryAdapter
    private val historyEvents = mutableListOf<HistoryEvent>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_history)
        recyclerView.layoutManager = LinearLayoutManager(context)
        historyAdapter = HistoryAdapter(historyEvents)
        recyclerView.adapter = historyAdapter

        loadHistoryEvents()
    }

    private fun loadHistoryEvents() {
        // Aquí cargarías los eventos del historial desde tu base de datos o API
        historyEvents.add(HistoryEvent("Reserva Creada", "Espacio de Coworking 1", "20/05/2024"))
        historyEvents.add(HistoryEvent("Reserva Cancelada", "Sala de Reuniones B", "18/05/2024"))
        historyEvents.add(HistoryEvent("Reserva Creada", "Oficina Privada 3", "15/05/2024"))
        historyAdapter.notifyDataSetChanged()
    }
}
