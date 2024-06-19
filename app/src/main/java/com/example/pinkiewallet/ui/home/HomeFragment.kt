package com.example.pinkiewallet.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pinkiewallet.HorizontalAdapter
import com.example.pinkiewallet.Item
import com.example.pinkiewallet.databinding.FragmentHomeBinding

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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Favorite Transaction
        val horizontalRecyclerViewFav = binding.horizontalRecyclerViewFavoriteTransaction
        val itemListFav: MutableList<Item> = ArrayList()
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
        val itemList: MutableList<Item> = ArrayList()
        // Tambahkan item ke dalam daftar
        itemList.add(Item("Item 1", "https://www.ukulele.co.nz/wp-content/uploads/2020/11/Iklan-mcdonalds.jpg"))
        itemList.add(Item("Item 2", "https://kledo.com/blog/wp-content/uploads/2022/01/iklan-produk.jpg"))
        itemList.add(Item("Item 3", "https://cdn-image.hipwee.com/wp-content/uploads/2020/06/hipwee-floridina-01.jpg"))
        itemList.add(Item("Item 4", "https://lh5.googleusercontent.com/YOVjx5EeT8vtVEge-HV6TSWRe2wyxPsaWvtiWl6u9jrAIoEnEwfLHZX9NVNZlUYdpG3sqTwWgdljrkGyw5jTv3qAXhgVSdws2I6SChKFVWP2i7ABXiz4s60lTYXsFHWKOQUhrrdjTqP4g0RY-T_gDiU"))

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

        //Ini view/unview saldo
        binding.eyebuttonopen.setOnClickListener{
            //Tidak tampilkan saldo
            binding.eyebuttonopen.visibility = View.INVISIBLE
            binding.eyebuttonclose.visibility = View.VISIBLE
            binding.listcirclecash.visibility = View.VISIBLE
            binding.cash.visibility = View.INVISIBLE
            binding.closepoints.visibility = View.VISIBLE
            binding.openpoints.visibility = View.INVISIBLE
        }
        binding.eyebuttonclose.setOnClickListener{
            //tampilkan saldo
            binding.eyebuttonclose.visibility = View.INVISIBLE
            binding.eyebuttonopen.visibility = View.VISIBLE
            binding.cash.visibility = View.VISIBLE
            binding.listcirclecash.visibility = View.INVISIBLE
            binding.openpoints.visibility = View.VISIBLE
            binding.closepoints.visibility = View.INVISIBLE
        }

        //Ini tuh menu menu yang diatas
        binding.topupbt.setOnClickListener{
            Toast.makeText(requireContext(), "This is Top Up men", Toast.LENGTH_SHORT).show()
        }
        binding.transferbt.setOnClickListener{
            Toast.makeText(requireContext(), "This is Transfer ges", Toast.LENGTH_SHORT).show()
        }
        binding.withdrawbt.setOnClickListener{
            Toast.makeText(requireContext(), "Upcoming Feature", Toast.LENGTH_SHORT).show()
        }
        binding.historybt.setOnClickListener{
            Toast.makeText(requireContext(), "This is History tante", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}