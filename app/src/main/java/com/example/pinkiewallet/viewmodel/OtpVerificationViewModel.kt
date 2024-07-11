package com.example.pinkiewallet.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*

class OtpVerificationViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.getReference("users")

    // LiveData to notify whether user is new or existing
    private val _isNewUser = MutableLiveData<Boolean>()
    val isNewUser: LiveData<Boolean>
        get() = _isNewUser

    fun verifyOtp(
        verificationId: String,
        otp: String,
        phoneNumber: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    checkUserExistence(phoneNumber, onSuccess, onError)
                } else {
                    onError("OTP verification failed")
                }
            }
    }

    private fun checkUserExistence(
        phoneNumber: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val query = usersRef.orderByChild("phone_number").equalTo(phoneNumber)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val status = userSnapshot.child("status").getValue(String::class.java)
                        if (status == "login") {
                            onError("User already logged in on another device")
                            return
                        } else {
                            handleUserLoggedIn(userSnapshot.key ?: "", onSuccess, onError)
                            return
                        }
                    }
                } else {
                    // User not found in database
                    _isNewUser.value = true // New user
                    savePhoneNumberToDatabase(phoneNumber, onSuccess, onError)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OtpVerificationViewModel", "Failed to check user existence: ${error.message}")
                onError("Failed to check user existence")
            }
        })
    }

    private fun handleUserLoggedIn(
        userId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        usersRef.child(userId).child("status").setValue("login")
            .addOnSuccessListener {
                _isNewUser.value = false // Existing user
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("OtpVerificationViewModel", "Error updating user status", e)
                onError("Failed to update user status")
            }
    }

    private fun savePhoneNumberToDatabase(
        phoneNumber: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: ""
        if (userId.isNotEmpty()) {
            usersRef.child(userId).apply {
                // Example of setting additional fields
                child("phone_number").setValue(phoneNumber)
                child("balance").setValue(0)
                child("point").setValue(0)
                child("status").setValue("login")
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        Log.e("OtpVerificationViewModel", "Error saving phone number to database", e)
                        onError("Failed to save phone number")
                    }
            }
        } else {
            onError("User ID is empty")
        }
    }
}
