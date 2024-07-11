package com.example.pinkiewallet.backend;

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.pinkiewallet.R
import com.example.pinkiewallet.backend.Payment
import com.example.pinkiewallet.backend.PinReqActivity
import com.example.pinkiewallet.databinding.TransferBinding
import com.example.pinkiewallet.viewmodel.TransferViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TransferActivity : AppCompatActivity() {

    private lateinit var binding: TransferBinding

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private var balanceListener: ValueEventListener? = null

    private val transferViewModel: TransferViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TransferBinding.inflate(layoutInflater) // Updated to use the new layout file name
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")

        setupUI()
        initFirebase()

        // Hide BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.visibility = View.GONE

        binding.backwardButton.setOnClickListener {
            finish()
            bottomNavigationView.visibility = View.VISIBLE
        }

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
                val amount = amountStr
                initiatePinRequest(recipientPhone, amount)
            }
        }

        transferViewModel.transferResult.observe(this, Observer { transferSuccessful ->
            if (transferSuccessful) {
                Toast.makeText(this, "Transfer berhasil - Ke Payment", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Payment::class.java)
                intent.putExtra("jumlah_harga", binding.etTransferAmount.text.toString())
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                Toast.makeText(this, "Transfer gagal", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initFirebase() {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            balanceListener = createBalanceListener()
            usersRef.child(userId).addValueEventListener(balanceListener!!)
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
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseDB", "Failed to retrieve balance", databaseError.toException())
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
                    val amount = amountString.toLong()
                    val formattedAmount = formatToRupiah(amount)
                    binding.jumlahpembayaran.text = formattedAmount
                    binding.btnContinue.setBackgroundColor(ContextCompat.getColor(this@TransferActivity, R.color.teal_200))
                } else {
                    binding.jumlahpembayaran.text = "Rp0"
                    binding.btnContinue.setBackgroundColor(ContextCompat.getColor(this@TransferActivity, android.R.color.darker_gray))
                }
            }
        })

        binding.btnContinue.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
    }

    private fun formatToRupiah(amount: Long): String {
        val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("id", "ID"))
        return formatter.format(amount).replace("Rp", "Rp ")
    }

    private fun initiatePinRequest(nomorHP: String, jumlahHarga: String) {
        val pinReqIntent = Intent(this, PinReqActivity::class.java)
        pinReqIntent.putExtra("caller", "TransferActivity")
        pinReqIntent.putExtra("jumlah_harga", jumlahHarga)
        pinReqIntent.putExtra("nomorHp", nomorHP)
        startActivity(pinReqIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        balanceListener?.let {
            mAuth.currentUser?.uid?.let { userId ->
                usersRef.child(userId).removeEventListener(it)
            }
        }
    }
}
