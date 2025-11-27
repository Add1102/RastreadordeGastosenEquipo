package com.example.rastreadordegastosenequipo.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rastreadordegastosenequipo.dataBase.Gasto
import com.example.rastreadordegastosenequipo.R
import java.text.NumberFormat
import java.util.*

class GastosAdaptador(
    private var gastos: List<Gasto>,
    private val onItemClick: (Gasto) -> Unit
) : RecyclerView.Adapter<GastosAdaptador.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val tvMonto: TextView = itemView.findViewById(R.id.tvMonto)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gasto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gasto = gastos[position]
        val format = NumberFormat.getCurrencyInstance(Locale.US)

        holder.tvDescripcion.text = gasto.descripcion
        holder.tvMonto.text = format.format(gasto.monto)
        holder.tvFecha.text = convertirTimestampAFecha(gasto.fecha, holder.itemView.context)

        holder.itemView.setOnClickListener {
            onItemClick(gasto)
        }
    }

    override fun getItemCount(): Int = gastos.size

    fun actualizarLista(nuevaLista: List<Gasto>) {
        gastos = nuevaLista
        notifyDataSetChanged()
    }

    // Función helper para convertir timestamp a fecha legible
    private fun convertirTimestampAFecha(timestamp: Long, context: android.content.Context): String {
        val dateFormat = android.text.format.DateFormat.getDateFormat(context)
        val date = Date(timestamp)
        return dateFormat.format(date)
    }
}