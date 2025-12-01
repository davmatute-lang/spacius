package com.example.spacius.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.spacius.data.NotificationService
import kotlinx.coroutines.flow.map

class NotificationViewModel : ViewModel() {

    private val notificationService = NotificationService()

    val unreadNotificationsCount = notificationService.getNotificationsFlow().map { notifications ->
        notifications.count { !it.isRead }
    }.asLiveData()
}