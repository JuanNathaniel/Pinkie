package com.example.pinkiewallet

import Register1
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.pinkiewallet.databinding.OtpVerificationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OtpVerification : Fragment() {

    private var _binding: OtpVerificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var verificationId: String
    private lateinit var phoneNumber: String
    private lateinit var etOtp1: EditText
    private lateinit var etOtp2: EditText
    private lateinit var etOtp3: EditText
    private lateinit var etOtp4: EditText
    private lateinit var etOtp5: EditText
    private lateinit var etOtp6: EditText
    private lateinit var btnVerify: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = OtpVerificationBinding.inflate(inflater, container, false)

        // Initialize views
        etOtp1 = binding.etOtp1
        etOtp2 = binding.etOtp2
        etOtp3 = binding.etOtp3
        etOtp4 = binding.etOtp4
        etOtp5 = binding.etOtp5
        etOtp6 = binding.etOtp6
        btnVerify = binding.btnVerify

        // Set initial background tint (example with grey)
        binding.btnVerify.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.grey)
        binding.btnVerify.isEnabled = false

        // Handle OTP EditText input changes
        val otpEditTexts = arrayOf(etOtp1, etOtp2, etOtp3, etOtp4, etOtp5, etOtp6)
        for (i in otpEditTexts.indices) {
            otpEditTexts[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    validateOtpFields()
                    if (s?.length == 1 && i < otpEditTexts.size - 1) {
                        otpEditTexts[i + 1].requestFocus()
                    } else if (s?.length == 0 && i > 0) {
                        otpEditTexts[i - 1].requestFocus()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        // Handle verify button click
        btnVerify.setOnClickListener {
            val otp = "${etOtp1.text}${etOtp2.text}${etOtp3.text}${etOtp4.text}${etOtp5.text}${etOtp6.text}"
            // Process OTP here, for example validate it or send to server for verification
            if (otp.length == 6) {
                // Handle OTP validation logic here, e.g., send OTP to server for verification
                verifyOtpManually(otp)
            } else {
                Toast.makeText(requireContext(), "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle back button click
        binding.backwardButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Get verification ID and phone number from arguments
        verificationId = requireArguments().getString("verificationId").toString()
        phoneNumber = requireArguments().getString("phoneNumber").toString()

        return binding.root
    }


    private fun validateOtpFields() {
        val otp1 = etOtp1.text.toString()
        val otp2 = etOtp2.text.toString()
        val otp3 = etOtp3.text.toString()
        val otp4 = etOtp4.text.toString()
        val otp5 = etOtp5.text.toString()
        val otp6 = etOtp6.text.toString()

        val isAllFieldsFilled = otp1.isNotEmpty() && otp2.isNotEmpty() && otp3.isNotEmpty() &&
                otp4.isNotEmpty() && otp5.isNotEmpty() && otp6.isNotEmpty()

        binding.btnVerify.isEnabled = isAllFieldsFilled

        if (isAllFieldsFilled) {
            binding.btnVerify.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.teal_200)
        } else {
            binding.btnVerify.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.grey)
        }
    }

    private fun verifyOtpManually(otp: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // OTP verification successful
                    Toast.makeText(requireContext(), "Verifikasi Berhasil", Toast.LENGTH_SHORT).show()
                    val phoneNumber = arguments?.getString("phoneNumber") ?: ""
                    checkUserExistence(phoneNumber)
                } else {
                    // OTP verification failed
                    Toast.makeText(requireContext(), "Verifikasi Gagal", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserExistence(phoneNumber: String) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        val query = usersRef.orderByChild("phone_number").equalTo(phoneNumber)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val status = userSnapshot.child("status").getValue(String::class.java)
                        if (status == "login") {
                            // User is already logged in on another device
                            Toast.makeText(requireContext(), "User already logged in on another device", Toast.LENGTH_SHORT).show()
                        } else {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                            if (userId.isNotEmpty()) {
                                val database = FirebaseDatabase.getInstance()
                                val usersRef = database.getReference("users")
                                usersRef.child(userId).child("status").setValue("login")
                                navigateToMainActivity()
                            }
                        }
                    }
                } else {
                    savePhoneNumberToDatabase(phoneNumber)
                    navigateToRegister1(phoneNumber)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("OtpVerification", "Failed to check user existence: ${error.message}")
                Toast.makeText(requireContext(), "Failed to check user existence", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun navigateToRegister1(phoneNumber: String) {
        val fragment = Register1()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun savePhoneNumberToDatabase(phoneNumber: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userId.isNotEmpty()) {
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("users")
            usersRef.child(userId).child("phone_number").setValue(phoneNumber)
            usersRef.child(userId).child("balance").setValue(0)
            usersRef.child(userId).child("status").setValue("login")
            usersRef.child(userId).child("point").setValue(0)
                .addOnSuccessListener {
                    Log.d("OtpVerification", "Phone number saved to database")
                }
                .addOnFailureListener { e ->
                    Log.e("OtpVerification", "Error saving phone number to database", e)
                    Toast.makeText(requireContext(), "Failed to save phone number", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.e("OtpVerification", "User ID is empty")
        }
    }



    private fun navigateToMainActivity() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(verificationId: String, phoneNumber: String): OtpVerification {
            val fragment = OtpVerification()
            val args = Bundle()
            args.putString("verificationId", verificationId)
            args.putString("phoneNumber", phoneNumber)
            fragment.arguments = args
            return fragment
        }
    }

}
