package com.example.pinkiewallet.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pinkiewallet.databinding.FragmentHomeBinding
import com.example.pinkiewallet.ui.HorizontalAdapter
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {
//    private lateinit var binding : FragmentHomeBinding

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Favorite Transaction
        val horizontalRecyclerViewFav = binding.horizontalRecyclerViewFavoriteTransaction
        val itemListFav: MutableList<String> = ArrayList()
        // Tambahkan item ke dalam daftar
//        itemListFav.add("Item 1")
//        itemListFav.add("Item 2")
//        itemListFav.add("Item 3")
//        itemListFav.add("Item 4")
        //cek favorite transaction kosong atau tidak
        if (itemListFav.isEmpty()) {
            // Daftar item kosong, lakukan sesuatu di sini jika diperlukan
//            Toast.makeText(requireContext(), "Daftar Transaksi Favorit Kosong", Toast.LENGTH_SHORT).show()
            binding.emptyListTextView.visibility = View.VISIBLE
        } else {
            val adapter = HorizontalAdapter(itemListFav)
            horizontalRecyclerViewFav.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            horizontalRecyclerViewFav.adapter = adapter
        }

        //Insight
        val horizontalRecyclerView = binding.horizontalRecyclerView
        val itemList: MutableList<String> = ArrayList()
        // Tambahkan item ke dalam daftar
        itemList.add("Item 1")
        itemList.add("Item 2")
        itemList.add("Item 3")
        itemList.add("Item 4")

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

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}