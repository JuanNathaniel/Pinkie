package com.example.pinkiewallet.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pinkiewallet.model.ListItem
import com.example.pinkiewallet.databinding.ListItemBinding

class VerticalAdapter(private val items: List<ListItem>, private val listener: (ListItem) -> Unit) : RecyclerView.Adapter<VerticalAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                listener(items[adapterPosition])
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.itemIcon.setImageResource(item.icon)
        holder.binding.itemText.text = item.text
    }

    override fun getItemCount() = items.size
}
