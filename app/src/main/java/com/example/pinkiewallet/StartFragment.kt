package com.example.pinkiewallet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pinkiewallet.backend.Register
import com.example.pinkiewallet.databinding.FragmentStartBinding

class StartFragment : Fragment() {
    private var binding: FragmentStartBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.lanjutkanButton.setOnClickListener { v ->
            startActivity(
                Intent(
                    requireContext(),
                    Register::class.java
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}