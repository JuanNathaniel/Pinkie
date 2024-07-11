package com.example.pinkiewallet.view.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pinkiewallet.view.adapter.HorizontalAdapter
import com.example.pinkiewallet.model.Item
import com.example.pinkiewallet.backend.CreateQR
import com.example.pinkiewallet.backend.TransferActivity
import com.example.pinkiewallet.databinding.FragmentHomeBinding
import com.example.pinkiewallet.view.activity.HistoryActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var balanceListener: ValueEventListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")

        initUI()
        initFirebase()

        return root
    }

    private fun initUI() {
        // Initialize RecyclerView for Favorite Transaction
        val horizontalRecyclerViewFav = binding.horizontalRecyclerViewFavoriteTransaction
        val itemListFav: MutableList<Item> = ArrayList()

        // Example items for Favorite Transaction RecyclerView
//        itemListFav.add(Item("https://www.example.com/image1.jpg"))
//        itemListFav.add(Item("https://www.example.com/image2.jpg"))
        // Add more items as needed

        val favTransactionAdapter = HorizontalAdapter(itemListFav)
        horizontalRecyclerViewFav.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        horizontalRecyclerViewFav.adapter = favTransactionAdapter

        // Initialize RecyclerView for Insight
        val horizontalRecyclerView = binding.horizontalRecyclerView
        val itemList: MutableList<Item> = ArrayList()

        // Example items for Insight RecyclerView
        itemList.add(Item("https://www.ukulele.co.nz/wp-content/uploads/2020/11/Iklan-mcdonalds.jpg"))
        itemList.add(Item("https://kledo.com/blog/wp-content/uploads/2022/01/iklan-produk.jpg"))
        itemList.add(Item("https://cdn-image.hipwee.com/wp-content/uploads/2020/06/hipwee-floridina-01.jpg"))
        itemList.add(Item("https://lh5.googleusercontent.com/YOVjx5EeT8vtVEge-HV6TSWRe2wyxPsaWvtiWl6u9jrAIoEnEwfLHZX9NVNZlUYdpG3sqTwWgdljrkGyw5jTv3qAXhgVSdws2I6SChKFVWP2i7ABXiz4s60lTYXsFHWKOQUhrrdjTqP4g0RY-T_gDiU"))

        if (itemList.isEmpty()) {
            // Daftar item kosong, lakukan sesuatu di sini jika diperlukan
            // Toast.makeText(this, "Daftar item kosong", Toast.LENGTH_SHORT).show()
            binding.emptyListInsight.visibility = View.VISIBLE
        } else {
            val adapter = HorizontalAdapter(itemList)
            horizontalRecyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            horizontalRecyclerView.adapter = adapter
        }
        val insightAdapter = HorizontalAdapter(itemList)
        horizontalRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        horizontalRecyclerView.adapter = insightAdapter

        // Handle eye button to show/hide saldo
        binding.eyebuttonopen.setOnClickListener {
            binding.eyebuttonopen.visibility = View.INVISIBLE
            binding.eyebuttonclose.visibility = View.VISIBLE
            binding.listcirclecash.visibility = View.VISIBLE
            binding.cash.visibility = View.INVISIBLE
            binding.closepoints.visibility = View.VISIBLE
            binding.openpoints.visibility = View.INVISIBLE
        }

        binding.eyebuttonclose.setOnClickListener {
            binding.eyebuttonclose.visibility = View.INVISIBLE
            binding.eyebuttonopen.visibility = View.VISIBLE
            binding.cash.visibility = View.VISIBLE
            binding.listcirclecash.visibility = View.INVISIBLE
            binding.openpoints.visibility = View.VISIBLE
            binding.closepoints.visibility = View.INVISIBLE
        }

        // Handle top up button click
        binding.topupbt.setOnClickListener {
            val intent = Intent(requireContext(), CreateQR::class.java)
            startActivity(intent)
        }

        // Handle transfer button click
        binding.transferbt.setOnClickListener {
            val intent = Intent(requireContext(), TransferActivity::class.java)
            startActivity(intent)
        }

        // Handle withdraw button click
        binding.withdrawbt.setOnClickListener {
            Toast.makeText(requireContext(), "Upcoming Feature", Toast.LENGTH_SHORT).show()
        }

        // Handle history button click
        binding.historybt.setOnClickListener {
            val intent = Intent(requireContext(), HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initFirebase() {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            // Create and add ValueEventListener for balance
            balanceListener = createBalanceListener()
            usersRef.child(userId).addValueEventListener(balanceListener)
        } else {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createBalanceListener(): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val balance = dataSnapshot.child("balance").getValue(Long::class.java)
                    val points = dataSnapshot.child("point").getValue(Long::class.java)
                    binding.cash.text = balance?.toString() ?: "0"
                    binding.openpoints.text = points?.toString() ?: "0"
                } else {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseDB", "Failed to retrieve balance", databaseError.toException())
                Toast.makeText(requireContext(), "Failed to retrieve balance", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
