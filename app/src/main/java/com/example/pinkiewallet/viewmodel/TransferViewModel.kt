package com.example.pinkiewallet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class TransferViewModel : ViewModel() {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    private val _transferResult = MutableLiveData<Boolean>()
    val transferResult: LiveData<Boolean> = _transferResult

    fun transferMoney(recipientPhone: String, amount: Int) {
        val senderId = mAuth.currentUser?.uid
        if (senderId != null) {
            val senderRef = database.getReference("users").child(senderId)
            senderRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(senderSnapshot: DataSnapshot) {
                    if (senderSnapshot.exists()) {
                        val senderBalance = senderSnapshot.child("balance").getValue(Int::class.java) ?: 0
                        if (senderBalance >= amount) {
                            val recipientRef = database.getReference("users")
                            recipientRef.orderByChild("phone").equalTo(recipientPhone)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(recipientSnapshot: DataSnapshot) {
                                        if (recipientSnapshot.exists()) {
                                            val recipientId = recipientSnapshot.children.iterator().next().key
                                            if (recipientId != null) {
                                                val recipientBalance = recipientSnapshot.child(recipientId).child("balance").getValue(Int::class.java) ?: 0

                                                // Update sender and recipient balances
                                                senderRef.child("balance").setValue(senderBalance - amount)
                                                recipientRef.child(recipientId).child("balance").setValue(recipientBalance + amount)

                                                // Save transaction record
                                                val transactionRef = database.getReference("transactions").push()
                                                transactionRef.child("from").setValue(senderId)
                                                transactionRef.child("to").setValue(recipientId)
                                                transactionRef.child("amount").setValue(amount)
                                                transactionRef.child("timestamp").setValue(ServerValue.TIMESTAMP)

                                                _transferResult.value = true
                                            } else {
                                                _transferResult.value = false
                                            }
                                        } else {
                                            _transferResult.value = false
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        _transferResult.value = false
                                    }
                                })
                        } else {
                            _transferResult.value = false
                        }
                    } else {
                        _transferResult.value = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _transferResult.value = false
                }
            })
        } else {
            _transferResult.value = false
        }
    }
}
