package com.example.pinkiewallet.model

data class User(
    val userId: String,
    val fullName: String,
    val phoneNumber: String,
    var balance: Double = 0.0, // Saldo e-wallet, diinisialisasi dengan 0.0
    var status: String = "logout" // Status login, defaultnya logout
)