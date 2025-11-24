package com.example.rastreadordegastosenequipo.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rastreadordegastosenequipo.R
import com.example.rastreadordegastosenequipo.dataBase.Grupo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ListaGrupos(
    private var datos: List<Grupo>,
    private val clic: (Grupo) -> Unit,
    private val alMantenerPresionado: (Grupo) -> Unit
) : RecyclerView.Adapter<ListaGrupos.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val nombre: TextView = v.findViewById(R.id.tvNombreGrupo)
        val fecha: TextView = v.findViewById(R.id.tvFechaGrupo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.tarjeta_grupo, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datos[position]
        holder.nombre.text = item.nombre
        holder.fecha.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(item.fechaCreacion))

        holder.itemView.setOnClickListener { clic(item) }

        holder.itemView.setOnLongClickListener {
            alMantenerPresionado(item)
            true
        }
    }

    override fun getItemCount() = datos.size

    fun actualizar(nuevaLista: List<Grupo>) {
        datos = nuevaLista
        notifyDataSetChanged()
    }
}