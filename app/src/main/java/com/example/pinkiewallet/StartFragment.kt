package com.example.pinkiewallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.pinkiewallet.databinding.FragmentStartBinding

class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lanjutkanButton.setOnClickListener {
            val loginRegisterFragment = LoginRegister()
            val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, loginRegisterFragment)
            transaction.addToBackStack(null) // Untuk menambahkan ke back stack, jika diperlukan
            transaction.commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
