package com.example.rastreadordegastosenequipo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rastreadordegastosenequipo.R
import com.example.rastreadordegastosenequipo.dataBase.BD
import com.example.rastreadordegastosenequipo.dataBase.GastosManager
import com.example.rastreadordegastosenequipo.dataBase.Gasto
import android.content.Intent

class HistoricalDeTransaccionesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var gastosAdapter: GastosAdaptador
    private var grupoId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_detransacciones)

        // Obtener el ID del grupo desde la actividad anterior
        grupoId = intent.getLongExtra("GRUPO_ID", -1)

        setupRecyclerView()
        cargarGastos()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.rvGastos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        gastosAdapter = GastosAdaptador(emptyList()) { gasto ->
            // Al hacer clic en un gasto
            abrirDetalleGasto(gasto)
        }
        recyclerView.adapter = gastosAdapter
    }

    private fun cargarGastos() {
        try {
            // Usar el grupoId obtenido del intent
            if (grupoId == -1L) {
                Toast.makeText(this, "Error: No se recibió ID del grupo", Toast.LENGTH_SHORT).show()
                return
            }

            val dbHelper = BD(this)
            val database = dbHelper.readableDatabase
            val gastosManager = GastosManager(database)

            // CONVERTIR Long a Int para el método obtenerGastosDelGrupo
            val gastosReales = gastosManager.obtenerGastosDelGrupo(grupoId.toInt())
            gastosAdapter.actualizarLista(gastosReales)

            if (gastosReales.isEmpty()) {
                Toast.makeText(this, "No hay gastos registrados", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar gastos: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirDetalleGasto(gasto: Gasto) {
        val intent = Intent(this, DetallesGastoActivity::class.java)
        intent.putExtra("GASTO", gasto)
        startActivity(intent)
    }
}