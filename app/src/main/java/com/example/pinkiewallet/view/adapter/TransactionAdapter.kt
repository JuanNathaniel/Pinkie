package com.example.pinkiewallet.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pinkiewallet.R
import com.example.pinkiewallet.model.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAmount: TextView = itemView.findViewById(R.id.tvTransactionAmount)
        val tvFrom: TextView = itemView.findViewById(R.id.tvTransactionFrom)
        val tvTo: TextView = itemView.findViewById(R.id.tvTransactionTo)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTransactionTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transactions, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        // Format data dengan padding agar rata
        val amountText = String.format("%-8s %s", "Amount:", transaction.amount?.toString() ?: "N/A")
        val fromText = String.format("%-8s %s", "From:", transaction.from ?: "N/A")
        val toText = String.format("%-11s %s", "To:", transaction.to ?: "N/A")

        // Format timestamp menjadi tanggal yang dapat dibaca
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = transaction.timestamp?.let { Date(it) }
        val timestampText = String.format("%-11s %s", "Date:", date?.let { dateFormat.format(it) } ?: "N/A")

        holder.tvAmount.text = amountText
        holder.tvFrom.text = fromText
        holder.tvTo.text = toText
        holder.tvTimestamp.text = timestampText

    }



    override fun getItemCount(): Int = transactions.size
}
