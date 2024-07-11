package com.example.pinkiewallet.view.activity

import android.content.Intent
import android.os.Bundle

import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pinkiewallet.R
import com.example.pinkiewallet.backend.QrMain
import com.example.pinkiewallet.backend.ScannedBarcodeActivity
import com.example.pinkiewallet.databinding.ActivityMainBinding
import com.example.pinkiewallet.model.User
//import com.example.pinkiewallet.view.fragment.Transfer
import com.example.pinkiewallet.viewmodel.FirebaseNotificationManager
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var firebaseNotificationManager: FirebaseNotificationManager
    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_notifications // Exclude navigation_dashboard
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    val intent = Intent(this, ScannedBarcodeActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> {
                    navController.navigate(item.itemId)
                    true
                }
            }
        }

//        supportFragmentManager.addOnBackStackChangedListener {
//            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
//            if (currentFragment is Transfer) {
//                navView.visibility = View.GONE
//            } else {
//                navView.visibility = View.VISIBLE
//            }
//        }
    }
}
