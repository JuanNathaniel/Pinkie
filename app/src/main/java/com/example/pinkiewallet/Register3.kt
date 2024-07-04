package com.example.pinkiewallet

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.pinkiewallet.databinding.Register3Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Register3 : Fragment(){

    private var _binding: Register3Binding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Register3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.backwardButton.setOnClickListener{
            val fragmentManager = requireActivity().supportFragmentManager
            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStack()
            } else {
                // Handle jika tidak ada fragment sebelumnya di back stack
            }
            fragmentManager.beginTransaction().remove(this).commit()
        }

        binding.fab.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.grey)

        // Function untuk validasi format email menggunakan regex
        fun isEmailValid(email: String): Boolean {
            val emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
            return email.matches(emailPattern.toRegex())
        }

        // Add TextWatcher untuk memonitor perubahan input teks
        binding.textFieldEmail.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = binding.textFieldEmail.editText?.text.toString().trim()
                val colorRes = if (isEmailValid(email)) R.color.teal_200 else R.color.grey
                binding.fab.backgroundTintList = ContextCompat.getColorStateList(requireContext(), colorRes)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.fab.setOnClickListener {
            val email = binding.textFieldEmail.editText?.text.toString().trim()

            if (isEmailValid(email)) {
                // Email format valid, simpan ke Firebase Realtime Database
                val userId = mAuth.currentUser?.uid
                if (userId != null) {
                    val userRef = database.getReference("users").child(userId)
                    userRef.child("email").setValue(email)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Email berhasil disimpan", Toast.LENGTH_SHORT).show()
                            // Contoh navigasi ke activity lain setelah simpan berhasil
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish() // Opsional, tutup activity saat ini setelah navigasi
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Gagal menyimpan email", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "User tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Email format tidak valid
                Toast.makeText(requireContext(), "Format email tidak valid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
