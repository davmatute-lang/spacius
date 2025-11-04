package com.example.spacius

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
        private val eventTypeTextView: TextView = itemView.findViewById(R.id.text_view_event_type)
        private val spaceNameTextView: TextView = itemView.findViewById(R.id.text_view_space_name)
        private val dateTextView: TextView = itemView.findViewById(R.id.text_view_date)

        fun bind(event: HistoryEvent) {
            eventTypeTextView.text = event.eventType
            spaceNameTextView.text = event.spaceName
            dateTextView.text = event.date
        }
    }
}
