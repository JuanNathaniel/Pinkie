package com.example.pinkiewallet

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.pinkiewallet.databinding.Register2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest

class Register2 : Fragment() {

    private var _binding: Register2Binding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Register2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val pinViews = listOf(
            binding.pinCircle1,
            binding.pinCircle2,
            binding.pinCircle3,
            binding.pinCircle4,
            binding.pinCircle5,
            binding.pinCircle6
        )

        // Set initial background tint (example with grey)
        binding.fab.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.grey)
        binding.fab.isEnabled = false

        binding.pinInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updatePinCircles(pinViews, s)
                // Check if PIN is complete (length == 6)
                val isPinComplete = s?.length == 6
                binding.fab.isEnabled = isPinComplete

                // Set background tint list based on completion status
                val colorRes = if (isPinComplete) R.color.teal_200 else R.color.grey
                binding.fab.backgroundTintList = ContextCompat.getColorStateList(requireContext(), colorRes)
            }

            override fun afterTextChanged(s: Editable?) {
                // Check if PIN length is less than 6 after text change
                if ((s?.length ?: 0) < 6) {
                    binding.fab.isEnabled = false
                    binding.fab.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.grey)
                }
            }
        })

        // Set click listeners for pin circles
        pinViews.forEach { view ->
            view.setOnClickListener {
                binding.pinInput.requestFocus()
                showKeyboard(binding.pinInput)
            }
        }

        // Request focus and show keyboard after view is ready
        binding.pinInput.requestFocus()
        Handler(Looper.getMainLooper()).postDelayed({
            showKeyboard(binding.pinInput)
        }, 100)

        binding.fab.setOnClickListener {
            val pin = binding.pinInput.text.toString()

            if (pin.length == 6) {
                // Hash the PIN before saving
                val hashedPin = hashPin(pin)

                // Save hashed PIN to Firebase Realtime Database
                val userId = mAuth.currentUser?.uid
                if (userId != null) {
                    val userRef = database.getReference("users").child(userId)
                    userRef.child("pin").setValue(hashedPin)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "PIN berhasil disimpan", Toast.LENGTH_SHORT).show()
                            // Move to next fragment or perform other actions
                            val register3Fragment = Register3()
                            val transaction = requireActivity().supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.fragment_container, register3Fragment)
                            transaction.addToBackStack(null) // To add to back stack if needed
                            transaction.commit()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Gagal menyimpan PIN", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "User tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Harap masukkan 6 digit PIN", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backwardButton.setOnClickListener {
            // Mendapatkan instance dari FragmentManager
            val fragmentManager = requireActivity().supportFragmentManager

            // Cek apakah ada fragment sebelumnya di dalam back stack
            if (fragmentManager.backStackEntryCount > 0) {
                // Navigasi kembali ke fragment sebelumnya
                fragmentManager.popBackStack()
            } else {
                // Tidak ada fragment sebelumnya dalam back stack, bisa tambahkan logika lain jika diperlukan
            }

            // Hancurkan fragment saat ini
            fragmentManager.beginTransaction().remove(this).commit()
        }
    }

    private fun updatePinCircles(pinViews: List<View>, s: CharSequence?) {
        for (i in pinViews.indices) {
            if (i < (s?.length ?: 0)) {
                pinViews[i].setBackgroundResource(R.drawable.circle_filled)  // Gunakan background lingkaran terisi
            } else {
                pinViews[i].setBackgroundResource(R.drawable.circle_background)  // Gunakan background lingkaran kosong
            }
        }
    }

    private fun showKeyboard(view: View) {
        val imm = requireContext().getSystemService(InputMethodManager::class.java) as InputMethodManager
        view.post {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(pin.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
