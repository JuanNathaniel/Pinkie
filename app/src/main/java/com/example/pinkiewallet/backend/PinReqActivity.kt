package com.example.pinkiewallet.backend

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
import com.example.pinkiewallet.view.fragment.Transfer
import com.example.pinkiewallet.viewmodel.PinViewModel
import com.example.pinkiewallet.viewmodel.TransferViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.security.MessageDigest

class PinReqActivity : AppCompatActivity() {

    private lateinit var pinInput: EditText
    private lateinit var fab: FloatingActionButton

    private val pinViewModel: PinViewModel by viewModels()
    private val transferViewModel: TransferViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pin_req)

        val caller = intent.getStringExtra("caller")
        val jumlahHarga = intent.getStringExtra("jumlah_harga")?.toInt() ?: 0
        val nomorHp = intent.getStringExtra("nomorHp") // Terima sebagai String

        pinInput = findViewById(R.id.pin_input)
        fab = findViewById(R.id.fab)

        // Log untuk memastikan nomor telepon diterima dengan benar
        Log.d("PinReqActivity", "Nomor HP: $nomorHp")
        Log.d("PinReqActivity", "Jumlah Harga: $jumlahHarga")

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
                Toast.makeText(this, "PIN berhasil diverifikasi", Toast.LENGTH_SHORT).show()

                if (caller == "TransferActivity") {
                    transferViewModel.checkRecipientPhoneAndTransfer(nomorHp ?: "", jumlahHarga, applicationContext)
                } else if (caller == "QrMain") {
                    pinViewModel.updateBalance(jumlahHarga,applicationContext)

                }
            } else {
                Toast.makeText(this, "PIN salah", Toast.LENGTH_SHORT).show()
                showKeyboard(pinInput)
            }
        })

        transferViewModel.transferResult.observe(this, Observer { transferSuccessful ->
            if (transferSuccessful) {
                navigateToPayment(jumlahHarga, "Transfer To " + nomorHp)
            } else {
                Toast.makeText(this, "Transfer gagal", Toast.LENGTH_SHORT).show()
                showKeyboard(pinInput)
                navigateToTransfer() // Mengarahkan kembali ke Transfer class (fragment)
            }
        })

        pinViewModel.balanceUpdateResult.observe(this, Observer { balanceUpdateSuccessful ->
            if (balanceUpdateSuccessful) {
                navigateToPayment(jumlahHarga, "Bayar")
            } else {
                Toast.makeText(this, "Update saldo gagal", Toast.LENGTH_SHORT).show()
                showKeyboard(pinInput)
            }
        })
    }

    private fun navigateToPayment(jumlahHarga: Int, origin: String) {
        Toast.makeText(this, "Transfer berhasil - Ke Payment", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, Payment::class.java)
        intent.putExtra("jumlah_harga", jumlahHarga)
        intent.putExtra("origin", origin) // Menambahkan asal transaksi ke intent
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun navigateToTransfer() {
        val transferFragment = Transfer()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, transferFragment)
            .addToBackStack(null)
            .commit()

        // Pastikan BottomNavigationView disembunyikan
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.visibility = View.GONE
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
}
