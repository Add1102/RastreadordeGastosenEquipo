package com.example.rastreadordegastosenequipo.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rastreadordegastosenequipo.R
import com.example.rastreadordegastosenequipo.control.SettlementTransaction // Importación corregida!
import java.text.NumberFormat
import java.util.Locale

class SettlementAdapter(private val transactions: List<SettlementTransaction>) :
    RecyclerView.Adapter<SettlementAdapter.SettlementViewHolder>() {

    private val formatter = NumberFormat.getCurrencyInstance(Locale.US)

    inner class SettlementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val instructionText: TextView = itemView.findViewById(R.id.tv_transaction_instruction)

        fun bind(transaction: SettlementTransaction) {
            val amountFormatted = formatter.format(transaction.amount)


            val instruction = "${transaction.from} debe $amountFormatted a ${transaction.to}"
            instructionText.text = instruction
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettlementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_settlement_transaction, parent, false)
        return SettlementViewHolder(view)
    }

    override fun onBindViewHolder(holder: SettlementViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount() = transactions.size
}