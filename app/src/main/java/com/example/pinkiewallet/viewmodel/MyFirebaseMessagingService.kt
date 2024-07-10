package com.example.pinkiewallet.viewmodel

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle notification message
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")

            // Tampilkan notifikasi menggunakan Snackbar atau Toast di MainActivity
            // Misalnya, jika MainActivity di dalam onMessageReceived
            // Snackbar.make(findViewById(android.R.id.content), it.body ?: "", Snackbar.LENGTH_LONG).show()
        }

        // Handle data message
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

            // Di sini Anda bisa menangani payload data yang diterima jika diperlukan
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")

        // Simpan atau kirim token ke server jika diperlukan
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
