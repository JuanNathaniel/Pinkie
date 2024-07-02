package com.example.pinkiewallet

import android.content.Context
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
import com.example.pinkiewallet.databinding.LoginRegisterBinding

class LoginRegister : Fragment() {

    private var _binding: LoginRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoginRegisterBinding.inflate(inflater, container, false)

        // Set initial background tint (example with grey)
        binding.lanjutkanButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.grey)
        binding.lanjutkanButton.isEnabled = false

        binding.textFieldPhone.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validatePhoneNumber(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        binding.lanjutkanButton.setOnClickListener{
            if (isPhoneNumberValid(binding.textFieldPhone.toString())) {

                //disini kondisi pengecekan apakah nomor telepon ada di database atau engga
                val register1Fragment = Register1()
                val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, register1Fragment)
                transaction.addToBackStack(null) // Untuk menambahkan ke back stack, jika diperlukan
                transaction.commit()
                Toast.makeText(requireContext(), "Bisa nih mantap", Toast.LENGTH_SHORT).show()
            }
        }

        //
        binding.backwardButton.setOnClickListener{
            //do something
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

        return binding.root
    }

    private fun validatePhoneNumber(phoneNumber: String) {
        if (isPhoneNumberValid(phoneNumber)) {
            binding.lanjutkanButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.pink)
            binding.lanjutkanButton.isEnabled = true
        } else {
            binding.lanjutkanButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.grey)
            binding.lanjutkanButton.isEnabled = false
        }
    }

    private fun isPhoneNumberValid(phoneNumber: String): Boolean {
        // Replace this with your actual validation logic
        return phoneNumber.length > 10 // Contoh validasi, misalnya nomor telepon harus 10 digit
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
