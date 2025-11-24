package com.example.rastreadordegastosenequipo.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rastreadordegastosenequipo.R
import com.example.rastreadordegastosenequipo.dataBase.Miembro

class ListaMiembros(private var datos: List<Miembro>) : RecyclerView.Adapter<ListaMiembros.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val nombre: TextView = v.findViewById(R.id.tvNombreMiembro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.tarjeta_miembro, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nombre.text = datos[position].nombre
    }

    override fun getItemCount() = datos.size

    fun actualizar(nuevaLista: List<Miembro>) {
        datos = nuevaLista
        notifyDataSetChanged()
    }
}