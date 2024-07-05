package com.example.pinkiewallet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pinkiewallet.model.User

class UserViewModel : ViewModel() {

    // Data user yang akan dikelola oleh ViewModel
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    // Metode untuk mengubah data user
    fun setUser(newUser: User) {
        _user.value = newUser
    }

    // Metode untuk mendapatkan data user saat ini
    fun getUser(): User? {
        return _user.value
    }

    // Metode untuk memperbarui saldo e-wallet user
    fun updateBalance(newBalance: Double) {
        val currentUser = _user.value
        currentUser?.balance = newBalance
        _user.value = currentUser
    }

}