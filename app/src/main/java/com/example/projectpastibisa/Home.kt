package com.example.projectpastibisa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Home : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var textViewRp: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userRef: DatabaseReference
    private lateinit var balanceListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        // Inisialisasi Firebase
        mAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        // Inisialisasi TextView
        textViewRp = findViewById(R.id.textViewRp)

        // Mendapatkan referensi pengguna yang sedang login
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            userRef = databaseReference.child("users").child(userId)
            // Membuat dan menambahkan ValueEventListener untuk saldo
            balanceListener = createBalanceListener()
            userRef.addValueEventListener(balanceListener)
        } else {
            Toast.makeText(applicationContext, "Pengguna tidak terautentikasi", Toast.LENGTH_SHORT).show()
        }

        //Favorite Transaction
        val horizontalRecyclerViewFav = findViewById<RecyclerView>(R.id.horizontalRecyclerViewFavoriteTransaction)
        val itemListFav: MutableList<String> = ArrayList()
        // Tambahkan item ke dalam daftar
        itemListFav.add("Item 1")
        itemListFav.add("Item 2")
        itemListFav.add("Item 3")
        itemListFav.add("Item 4")

        if (itemListFav.isEmpty()) {
            // Daftar item kosong, lakukan sesuatu di sini jika diperlukan
            Toast.makeText(this, "Daftar item kosong", Toast.LENGTH_SHORT).show()
        } else {
            // Daftar item tidak kosong, kirimkan ke adapter
            val adapter = HorizontalAdapter(itemListFav)
            horizontalRecyclerViewFav.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            horizontalRecyclerViewFav.adapter = adapter
        }

        //Insight
        val horizontalRecyclerView = findViewById<RecyclerView>(R.id.horizontalRecyclerView)
        val itemList: MutableList<String> = ArrayList()
        // Tambahkan item ke dalam daftar
        itemList.add("Item 1")
        itemList.add("Item 2")
        itemList.add("Item 3")
        itemList.add("Item 4")

        if (itemList.isEmpty()) {
            // Daftar item kosong, lakukan sesuatu di sini jika diperlukan
            Toast.makeText(this, "Daftar item kosong", Toast.LENGTH_SHORT).show()
        } else {
            // Daftar item tidak kosong, kirimkan ke adapter
            val adapter = HorizontalAdapter(itemList)
            horizontalRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            horizontalRecyclerView.adapter = adapter
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home_menu -> {
                    val intent = Intent(this@Home, CreateQR::class.java)
                    startActivity(intent)
                    true
                }
                R.id.search_menu -> {
                    val intent = Intent(this@Home, QrMain::class.java)
                    startActivity(intent)
                    true
                }
                R.id.account_menu -> {
                    val intent = Intent(this@Home, TransferActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun createBalanceListener(): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val balance = dataSnapshot.child("balance").getValue(Long::class.java)
                    textViewRp.text = "Saldo Anda: Rp $balance"
                } else {
                    Toast.makeText(applicationContext, "Pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseDB", "Gagal mengambil saldo", databaseError.toException())
                Toast.makeText(applicationContext, "Gagal mengambil saldo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Menghapus listener untuk saldo saat activity dihancurkan
        userRef.removeEventListener(balanceListener)
    }
}
