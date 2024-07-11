package com.example.pinkiewallet.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener

class PinViewModel : ViewModel() {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    private val _pinVerificationResult = MutableLiveData<Boolean>()
    val pinVerificationResult: LiveData<Boolean> = _pinVerificationResult

    private val _balanceUpdateResult = MutableLiveData<Boolean>()
    val balanceUpdateResult: LiveData<Boolean> = _balanceUpdateResult

    fun verifyPin(inputPin: String, hashedInputPin: String) {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            val databaseReference = database.getReference("users").child(userId).child("pin")

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val storedHashedPin = snapshot.getValue(String::class.java)
                    _pinVerificationResult.value = hashedInputPin == storedHashedPin
                }

                override fun onCancelled(error: DatabaseError) {
                    _pinVerificationResult.value = false
                }
            })
        } else {
            _pinVerificationResult.value = false
        }
    }

    fun updateBalance(jumlahHarga: Int, applicationContext: Context) {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            val userRef = database.getReference("users").child(userId)
            userRef.child("balance").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val currentBalance = snapshot.getValue(Int::class.java) ?: 0

                        if (currentBalance >= jumlahHarga) {
                            val newBalance = currentBalance - jumlahHarga
                            userRef.child("balance").setValue(newBalance).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    _balanceUpdateResult.value = true
                                    Toast.makeText(applicationContext, "Saldo berhasil diperbarui", Toast.LENGTH_SHORT).show()
                                    addTransactionHistory(userId, jumlahHarga, applicationContext)
                                } else {
                                    _balanceUpdateResult.value = false
                                    Toast.makeText(applicationContext, "Gagal memperbarui saldo", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            _balanceUpdateResult.value = false
                            Toast.makeText(applicationContext, "Saldo tidak mencukupi", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        _balanceUpdateResult.value = false
                        Toast.makeText(applicationContext, "Saldo tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _balanceUpdateResult.value = false
                    Toast.makeText(applicationContext, "Gagal memperbarui saldo", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            _balanceUpdateResult.value = false
            Toast.makeText(applicationContext, "User tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addTransactionHistory(userId: String, amount: Int, applicationContext: Context) {
        val transactionRef = database.getReference("transactions").push()
        val transaction = hashMapOf(
            "from" to userId,
            "to" to "Bayar",
            "amount" to amount,
            "timestamp" to ServerValue.TIMESTAMP
        )
        transactionRef.setValue(transaction).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "Riwayat transaksi berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "Gagal menambahkan riwayat transaksi", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
