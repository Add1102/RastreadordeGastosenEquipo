package com.example.rastreadordegastosenequipo.ui

import android.content.ContentValues
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rastreadordegastosenequipo.R
import com.example.rastreadordegastosenequipo.dataBase.BD
import com.example.rastreadordegastosenequipo.dataBase.Miembro
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.Intent
import com.example.rastreadordegastosenequipo.ui.SettlementActivity // ¡Importación correcta!


class DetalleGrupoActivity : AppCompatActivity() {
    private var idGrupo = 0
    private lateinit var listaVisual: ListaMiembros
    private val datos = mutableListOf<Miembro>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_grupo)

        idGrupo = intent.getIntExtra("ID", 0)
        val nombreGrupo = intent.getStringExtra("NOMBRE")
        findViewById<TextView>(R.id.tvTituloDetalle).text = nombreGrupo

        val rv = findViewById<RecyclerView>(R.id.rvMiembros)
        rv.layoutManager = LinearLayoutManager(this)
        listaVisual = ListaMiembros(datos)
        rv.adapter = listaVisual

        cargar()

        findViewById<FloatingActionButton>(R.id.btnAgregarMiembro).setOnClickListener {
            val input = EditText(this)
            AlertDialog.Builder(this)
                .setTitle("Nuevo Miembro")
                .setView(input)
                .setPositiveButton("Agregar") { _, _ ->
                    if (input.text.isNotEmpty()) guardar(input.text.toString())
                }
                .show()
        }



        findViewById<LinearLayout>(R.id.btnModuloGastos).setOnClickListener {
            val listaMiembros = ArrayList(datos.map { it.nombre })
            val intent = Intent(this, AddExpenseActivity::class.java)
            intent.putStringArrayListExtra("miembros", listaMiembros)
            intent.putExtra("GRUPO_ID", idGrupo)
            intent.putExtra("idGrupo", idGrupo)
            startActivity(intent)
        }

        
        findViewById<LinearLayout>(R.id.btnModuloHistorial).setOnClickListener {
            if (datos.isEmpty()) {
                Toast.makeText(this, "Primero agrega miembros al grupo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, HistoricalDeTransaccionesActivity::class.java)
            intent.putExtra("GRUPO_ID", idGrupo.toLong())
            intent.putExtra("NOMBRE_GRUPO", findViewById<TextView>(R.id.tvTituloDetalle).text.toString())
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btnModuloSaldos).setOnClickListener {
            val intent = Intent(this, SaldosActivity::class.java)
            intent.putExtra("ID_GRUPO", idGrupo)
            startActivity(intent)
        }

        // MÓDULO 5: Liquidación (Pagar Deudas)
        findViewById<LinearLayout>(R.id.btnModuloLiquidar).setOnClickListener {
            // Verificación
            if (datos.isEmpty()) {
                Toast.makeText(this, "No hay miembros en el grupo para liquidar.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val intent = Intent(this, SettlementActivity::class.java)


            intent.putExtra("ID", idGrupo)

            startActivity(intent)
        }
    }

    private fun cargar() {
        datos.clear()
        val db = BD(this).readableDatabase
        val c = db.rawQuery("SELECT * FROM miembros WHERE id_grupo = $idGrupo", null)
        if (c.moveToFirst()) {
            do {
                datos.add(Miembro(c.getInt(0), c.getString(1), idGrupo))
            } while (c.moveToNext())
        }
        c.close()
        db.close()
        listaVisual.actualizar(datos)
    }

    private fun guardar(nombre: String) {
        val db = BD(this).writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("id_grupo", idGrupo)
        }
        db.insert("miembros", null, values)
        db.close()
        cargar()
    }
}