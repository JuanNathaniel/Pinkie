package com.example.pinkiewallet.ui.ProfileFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pinkiewallet.ListAdapter
import com.example.pinkiewallet.ListItem
import com.example.pinkiewallet.R
import com.example.pinkiewallet.VerticalAdapter
import com.example.pinkiewallet.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val listItems = listOf(
            ListItem(R.drawable.ic_help, "Help Center"),
            ListItem(R.drawable.ic_conditions, "Terms & Conditions"),
            ListItem(R.drawable.ic_policy, "Privacy Policy")
        )

        val adapter = VerticalAdapter(listItems) { item ->
            // Implementasi untuk intent ke halaman lain atau tindakan lain saat item diklik
            Toast.makeText(requireContext(), "Item ${item.text} clicked", Toast.LENGTH_SHORT).show()
            // Contoh implementasi intent:
            // startActivity(Intent(requireContext(), DetailActivity::class.java))
        }

        binding.recyclerViewProfile.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewProfile.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}