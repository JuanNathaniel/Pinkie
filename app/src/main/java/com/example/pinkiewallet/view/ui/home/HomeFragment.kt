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
import com.example.pinkiewallet.R
import com.example.pinkiewallet.view.adapter.HorizontalAdapter
import com.example.pinkiewallet.model.Item
import com.example.pinkiewallet.backend.CreateQR
import com.example.pinkiewallet.backend.TransferActivity
import com.example.pinkiewallet.databinding.FragmentHomeBinding
import com.example.pinkiewallet.view.activity.ContactActivity
import com.example.pinkiewallet.view.activity.HistoryActivity
import com.example.pinkiewallet.view.fragment.Register2
import com.google.android.material.bottomnavigation.BottomNavigationView
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
        val horizontalRecyclerViewFav = binding.horizontalRecyclerViewFavoriteTransaction
        val itemListFav: MutableList<Item> = ArrayList()

        val favTransactionAdapter = HorizontalAdapter(itemListFav)
        horizontalRecyclerViewFav.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        horizontalRecyclerViewFav.adapter = favTransactionAdapter

        val horizontalRecyclerView = binding.horizontalRecyclerView
        val itemList: MutableList<Item> = ArrayList()

        itemList.add(Item("https://jabarekspres.com/wp-content/uploads/2023/02/Review-AdaKami-e1676089545943.jpg"))
        itemList.add(Item("https://kledo.com/blog/wp-content/uploads/2022/01/iklan-produk.jpg"))
        itemList.add(Item("https://cdn-image.hipwee.com/wp-content/uploads/2020/06/hipwee-floridina-01.jpg"))
        itemList.add(Item("https://lh5.googleusercontent.com/YOVjx5EeT8vtVEge-HV6TSWRe2wyxPsaWvtiWl6u9jrAIoEnEwfLHZX9NVNZlUYdpG3sqTwWgdljrkGyw5jTv3qAXhgVSdws2I6SChKFVWP2i7ABXiz4s60lTYXsFHWKOQUhrrdjTqP4g0RY-T_gDiU"))

        if (itemList.isEmpty()) {
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

        binding.topupbt.setOnClickListener {
            val intent = Intent(requireContext(), CreateQR::class.java)
            startActivity(intent)
        }

        binding.transferbt.setOnClickListener {
            val intent = Intent(requireContext(), TransferActivity::class.java)
            startActivity(intent)

//            val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
//            bottomNavigationView.visibility = View.GONE
//            val transferFragment = TransferActivity()
//
//            requireActivity().supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, transferFragment)
//                .addToBackStack(null)
//                .commit()
        }

        binding.withdrawbt.setOnClickListener {
            val intent = Intent(requireContext(), ContactActivity::class.java)
            startActivity(intent)
        }

        binding.historybt.setOnClickListener {
            val intent = Intent(requireContext(), HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initFirebase() {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
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
                    _binding?.let { binding ->
                        binding.cash.text = balance?.toString() ?: "0"
                        binding.openpoints.text = points?.toString() ?: "0"
                    }
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
