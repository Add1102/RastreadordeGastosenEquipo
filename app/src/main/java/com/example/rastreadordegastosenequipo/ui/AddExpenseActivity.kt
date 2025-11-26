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

        val resultIntent = Intent()
        resultIntent.putExtra("monto", monto)
        resultIntent.putExtra("descripcion", descripcion)
        resultIntent.putExtra("pagador", pagador)
        resultIntent.putStringArrayListExtra("dividirEntre", ArrayList(seleccionados))

        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
