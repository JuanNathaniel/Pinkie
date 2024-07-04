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
import com.example.pinkiewallet.R
import com.example.pinkiewallet.databinding.OtpVerificationBinding

class OtpVerification : Fragment() {

    private var _binding: OtpVerificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var etOtp1: EditText
    private lateinit var etOtp2: EditText
    private lateinit var etOtp3: EditText
    private lateinit var etOtp4: EditText
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
        btnVerify = binding.btnVerify

        // Set initial background tint (example with grey)
        binding.btnVerify.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.grey)
        binding.btnVerify.isEnabled = false

        // Handle OTP EditText input changes
        val otpEditTexts = arrayOf(etOtp1, etOtp2, etOtp3, etOtp4)
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
            val otp = "${etOtp1.text}${etOtp2.text}${etOtp3.text}${etOtp4.text}"
            // Process OTP here, for example validate it or send to server for verification
            if (otp.length == 4) {
                // Handle OTP validation logic here, e.g., send OTP to server for verification
                Toast.makeText(requireContext(), "Verifying OTP: $otp", Toast.LENGTH_SHORT).show()
                // Example of manual verification
                // verifyOtpManually(otp)
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

        binding.btnVerify.isEnabled = otp1.isNotEmpty() && otp2.isNotEmpty() && otp3.isNotEmpty() && otp4.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
