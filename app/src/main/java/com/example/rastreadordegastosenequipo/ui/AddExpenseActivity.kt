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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        etMonto = findViewById(R.id.etMonto)
        etDescripcion = findViewById(R.id.etDescripcion)
        spPagador = findViewById(R.id.spPagador)
        containerMiembros = findViewById(R.id.containerMiembros)
        btnGuardar = findViewById(R.id.btnGuardar)

        // Recibir lista de miembros desde el módulo 1
        miembros = intent.getStringArrayListExtra("miembros") ?: listOf()

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

        //  Guardar en base de datos
        try {
            val dbHelper = BD(this)
            val database = dbHelper.writableDatabase
            val gastosManager = GastosManager(database)

            // TEMPORAL: Usar grupoId = 1 por defecto (luego vendrá del Módulo 1)
            val grupoId = 1

            // TEMPORAL: Convertir nombre a ID (luego vendrá del Módulo 1)
            val idPagado = miembros.indexOf(pagador) + 1

            val nuevoGasto = Gasto(
                descripcion = descripcion,
                monto = monto,
                idPagador = idPagado,
                idGrupo = grupoId,
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
