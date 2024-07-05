package com.example.pinkiewallet.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Register1ViewModel : ViewModel() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }

    fun saveNamaLengkapToDatabase(namaLengkap: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            val userRef = database.getReference("users").child(userId)
            userRef.child("nama_lengkap").setValue(namaLengkap)
                .addOnSuccessListener {
                    onSuccess.invoke()
                }
                .addOnFailureListener { e ->
                    onFailure.invoke("Gagal menyimpan data: ${e.message}")
                }
        } else {
            onFailure.invoke("User tidak ditemukan")
        }
    }
}
