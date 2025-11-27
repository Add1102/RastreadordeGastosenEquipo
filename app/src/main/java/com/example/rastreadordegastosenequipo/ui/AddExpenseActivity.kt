package com.example.rastreadordegastosenequipo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.CheckBox
import android.widget.Toast
import android.content.Intent
import com.example.rastreadordegastosenequipo.R
import com.example.rastreadordegastosenequipo.dataBase.BD
import com.example.rastreadordegastosenequipo.dataBase.Gasto
import com.example.rastreadordegastosenequipo.dataBase.GastosManager

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var etMonto: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var spPagador: Spinner
    private lateinit var containerMiembros: LinearLayout
    private lateinit var btnGuardar: Button

    private lateinit var miembros: List<String>
    private var grupoId: Int = -1  // nueva variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        etMonto = findViewById(R.id.etMonto)
        etDescripcion = findViewById(R.id.etDescripcion)
        spPagador = findViewById(R.id.spPagador)
        containerMiembros = findViewById(R.id.containerMiembros)
        btnGuardar = findViewById(R.id.btnGuardar)

        // Recibir lista de miembros Y el grupoId
        miembros = intent.getStringArrayListExtra("miembros") ?: listOf()
        grupoId = intent.getIntExtra("GRUPO_ID", -1)  // ← Recibir el grupoId

        // Llenar spinner con pagadores
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, miembros)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPagador.adapter = adapter

        // Crear checkboxes dinámicamente
        for (m in miembros) {
            val check = CheckBox(this)
            check.text = m
            containerMiembros.addView(check)
        }

        btnGuardar.setOnClickListener {
            guardarGasto()
        }
    }

    private fun guardarGasto() {
        val monto = etMonto.text.toString().toDoubleOrNull()
        val descripcion = etDescripcion.text.toString()
        val pagador = spPagador.selectedItem.toString()

        val seleccionados = mutableListOf<String>()
        for (i in 0 until containerMiembros.childCount) {
            val cb = containerMiembros.getChildAt(i) as CheckBox
            if (cb.isChecked) seleccionados.add(cb.text.toString())
        }

        if (monto == null || descripcion.isEmpty() || seleccionados.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Guardar en base de datos
        try {
            val dbHelper = BD(this)
            val database = dbHelper.writableDatabase
            val gastosManager = GastosManager(database)

            // Convertir nombre a ID (usar índice + 1 como ID temporal)
            val idPagador = miembros.indexOf(pagador) + 1

            val nuevoGasto = Gasto(
                descripcion = descripcion,
                monto = monto,
                idPagador = idPagador,
                idGrupo = if (grupoId == -1) 1 else grupoId,  // ← Usar grupoId real o 1 como fallback
                fecha = System.currentTimeMillis()
            )

            // Guardar en base de datos
            gastosManager.guardarGasto(nuevoGasto)

            Toast.makeText(this, "Gasto guardado correctamente", Toast.LENGTH_SHORT).show()
            finish()

        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
