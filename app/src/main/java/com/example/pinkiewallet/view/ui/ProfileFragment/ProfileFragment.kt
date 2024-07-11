package com.example.pinkiewallet.view.ui.ProfileFragment

import android.os.Bundle
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var nameListener: ValueEventListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")

        initFirebase()

        val listItems = listOf(
            ListItem(R.drawable.ic_help, "Help Center"),
            ListItem(R.drawable.ic_conditions, "Terms & Conditions"),
            ListItem(R.drawable.ic_policy, "Privacy Policy")
        )

        val adapter = VerticalAdapter(listItems) { item ->
            Toast.makeText(requireContext(), "Item ${item.text} clicked", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerViewProfile.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewProfile.adapter = adapter

        binding.logoutButton.setOnClickListener {
            profileViewModel.logout(requireContext())
        }

        return root
    }

    private fun initFirebase() {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            nameListener = createNameListener()
            usersRef.child(userId).addValueEventListener(nameListener)
        } else {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNameListener(): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                _binding?.let {
                    if (dataSnapshot.exists()) {
                        val nama = dataSnapshot.child("nama_lengkap").getValue(String::class.java)
                        val noHp = dataSnapshot.child("phone_number").getValue(String::class.java)
                        it.textName.text = nama ?: "Nama Tidak Ditemukan"
                        it.textPhoneNumber.text = noHp ?: "Nomor HP Tidak Ditemukan"
                    } else {
                        Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseDB", "Failed to retrieve data", databaseError.toException())
                Toast.makeText(requireContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
