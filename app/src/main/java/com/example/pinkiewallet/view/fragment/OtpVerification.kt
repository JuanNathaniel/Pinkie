package com.example.pinkiewallet.view.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.pinkiewallet.R
import com.example.pinkiewallet.databinding.OtpVerificationBinding
import com.example.pinkiewallet.view.activity.MainActivity
import com.example.pinkiewallet.viewmodel.OtpVerificationViewModel

class OtpVerification : Fragment() {

    private var _binding: OtpVerificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var etOtp1: EditText
    private lateinit var etOtp2: EditText
    private lateinit var etOtp3: EditText
    private lateinit var etOtp4: EditText
    private lateinit var etOtp5: EditText
    private lateinit var etOtp6: EditText

    private val viewModel: OtpVerificationViewModel by viewModels()

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

        // Observe isNewUser LiveData to navigate accordingly
        viewModel.isNewUser.observe(viewLifecycleOwner) { isNewUser ->
            if (isNewUser) {
                navigateToRegister1()
            } else {
                navigateToMainActivity()
            }
        }

        // Handle verify button click
        binding.btnVerify.setOnClickListener {
            val otp = "${etOtp1.text}${etOtp2.text}${etOtp3.text}${etOtp4.text}${etOtp5.text}${etOtp6.text}"
            // Process OTP here, for example validate it or send to server for verification
            if (otp.length == 6) {
                // Handle OTP validation logic here, e.g., send OTP to server for verification
                viewModel.verifyOtp(
                    verificationId = requireArguments().getString("verificationId").toString(),
                    otp = otp,
                    phoneNumber = requireArguments().getString("phoneNumber").toString(),
                    onSuccess = {
                        // Handle successful verification, no need to navigate directly here
                        Toast.makeText(requireContext(), "Verifikasi Berhasil", Toast.LENGTH_SHORT).show()
                    },
                    onError = { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(requireContext(), "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle back button click
        binding.backwardButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

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

    private fun navigateToMainActivity() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun navigateToRegister1() {
        val fragment = Register1()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null) // Menambahkan ke back stack jika diperlukan
        transaction.commit()
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
