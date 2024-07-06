package com.example.pinkiewallet.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pinkiewallet.model.User

class NotificationViewModel : ViewModel() {
    private val notificationService = NotificationService()

    fun addObserver(user: User) {
        notificationService.addObserver(user)
    }

    fun removeObserver(user: User) {
        notificationService.removeObserver(user)
    }

    fun sendNotification(message: String) {
        notificationService.sendNotification(message)
    }
}