package com.example.pinkiewallet

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

class OtpVerification : Fragment() {

    private var _binding: OtpVerificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var verificationId: String
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
        for (editText in otpEditTexts) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    validateOtpFields()
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

        // Get verification ID from arguments
        verificationId = requireArguments().getString("verificationId").toString()

        return binding.root
    }

    private fun validateOtpFields() {
        val otp1 = etOtp1.text.toString()
        val otp2 = etOtp2.text.toString()
        val otp3 = etOtp3.text.toString()
        val otp4 = etOtp4.text.toString()
        val otp5 = etOtp5.text.toString()
        val otp6 = etOtp6.text.toString()

        binding.btnVerify.isEnabled = otp1.isNotEmpty() && otp2.isNotEmpty() && otp3.isNotEmpty() &&
                otp4.isNotEmpty() && otp5.isNotEmpty() && otp6.isNotEmpty()
    }

    private fun verifyOtpManually(otp: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // OTP verification successful
                    Toast.makeText(requireContext(), "Verifikasi Berhasil", Toast.LENGTH_SHORT).show()
                    // Redirect to MainActivity or other actions
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                } else {
                    // OTP verification failed
                    Toast.makeText(requireContext(), "Verifikasi Gagal", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(verificationId: String): OtpVerification {
            val fragment = OtpVerification()
            val args = Bundle()
            args.putString("verificationId", verificationId)
            fragment.arguments = args
            return fragment
        }
    }
}
