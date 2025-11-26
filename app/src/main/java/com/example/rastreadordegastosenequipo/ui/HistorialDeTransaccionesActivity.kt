package com.example.rastreadordegastosenequipo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rastreadordegastosenequipo.R

class HistoricalDeTransaccionesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    // private lateinit var gastosAdapter: GastosAdapter - Lo crearemos después

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_detransacciones)

        // Obtener el ID del grupo desde la actividad anterior
        val grupoId = intent.getLongExtra("GRUPO_ID", -1)

        setupRecyclerView()
        // cargarGastos() - Lo implementaremos después
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.rvGastos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // TEMPORAL: Por ahora lista vacía - luego crearemos el adaptador
        // gastosAdapter = GastosAdapter(emptyList())
        // recyclerView.adapter = gastosAdapter
    }
}