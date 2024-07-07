package com.example.pinkiewallet.backend

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pinkiewallet.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener

class TransferActivity : AppCompatActivity() {
    private lateinit var etRecipientPhone: EditText
    private lateinit var etAmount: EditText
    private lateinit var btnTransfer: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        etRecipientPhone = findViewById(R.id.etRecipientPhone)
        etAmount = findViewById(R.id.etAmount)
        btnTransfer = findViewById(R.id.btnTransfer)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Ambil nomor telepon dari intent dan set ke etRecipientPhone
        val phoneNumber = intent.getStringExtra("nomor_telepon")
        if (phoneNumber != null) {
            etRecipientPhone.setText(phoneNumber)
        }

        btnTransfer.setOnClickListener {
            val recipientPhone = etRecipientPhone.text.toString()
            val amountStr = etAmount.text.toString()

            if (recipientPhone.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(applicationContext, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                val amount = amountStr.toInt()
                transferMoney(recipientPhone, amount)
            }
        }
    }

    private fun transferMoney(recipientPhone: String, amount: Int) {
        val senderId = mAuth.currentUser?.uid ?: return

        database.child("users").child(senderId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val senderBalance = dataSnapshot.child("balance").getValue(Int::class.java)
                    if (senderBalance != null) {
                        if (senderBalance >= amount) {
                            val recipientRef = database.child("users")
                            recipientRef.orderByChild("phone_number").equalTo(recipientPhone).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(recipientSnapshot: DataSnapshot) {
                                    if (recipientSnapshot.exists()) {
                                        val recipientId = recipientSnapshot.children.iterator().next().key
                                        val recipientBalance = recipientId?.let {
                                            recipientSnapshot.child(it).child("balance").getValue(Int::class.java)
                                        }
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
                                        } else {
                                            Toast.makeText(applicationContext, "Saldo penerima tidak ditemukan", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(applicationContext, "Nomor penerima tidak ditemukan", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    Log.e("FirebaseDB", "Gagal mencari nomor penerima", databaseError.toException())
                                }
                            })
                        } else {
                            Toast.makeText(applicationContext, "Saldo tidak cukup", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "Saldo pengirim tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Pengirim tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseDB", "Gagal mencari saldo pengirim", databaseError.toException())
            }
        })
    }
}