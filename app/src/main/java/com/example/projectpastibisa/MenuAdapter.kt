package com.example.projectpastibisa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectpastibisa.databinding.ItemMenuBinding

class MenuAdapter(private val menuItems: List<String>, private val onItemClick: (Int) -> Unit) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menuItem = menuItems[position]
        holder.bind(menuItem)
    }

    override fun getItemCount(): Int {
        return menuItems.size
    }

    inner class ViewHolder(private val binding: ItemMenuBinding, onItemClick: (Int) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(menuItem: String) {
            binding.tvItemName.text = menuItem
        }

        init {
            binding.root.setOnClickListener {
                onItemClick(absoluteAdapterPosition)
            }
        }
    }
}
