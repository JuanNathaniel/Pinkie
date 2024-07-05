package com.example.pinkiewallet

import Register1
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.pinkiewallet.databinding.ActivityStartBinding
import com.google.firebase.auth.FirebaseAuth

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        // Check if user is already logged in
        if (mAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(binding.fragmentContainer.id, StartFragment())
            }
        }

        // Cek apakah berasal dari Register
        if (intent.getBooleanExtra("FROM_REGISTER", false)) {
            supportFragmentManager.commit {
                replace(binding.fragmentContainer.id, Register1())
                addToBackStack(null)
            }
        }
    }
}