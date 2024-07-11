package com.example.pinkiewallet.backend

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.pinkiewallet.R
import com.example.pinkiewallet.databinding.ActivityTransferBinding
import com.example.pinkiewallet.databinding.TransferBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class TransferActivity : AppCompatActivity() {

    private lateinit var binding: TransferBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")

        setupUI()
        initFirebase()

        binding.backwardButton.setOnClickListener {
            finish()
        }

        // Ambil nomor telepon dari intent dan set ke etRecipientPhone
        val phoneNumber = intent.getStringExtra("nomor_telepon")
        if (phoneNumber != null) {
            binding.nomorPenerima.setText(phoneNumber)
        }

        binding.btnContinue.setOnClickListener {
            val recipientPhone = binding.nomorPenerima.text.toString()
            val amountStr = binding.etTransferAmount.text.toString()

            if (recipientPhone.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                val amount = amountStr.toInt()
                transferMoney(recipientPhone, amount)
            }
        }
    }

    private fun transferMoney(recipientPhone: String, amount: Int) {
        val senderId = mAuth.currentUser?.uid

        usersRef.child(senderId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val senderBalanceObj = dataSnapshot.child("balance").getValue(Int::class.java)
                    val senderBalance = senderBalanceObj ?: 0

                    if (senderBalance >= amount) {
                        val recipientRef = usersRef
                        recipientRef.orderByChild("phone_number").equalTo(recipientPhone).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(recipientSnapshot: DataSnapshot) {
                                if (recipientSnapshot.exists()) {
                                    val recipientId = recipientSnapshot.children.iterator().next().key
                                    val recipientBalanceObj = recipientSnapshot.child(recipientId!!).child("balance").getValue(Int::class.java)
                                    val recipientBalance = recipientBalanceObj ?: 0

                                    // Update sender and recipient balances
                                    usersRef.child(senderId).child("balance").setValue(senderBalance - amount)
                                    usersRef.child(recipientId).child("balance").setValue(recipientBalance + amount)

                                    // Save transaction record
                                    val transactionRef = usersRef.child("transactions").push()
                                    transactionRef.child("from").setValue(senderId)
                                    transactionRef.child("to").setValue(recipientId)
                                    transactionRef.child("amount").setValue(amount)
                                    transactionRef.child("timestamp").setValue(ServerValue.TIMESTAMP)

                                    Toast.makeText(this@TransferActivity, "Transfer berhasil", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@TransferActivity, "Nomor penerima tidak ditemukan", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.e("FirebaseDB", "Gagal mencari nomor penerima", databaseError.toException())
                            }
                        })
                    } else {
                        Toast.makeText(this@TransferActivity, "Saldo tidak cukup", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@TransferActivity, "Pengirim tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseDB", "Gagal mencari saldo pengirim", databaseError.toException())
            }
        })
    }

    private fun initFirebase() {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            // Create and add ValueEventListener for balance
            val balanceListener = createBalanceListener()
            usersRef.child(userId).addValueEventListener(balanceListener)
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createBalanceListener(): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val balance = dataSnapshot.child("balance").getValue(Long::class.java)
                    val formattedBalance = formatToRupiah(balance ?: 0)
                    binding.saldodapatditransfer.text = "Saldo yang Dapat Ditransfer: $formattedBalance"
                } else {
                    Toast.makeText(this@TransferActivity, "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseDB", "Failed to retrieve balance", databaseError.toException())
                Toast.makeText(this@TransferActivity, "Failed to retrieve balance", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupUI() {
        binding.etTransferAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val amountString = s.toString()
                if (amountString.isNotEmpty()) {
                    // Parse the amount to Long to remove any leading zeros
                    val amount = amountString.toLong()

                    // Format amount to Indonesian Rupiah format
                    val formattedAmount = formatToRupiah(amount)

                    // Set formatted amount to TextView
                    binding.jumlahpembayaran.text = formattedAmount

                    // Set button background color
                    binding.btnContinue.setBackgroundColor(ContextCompat.getColor(this@TransferActivity, R.color.teal_200))
                } else {
                    binding.jumlahpembayaran.text = "Rp0"
                    binding.btnContinue.setBackgroundColor(ContextCompat.getColor(this@TransferActivity, android.R.color.darker_gray))
                }
            }
        })
        // Set initial button color to gray
        binding.btnContinue.setBackgroundColor(ContextCompat.getColor(this@TransferActivity, android.R.color.darker_gray))
    }

    private fun formatToRupiah(amount: Long): String {
        val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("id", "ID"))
        return formatter.format(amount).replace("Rp", "Rp ")
    }
}
