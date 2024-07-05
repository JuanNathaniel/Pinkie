package com.example.pinkiewallet.view.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pinkiewallet.model.Item
import com.example.pinkiewallet.databinding.ItemHorizontalBinding

class HorizontalAdapter(private val itemList: List<Item>) :
    RecyclerView.Adapter<HorizontalAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(private val binding: ItemHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            Glide.with(binding.itemImageViewHorizontal.context)
                .load(item.imageUrl)
                .into(binding.itemImageViewHorizontal)
        }
    }
}