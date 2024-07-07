package com.example.pinkiewallet.backend

import PinViewModel
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.pinkiewallet.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.security.MessageDigest

class PinReqActivity : AppCompatActivity() {

    private lateinit var pinInput: EditText
    private lateinit var fab: FloatingActionButton
    private var jumlahHarga: Int = 0

    private val pinViewModel: PinViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pin_req)

        jumlahHarga = intent.getIntExtra("jumlah_harga", 0)

        pinInput = findViewById(R.id.pin_input)
        fab = findViewById(R.id.fab)

        pinInput.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                pinInput.viewTreeObserver.removeOnGlobalLayoutListener(this)
                pinInput.requestFocus()
                showKeyboard(pinInput)
            }
        })

        fab.setOnClickListener {
            val pin = pinInput.text.toString()
            if (pin.length == 6) {
                val hashedPin = hashPin(pin)
                pinViewModel.verifyPin(pin, hashedPin)
            } else {
                Toast.makeText(this, "Please enter a 6-digit PIN", Toast.LENGTH_SHORT).show()
                showKeyboard(pinInput)
            }
        }

        pinInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updatePinCircles(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        pinViewModel.pinVerificationResult.observe(this, Observer { isVerified ->
            if (isVerified) {
                Toast.makeText(this, "PIN verified successfully", Toast.LENGTH_SHORT).show()
                pinViewModel.updateBalance(jumlahHarga)
            } else {
                Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show()
                showKeyboard(pinInput)
            }
        })

        pinViewModel.balanceUpdateResult.observe(this, Observer { isSuccess ->
            if (isSuccess) {
                Toast.makeText(this, "Balance updated successfully", Toast.LENGTH_SHORT).show()
                openPaymentActivity(jumlahHarga)
            } else {
                Toast.makeText(this, "Failed to update balance", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updatePinCircles(pin: CharSequence?) {
        val pinCircles = listOf<View>(
            findViewById(R.id.pin_circle_1),
            findViewById(R.id.pin_circle_2),
            findViewById(R.id.pin_circle_3),
            findViewById(R.id.pin_circle_4),
            findViewById(R.id.pin_circle_5),
            findViewById(R.id.pin_circle_6)
        )

        for (i in pinCircles.indices) {
            if (i < pin?.length ?: 0) {
                pinCircles[i].setBackgroundResource(R.drawable.circle_filled)
            } else {
                pinCircles[i].setBackgroundResource(R.drawable.circle_background)
            }
        }
    }

    private fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(pin.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }



    private fun showKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        view.post {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun openPaymentActivity(jumlahHarga: Int) {
        val paymentIntent = Intent(this, Payment::class.java)
        paymentIntent.putExtra("jumlah_harga", jumlahHarga)
        startActivity(paymentIntent)
        finish()
    }
}
