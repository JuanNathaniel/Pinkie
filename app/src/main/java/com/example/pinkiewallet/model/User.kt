package com.example.pinkiewallet.model

data class User(
    val userId: String,
    val fullName: String,
    val phoneNumber: String,
    val fcmToken: String, // Tambahkan ini untuk FCM token
    var balance: Double = 0.0, // Saldo e-wallet, diinisialisasi dengan 0.0
    var status: String = "logout" // Status login, defaultnya logout
) : Observer<String> {
    override fun update(data: String) {
        println("Notifikasi untuk $fullName: $data")
        // FCM push notification handled by FirebaseMessagingService
    }

    fun getFcmToken(): String {
        return fcmToken
    }

}

