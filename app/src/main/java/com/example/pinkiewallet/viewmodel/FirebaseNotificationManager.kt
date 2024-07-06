package com.example.pinkiewallet.viewmodel

import com.example.pinkiewallet.model.Observer
import com.example.pinkiewallet.model.Subject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class FirebaseNotificationManager : Subject {
    private val observers = mutableListOf<Observer>()
    private val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    init {
        setupFirebaseObserver()
    }

    private fun setupFirebaseObserver() {
        // Daftarkan observer ke Firebase Realtime Database untuk mendapatkan notifikasi
        val notificationListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val message = snapshot.value.toString()
                notifyObservers(message)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        }

        // Contoh path di Firebase Realtime Database
        val notificationsRef = usersRef.child(firebaseUser?.uid ?: "").child("notifications")
        notificationsRef.addValueEventListener(notificationListener)
    }

    override fun registerObserver(observer: Observer) {
        observers.add(observer)
    }

    override fun removeObserver(observer: Observer) {
        observers.remove(observer)
    }

    override fun notifyObservers(message: String) {
        observers.forEach { it.update(message) }
    }

    // Metode untuk mengirim notifikasi ke Firebase Realtime Database atau Firestore
    fun sendNotification(message: String) {
        // Simpan notifikasi ke Firebase
        val notificationsRef = usersRef.child(firebaseUser?.uid ?: "").child("notifications")
        notificationsRef.push().setValue(message)
    }
}
