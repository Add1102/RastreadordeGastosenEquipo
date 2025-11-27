package com.example.rastreadordegastosenequipo.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rastreadordegastosenequipo.R // Importante importar R
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.absoluteValue

class ListaSaldos(private var datos: List<SaldoMiembro>) : RecyclerView.Adapter<ListaSaldos.ViewHolder>() {

    // 1. Buscamos los IDs de la nueva tarjeta XML
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val inicial: TextView = v.findViewById(R.id.tvInicial)
        val nombre: TextView = v.findViewById(R.id.tvNombreSaldo)
        val monto: TextView = v.findViewById(R.id.tvMontoSaldo)
        val estado: TextView = v.findViewById(R.id.tvEstadoSaldo)
    }

    // 2. Inflamos el layout 'tarjeta_saldo'
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.tarjeta_saldo, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datos[position]

        // Configurar Nombre e Inicial
        holder.nombre.text = item.miembro.nombre
        holder.inicial.text = item.miembro.nombre.take(1).uppercase() // Primera letra

        // Formato de moneda
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        val dinero = format.format(item.saldo.absoluteValue) // Mostramos valor positivo siempre en el número

        // Lógica de Colores y Textos
        if (item.saldo > 0.01) {
            // VERDE: A favor (Le deben dinero)
            holder.monto.text = "+ $dinero"
            holder.monto.setTextColor(Color.parseColor("#4CAF50"))
            holder.estado.text = "Recibe"
            holder.estado.setTextColor(Color.parseColor("#4CAF50"))

        } else if (item.saldo < -0.01) {
            // ROJO: En contra (Debe dinero)
            holder.monto.text = "- $dinero"
            holder.monto.setTextColor(Color.parseColor("#F44336"))
            holder.estado.text = "Debe"
            holder.estado.setTextColor(Color.parseColor("#F44336"))

        } else {
            // GRIS: En paz
            holder.monto.text = "$0.00"
            holder.monto.setTextColor(Color.GRAY)
            holder.estado.text = "Al día"
            holder.estado.setTextColor(Color.GRAY)
        }
    }

    override fun getItemCount() = datos.size
}