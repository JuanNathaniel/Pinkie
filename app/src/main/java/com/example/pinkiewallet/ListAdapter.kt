package com.example.pinkiewallet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.pinkiewallet.databinding.ListItemBinding

class ListAdapter(private val context: Context, private val listItems: List<ListItem>) : BaseAdapter() {

    override fun getCount(): Int = listItems.size

    override fun getItem(position: Int): Any = listItems[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: ListItemBinding
        val view: View

        if (convertView == null) {
            binding = ListItemBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as ListItemBinding
            view = convertView
        }

        val listItem = listItems[position]
        binding.itemIcon.setImageResource(listItem.icon)
        binding.itemText.text = listItem.text

        return view
    }
}



