package com.example.pinkiewallet.backend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.pinkiewallet.R
import com.example.pinkiewallet.view.activity.ContactActivity
import com.example.pinkiewallet.view.activity.MainActivity
import com.example.pinkiewallet.viewmodel.TransferViewModel
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
    private lateinit var btnContact: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val transferViewModel: TransferViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)


        etRecipientPhone = findViewById(R.id.etRecipientPhone)
        etAmount = findViewById(R.id.etAmount)
        btnTransfer = findViewById(R.id.btnTransfer)
        btnContact = findViewById(R.id.btnContact)

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
                val amount = amountStr
                val nomor = recipientPhone
                initiatePinRequest(nomor, amount)
            }
        }

        btnContact.setOnClickListener {
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
        }

        transferViewModel.transferResult.observe(this, Observer { transferSuccessful ->
            if (transferSuccessful) {
                Toast.makeText(this, "Transfer berhasil - Ke Payment", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Payment::class.java)
                intent.putExtra("jumlah_harga", etAmount.text.toString())
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                Toast.makeText(this, "Transfer gagal", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initiatePinRequest(nomorHP: String, jumlahHarga: String) {
        val pinReqIntent = Intent(this, PinReqActivity::class.java)
        pinReqIntent.putExtra("caller", "TransferActivity")
        pinReqIntent.putExtra("jumlah_harga", jumlahHarga)
        pinReqIntent.putExtra("nomorHp", nomorHP) // Pastikan dikirim sebagai String
        startActivity(pinReqIntent)
    }
}