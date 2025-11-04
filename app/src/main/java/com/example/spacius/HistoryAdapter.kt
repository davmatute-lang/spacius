package com.example.spacius

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(private val historyEvents: List<HistoryEvent>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history_event, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val event = historyEvents[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int = historyEvents.size

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventIcon: ImageView = itemView.findViewById(R.id.ivEventIcon)
        private val eventTypeTextView: TextView = itemView.findViewById(R.id.text_view_event_type)
        private val spaceNameTextView: TextView = itemView.findViewById(R.id.text_view_space_name)
        private val dateTextView: TextView = itemView.findViewById(R.id.text_view_date)

        fun bind(event: HistoryEvent) {
            eventTypeTextView.text = event.eventType
            spaceNameTextView.text = event.spaceName
            dateTextView.text = event.date
            
            // Cambiar icono y color según el tipo de evento
            when {
                event.eventType.contains("Creada", ignoreCase = true) -> {
                    eventIcon.setImageResource(android.R.drawable.ic_input_add)
                    eventIcon.setColorFilter(ContextCompat.getColor(itemView.context, android.R.color.holo_green_dark))
                }
                event.eventType.contains("Cancelada", ignoreCase = true) -> {
                    eventIcon.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
                    eventIcon.setColorFilter(ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark))
                }
                event.eventType.contains("Completada", ignoreCase = true) -> {
                    eventIcon.setImageResource(android.R.drawable.checkbox_on_background)
                    eventIcon.setColorFilter(ContextCompat.getColor(itemView.context, android.R.color.holo_blue_dark))
                }
                else -> {
                    eventIcon.setImageResource(android.R.drawable.ic_menu_today)
                    eventIcon.setColorFilter(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))
                }
            }
        }
    }
}
