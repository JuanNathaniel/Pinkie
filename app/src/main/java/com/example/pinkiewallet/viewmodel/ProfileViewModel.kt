package com.example.pinkiewallet.viewmodel

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.pinkiewallet.view.activity.StartActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileViewModel : ViewModel() {

    fun logout(context: Context) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userId.isNotEmpty()) {
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("users")
            usersRef.child(userId).child("status").setValue("logout")
                .addOnSuccessListener {
                    navigateToStartActivity(context)
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(context, "Logout successful", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Logout failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun navigateToStartActivity(context: Context) {
        val intent = Intent(context, StartActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
