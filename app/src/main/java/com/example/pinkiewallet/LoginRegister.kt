import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.pinkiewallet.OtpVerification
import com.example.pinkiewallet.R
import com.example.pinkiewallet.databinding.LoginRegisterBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginRegister : Fragment() {

    private var _binding: LoginRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var phoneNumber: String

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
            if (!binding.textFieldPhone.isEmpty()) {
                phoneNumber = binding.textFieldPhone.editText?.text.toString()
                sendOtpToPhoneNumber(phoneNumber)
            } else {
                Toast.makeText(requireContext(), "Ada Data Yang Masih Kosong", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backwardButton.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun sendOtpToPhoneNumber(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber("+62$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(mCallbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Handle verification completed (auto-retrieval) if needed
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(requireContext(), "Verifikasi Gagal", Toast.LENGTH_SHORT).show()
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(verificationId, token)
            val otpVerification = OtpVerification.newInstance(verificationId, phoneNumber)
            val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, otpVerification)
            transaction.addToBackStack(null)
            transaction.commit()
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
        return phoneNumber.length > 9 // Contoh validasi, misalnya nomor telepon harus 10 digit atau lebih
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
