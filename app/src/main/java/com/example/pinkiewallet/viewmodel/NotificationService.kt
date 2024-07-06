package com.example.pinkiewallet.viewmodel

import android.os.Message
import com.example.pinkiewallet.model.Observer
import com.example.pinkiewallet.model.Subject
import com.example.pinkiewallet.model.User
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message

class NotificationService : Subject<String> {
    private val observers = mutableListOf<Observer<String>>()

    override fun addObserver(observer: Observer<String>) {
        observers.add(observer)
    }

    override fun removeObserver(observer: Observer<String>) {
        observers.remove(observer)
    }

    override fun notifyObservers(data: String) {
        for (observer in observers) {
            observer.update(data)
            if (observer is User) {
                sendNotificationToFCM(observer.getFcmToken(), data)
            }
        }
    }

    fun sendNotification(message: String) {
        notifyObservers(message)
    }

    private fun sendNotificationToFCM(token: String, message: String) {
        val notificationMessage = Message.builder()
            .putData("message", message)
            .setToken(token)
            .build()

        try {
            val response = FirebaseMessaging.getInstance().send(notificationMessage)
            println("Successfully sent message: $response")
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle exception
        }
    }
}