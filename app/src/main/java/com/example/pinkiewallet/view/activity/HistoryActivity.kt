package com.example.pinkiewallet.view.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pinkiewallet.R
import com.example.pinkiewallet.model.Transaction
import com.example.pinkiewallet.view.adapter.TransactionAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var rvTransaction: RecyclerView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        rvTransaction = findViewById(R.id.rv_Transaction)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        rvTransaction.layoutManager = LinearLayoutManager(this)

        fetchTransactions()
    }

    private fun fetchTransactions() {
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
