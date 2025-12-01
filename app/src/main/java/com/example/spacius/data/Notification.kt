package com.example.spacius.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Notification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    @ServerTimestamp
    val timestamp: Date? = null,
    val isRead: Boolean = false
)