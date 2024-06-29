package com.example.pinkiewallet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.pinkiewallet.databinding.Register1Binding

class Register1 : Fragment(){

    private var _binding: Register1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Register1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.fab.setOnClickListener{
            if ((binding.textFieldName.editText?.text?.length ?: 0) >= 2){
                val register2Fragment = Register2()
                val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, register2Fragment)
                transaction.addToBackStack(null) // Untuk menambahkan ke back stack, jika diperlukan
                transaction.commit()
                Toast.makeText(requireContext(), "Bisa nih mantap", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(), "Harus 2 huruf minimal deck", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}