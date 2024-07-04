package com.example.pinkiewallet

import OtpVerification
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.pinkiewallet.databinding.LoginRegisterBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.TimeUnit

class LoginRegister : Fragment() {

    private var _binding: LoginRegisterBinding? = null
    private val binding get() = _binding!!

    var isNewUser: Boolean = false
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

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
//            if (isPhoneNumberValid(binding.textFieldPhone.toString())) {
//
//                //disini kondisi pengecekan apakah nomor telepon ada di database atau engga
//                val register1Fragment = Register1()
//                val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
//                transaction.replace(R.id.fragment_container, register1Fragment)
//                transaction.addToBackStack(null) // Untuk menambahkan ke back stack, jika diperlukan
//                transaction.commit()
//                Toast.makeText(requireContext(), "Bisa nih mantap", Toast.LENGTH_SHORT).show()
//            }
            if (!binding.textFieldPhone.isEmpty()) {
                checkIfUserExists(binding.textFieldPhone.editText?.text.toString())
//                bar.visibility = View.VISIBLE
//                llOtp.visibility = View.VISIBLE
            } else {
                Toast.makeText(requireContext(), "Ada Data Yang Masih Kosong", Toast.LENGTH_SHORT).show()
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

    private fun checkIfUserExists(noHp: String) {
        val database = FirebaseDatabase.getInstance().reference;

        val userRef = database.child("users")
        userRef.orderByChild("phone_number").equalTo(noHp).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    isNewUser = false
                    Toast.makeText(requireContext(), "Nomor terdaftar, lanjutkan ke login", Toast.LENGTH_SHORT).show()
                } else {
                    isNewUser = true
                    Toast.makeText(requireContext(), "Nomor belum terdaftar, lanjutkan ke registrasi", Toast.LENGTH_SHORT).show()
                }
                kodeOTP(noHp)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseDB", "Gagal memeriksa nomor telepon", databaseError.toException())
            }
        })
    }

    private fun kodeOTP(noHp: String) {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber("+62$noHp")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(mCallbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            loginByCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.e("OTP", "Verifikasi gagal", e)
            Toast.makeText(requireContext(), "Verifikasi Gagal", Toast.LENGTH_SHORT).show()
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(verificationId, token)
            Log.d("OTP", "Kode OTP terkirim: $verificationId")
            Toast.makeText(requireContext(), "Mengirim Kode OTP", Toast.LENGTH_SHORT).show()
            // Tambahkan logika UI di sini jika diperlukan, misalnya tampilkan input OTP
            val otpVerification = OtpVerification()
            val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, otpVerification)
            transaction.addToBackStack(null) // Untuk menambahkan ke back stack, jika diperlukan
            transaction.commit()
        }
    }

    private fun loginByCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    if (isNewUser) {
                        savePhoneNumberToDatabase()
                    } else {
                        Log.d("Auth", "Login Berhasil")
                        Toast.makeText(requireContext(), "Login Berhasil", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                } else {
                    Log.e("Auth", "Verifikasi gagal", task.exception)
                    Toast.makeText(requireContext(), "Verifikasi Gagal", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun savePhoneNumberToDatabase() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        userRef.child("phone_number").setValue(binding.textFieldPhone.editText?.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseDB", "Nomor telepon berhasil disimpan untuk userId: $userId")
                    Toast.makeText(requireContext(), "Registrasi Berhasil", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), Register1::class.java))
                    requireActivity().finish()
                } else {
                    Log.e("FirebaseDB", "Gagal menyimpan nomor telepon untuk userId: $userId", task.exception)
                    Toast.makeText(requireContext(), "Gagal menyimpan nomor telepon", Toast.LENGTH_SHORT).show()
                }
            }
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
