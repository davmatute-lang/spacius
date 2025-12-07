package com.example.spacius

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificationHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationHistoryAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyView: TextView

    private val viewModel: NotificationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification_history, container, false)

        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_view_notifications)
        progressBar = view.findViewById(R.id.progressBar_notifications)
        emptyView = view.findViewById(R.id.empty_view_notifications)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = NotificationHistoryAdapter(emptyList()) // Start with an empty list
        recyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar.visibility = View.VISIBLE

        viewModel.allNotifications.observe(viewLifecycleOwner) { notifications ->
            progressBar.visibility = View.GONE
            if (notifications.isEmpty()) {
                emptyView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.updateData(notifications)
            }
        }
    }
}
