package com.example.pinkiewallet.model

data class Transaction(
    var id: String? = null,
    var amount: Double? = null,
    var from: String? = null,
    var to: String? = null,
    var timestamp: Long? = null
) {
    // No-argument constructor required for Firebase
    constructor() : this(null, null, null, null, null)
}
