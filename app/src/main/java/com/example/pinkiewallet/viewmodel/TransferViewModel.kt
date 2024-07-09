package com.example.pinkiewallet.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class TransferViewModel : ViewModel() {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val _transferResult = MutableLiveData<Boolean>()
    val transferResult: LiveData<Boolean> = _transferResult

    fun checkRecipientPhoneAndTransfer(recipientPhone: String, amount: Int, applicationContext: Context) {
        val senderId = mAuth.currentUser?.uid ?: return

        Log.d("TransferViewModel", "Sender ID: $senderId")
        Log.d("TransferViewModel", "Recipient Phone: $recipientPhone")

        database.child("users").child(senderId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val senderBalance = dataSnapshot.child("balance").getValue(Int::class.java)
                    if (senderBalance != null && senderBalance >= amount) {
                        val recipientRef = database.child("users")
                        recipientRef.orderByChild("phone_number").equalTo(recipientPhone).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(recipientSnapshot: DataSnapshot) {
                                Log.d("TransferViewModel", "Recipient Snapshot: $recipientSnapshot")

                                if (recipientSnapshot.exists()) {
                                    val recipientId = recipientSnapshot.children.iterator().next().key
                                    Log.d("TransferViewModel", "Recipient ID: $recipientId")

                                    if (recipientId != null) {
                                        updateBalancesAndRecordTransaction(senderId, recipientId, senderBalance, amount, applicationContext)
                                    } else {
                                        Toast.makeText(applicationContext, "Nomor penerima tidak ditemukan", Toast.LENGTH_SHORT).show()
                                        _transferResult.value = false
                                    }
                                } else {
                                    Toast.makeText(applicationContext, "Nomor penerima tidak ditemukan", Toast.LENGTH_SHORT).show()
                                    _transferResult.value = false
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.e("FirebaseDB", "Gagal mencari nomor penerima", databaseError.toException())
                                _transferResult.value = false
                            }
                        })
                    } else {
                        Toast.makeText(applicationContext, "Saldo tidak cukup atau tidak ditemukan", Toast.LENGTH_SHORT).show()
                        _transferResult.value = false
                    }
                } else {
                    Toast.makeText(applicationContext, "Pengirim tidak ditemukan", Toast.LENGTH_SHORT).show()
                    _transferResult.value = false
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseDB", "Gagal mencari saldo pengirim", databaseError.toException())
                _transferResult.value = false
            }
        })
    }

    private fun updateBalancesAndRecordTransaction(senderId: String, recipientId: String, senderBalance: Int, amount: Int, applicationContext: Context) {
        val recipientRef = database.child("users").child(recipientId)
        recipientRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(recipientSnapshot: DataSnapshot) {
                val recipientBalance = recipientSnapshot.child("balance").getValue(Int::class.java)
                if (recipientBalance != null) {
                    // Update sender and recipient balances
                    database.child("users").child(senderId).child("balance").setValue(senderBalance - amount)
                    database.child("users").child(recipientId).child("balance").setValue(recipientBalance + amount)

                    // Save transaction record
                    val transactionRef = database.child("transactions").push()
                    transactionRef.child("from").setValue(senderId)
                    transactionRef.child("to").setValue(recipientId)
                    transactionRef.child("amount").setValue(amount)
                    transactionRef.child("timestamp").setValue(ServerValue.TIMESTAMP)

                    Toast.makeText(applicationContext, "Transfer berhasil", Toast.LENGTH_SHORT).show()
                    _transferResult.value = true
                } else {
                    Toast.makeText(applicationContext, "Saldo penerima tidak ditemukan", Toast.LENGTH_SHORT).show()
                    _transferResult.value = false
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseDB", "Gagal mencari saldo penerima", databaseError.toException())
                _transferResult.value = false
            }
        })
    }
}
