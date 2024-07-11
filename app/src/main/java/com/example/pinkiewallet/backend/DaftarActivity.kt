package com.example.pinkiewallet.backend


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pinkiewallet.R

class DaftarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar)
        supportActionBar?.hide()
    }
}