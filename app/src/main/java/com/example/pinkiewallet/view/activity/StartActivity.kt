package com.example.pinkiewallet.view.activity

import com.example.pinkiewallet.view.fragment.Register1
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.pinkiewallet.view.fragment.StartFragment
import com.example.pinkiewallet.databinding.ActivityStartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")

        // Check if user is already logged in
        if (mAuth.currentUser != null) {
            // Check user's status in the database
            val userId = mAuth.currentUser!!.uid
            usersRef.child(userId).child("status").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val status = snapshot.getValue(String::class.java)
                    if (status == "logout") {
                        // User is logged out, navigate to StartFragment
                        navigateToStartFragment()
                    } else {
                        // User is logged in, navigate to MainActivity
                        navigateToMainActivity()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    navigateToStartFragment() // Default to StartFragment in case of error
                }
            })
        } else {
            // No user is logged in, navigate to StartFragment
            navigateToStartFragment()
        }

        // Check if intent is from Register
        if (intent.getBooleanExtra("FROM_REGISTER", false)) {
            supportFragmentManager.commit {
                replace(binding.fragmentContainer.id, Register1())
                addToBackStack(null)
            }
        }
    }

    private fun navigateToStartFragment() {
        supportFragmentManager.commit {
            replace(binding.fragmentContainer.id, StartFragment())
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
