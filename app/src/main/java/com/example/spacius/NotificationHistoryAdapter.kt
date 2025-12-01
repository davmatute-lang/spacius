package com.example.spacius

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class NotificationHistoryAdapter(private var notifications: List<NotificationHistoryItem>) : RecyclerView.Adapter<NotificationHistoryAdapter.ViewHolder>() {

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            notifyDataSetChanged() // Update the view for the countdown
            handler.postDelayed(this, 1000) // Update every second
        }
    }

    init {
        handler.postDelayed(updateRunnable, 1000)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]
        holder.bind(notification)
    }

    override fun getItemCount() = notifications.size

    fun updateData(newNotifications: List<NotificationHistoryItem>) {
        notifications = newNotifications
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.notification_title)
        private val message: TextView = itemView.findViewById(R.id.notification_message)
        private val countdown: TextView = itemView.findViewById(R.id.notification_countdown)
        private val date: TextView = itemView.findViewById(R.id.notification_date)

        fun bind(notification: NotificationHistoryItem) {
            title.text = notification.title
            message.text = notification.message

            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            date.text = sdf.format(Date(notification.timestamp))

            // Countdown logic
            if (notification.notificationType == "reserva_creada" && notification.eventTimestamp != null && notification.eventTimestamp > System.currentTimeMillis()) {
                val diff = notification.eventTimestamp - System.currentTimeMillis()
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60

                countdown.text = String.format("Faltan: %d d, %02d h, %02d m, %02d s", days, hours, minutes, seconds)
                countdown.visibility = View.VISIBLE
            } else {
                countdown.visibility = View.GONE
            }
        }
    }

    // Clean up the handler to avoid memory leaks
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        handler.removeCallbacks(updateRunnable)
    }
}
