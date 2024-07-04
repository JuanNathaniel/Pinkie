import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.pinkiewallet.R
import com.example.pinkiewallet.Register2
import com.example.pinkiewallet.databinding.Register1Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Register1 : Fragment() {

    private var _binding: Register1Binding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Register1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

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

        binding.fab.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.grey)

        // Add TextWatcher to monitor text input changes
        binding.textFieldName.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check if text length is at least 2 characters
                val isTextValid = (s?.length ?: 0) >= 2
                // Set FAB background tint based on validation
                val colorRes = if (isTextValid) R.color.teal_200 else R.color.grey
                binding.fab.backgroundTintList = ContextCompat.getColorStateList(requireContext(), colorRes)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.fab.setOnClickListener {
            val namaLengkap = binding.textFieldName.editText?.text?.toString()

            if (!namaLengkap.isNullOrEmpty()) {
                // Simpan data ke Firebase Realtime Database
                val userId = mAuth.currentUser?.uid
                if (userId != null) {
                    val userRef = database.getReference("users").child(userId)
                    userRef.child("nama_lengkap").setValue(namaLengkap)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                            // Pindah ke fragment berikutnya atau lakukan aksi lainnya
                            val register2Fragment = Register2()
                            val transaction = requireActivity().supportFragmentManager.beginTransaction()
                            transaction.replace(R.id.fragment_container, register2Fragment)
                            transaction.addToBackStack(null) // Untuk menambahkan ke back stack, jika diperlukan
                            transaction.commit()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "User tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Harap masukkan nama lengkap", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
