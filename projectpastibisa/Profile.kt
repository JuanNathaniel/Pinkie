package com.example.projectpastibisa


import com.example.projectpastibisa.MenuAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectpastibisa.databinding.ActivityMainBinding

class Profile : AppCompatActivity() {

//    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Panggil super.onCreate() terlebih dahulu
//        binding =  ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        setContentView(R.layout.profile)

        // Inisialisasi RecyclerView pertama
        val recyclerView: RecyclerView = findViewById(R.id.rv_menu)
        // Set LayoutManager untuk RecyclerView pertama
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Data untuk ditampilkan dalam RecyclerView pertama
        val menuItems = listOf("Profile Settings", "EzCash Points", "Linked Account")
        // Buat adapter untuk RecyclerView pertama
        val adapter = MenuAdapter(menuItems) { position ->
            // Handle item click
            Toast.makeText(this, "Item clicked: ${menuItems[position]}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter
        recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER


        //////////////2
        // Inisialisasi RecyclerView kedua
        val recyclerView2: RecyclerView = findViewById(R.id.rv_help)
        // Set LayoutManager untuk RecyclerView kedua
        recyclerView2.layoutManager = LinearLayoutManager(this)
        // Data untuk ditampilkan dalam RecyclerView kedua
        val helpItems = listOf("Help1", "Help2", "Help3")
        // Buat adapter untuk RecyclerView kedua
        val adapter2 = MenuAdapter(helpItems) { position ->
            // Handle item click
            Toast.makeText(this, "Item clicked: ${helpItems[position]}", Toast.LENGTH_SHORT).show()
        }
        recyclerView2.adapter = adapter2
        recyclerView2.overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        //////////////2
        // Inisialisasi RecyclerView kedua
        val recyclerView3: RecyclerView = findViewById(R.id.rv_free)
        // Set LayoutManager untuk RecyclerView kedua
        recyclerView3.layoutManager = LinearLayoutManager(this)
        // Data untuk ditampilkan dalam RecyclerView kedua
        val free = listOf("Halow", "Hilow", "Hulow", "Helow", "Holow")
        // Buat adapter untuk RecyclerView kedua
        val adapter3 = MenuAdapter(free) { position ->
            // Handle item click
            Toast.makeText(this, "Item clicked: ${free[position]}", Toast.LENGTH_SHORT).show()
        }
        recyclerView3.adapter = adapter3
        recyclerView3.overScrollMode = RecyclerView.OVER_SCROLL_NEVER

    }


}