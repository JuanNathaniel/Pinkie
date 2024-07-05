package com.example.pinkiewallet.view.ui.ProfileFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pinkiewallet.R
import com.example.pinkiewallet.databinding.FragmentProfileBinding
import com.example.pinkiewallet.model.ListItem
import com.example.pinkiewallet.view.adapter.VerticalAdapter
import com.example.pinkiewallet.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by activityViewModels()

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

        binding.logoutButton.setOnClickListener {
            profileViewModel.logout(requireContext())
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
