package com.example.pinkiewallet.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

data class User(
    var userId: String = "",
    val fullName: String,
    val phoneNumber: String,
    var fcmToken: String = "", // Tambahkan ini untuk FCM token
    var balance: Double = 0.0, // Saldo e-wallet, diinisialisasi dengan 0.0
    var status: String = "logout" // Status login, defaultnya logout
) : Observer {

    init {
        // Ambil ID pengguna dari Firebase Authentication
        val currentUser = FirebaseAuth.getInstance().currentUser
        userId = currentUser?.uid ?: ""

        // Dapatkan dan simpan token FCM
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                fcmToken = task.result ?: ""
                // Simpan atau gunakan token FCM sesuai kebutuhan
            } else {
                // Handle token retrieval failure
            }
        }
    }

    override fun update(message: String) {
        println("User $userId received notification: $message")
        // Di sini Anda bisa menambahkan logika untuk menangani notifikasi yang diterima
    }
}
