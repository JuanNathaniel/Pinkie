package com.example.pinkiewallet.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.pinkiewallet.R
import com.example.pinkiewallet.databinding.Register1Binding
import com.example.pinkiewallet.viewmodel.Register1ViewModel

class Register1 : Fragment() {

    private var _binding: Register1Binding? = null
    private val binding get() = _binding!!

    private val viewModel: Register1ViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Register1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backwardButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.fab.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.grey)

        binding.textFieldName.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isTextValid = (s?.length ?: 0) >= 2
                val colorRes = if (isTextValid) R.color.teal_200 else R.color.grey
                binding.fab.backgroundTintList = ContextCompat.getColorStateList(requireContext(), colorRes)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.fab.setOnClickListener {
            val namaLengkap = binding.textFieldName.editText?.text?.toString()

            if (!namaLengkap.isNullOrEmpty()) {
                viewModel.saveNamaLengkapToDatabase(namaLengkap,
                    onSuccess = {
                        Toast.makeText(requireContext(), "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                        val register2Fragment = Register2()
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, register2Fragment)
                            .addToBackStack(null)
                            .commit()
                    },
                    onFailure = { errorMessage ->
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
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

