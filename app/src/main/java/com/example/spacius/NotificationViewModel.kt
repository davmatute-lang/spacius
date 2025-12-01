package com.example.spacius

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationHistoryDao = AppDatabase.getDatabase(application).notificationHistoryDao()

    val allNotifications: LiveData<List<NotificationHistoryItem>> = notificationHistoryDao.getAllNotifications()
}
