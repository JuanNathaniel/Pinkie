package com.example.pinkiewallet.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pinkiewallet.R
import com.example.pinkiewallet.databinding.ActivityHistoryBinding
import com.example.pinkiewallet.model.Transaction
import com.example.pinkiewallet.view.adapter.TransactionAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var rvTransaction: RecyclerView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide the support action bar
        supportActionBar?.hide()

        rvTransaction = findViewById(R.id.rv_Transaction)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        rvTransaction.layoutManager = LinearLayoutManager(this)

        // Tambahkan DividerItemDecoration di sini
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        val dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider_line)
        if (dividerDrawable != null) {
            itemDecoration.setDrawable(dividerDrawable)
        }
        rvTransaction.addItemDecoration(itemDecoration)
        binding.backwardButton.setOnClickListener {
            val intent = Intent(this@HistoryActivity, MainActivity::class.java)
            startActivity(intent)
            finish() // Menutup activity Payment
        }
        fetchUserDetails()
    }

    private fun fetchUserDetails() {
        database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userMap = mutableMapOf<String, String>()
                for (snapshot in dataSnapshot.children) {
                    val userId = snapshot.key
                    val phoneNumber = snapshot.child("phone_number").getValue(String::class.java)
                    if (userId != null && phoneNumber != null) {
                        userMap[userId] = phoneNumber
                    }
                }
                fetchTransactions(userMap)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("HistoryActivity", "Failed to load user details", databaseError.toException())
            }
        })
    }

    private fun fetchTransactions(userMap: Map<String, String>) {
        val userId = mAuth.currentUser?.uid
        if (userId == null) {
            Log.e("HistoryActivity", "User ID is null")
            return
        }

        val transactionsRef = database.child("transactions")
        transactionsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val transactions = mutableListOf<Transaction>()
                for (snapshot in dataSnapshot.children) {
                    val transaction = snapshot.getValue(Transaction::class.java)
                    if (transaction != null && (transaction.from == userId || transaction.to == userId)) {
                        // Map user IDs to phone numbers
                        transaction.from = userMap[transaction.from] ?: transaction.from
                        transaction.to = userMap[transaction.to] ?: transaction.to
                        transactions.add(transaction)
                    }
                }
                rvTransaction.adapter = TransactionAdapter(transactions)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("HistoryActivity", "Failed to load transactions", databaseError.toException())
            }
        })
    }
}
