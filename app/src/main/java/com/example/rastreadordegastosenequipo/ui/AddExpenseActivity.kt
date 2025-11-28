package com.example.rastreadordegastosenequipo.ui

import android.content.ContentValues
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.rastreadordegastosenequipo.R
import com.example.rastreadordegastosenequipo.dataBase.BD

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var etMonto: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var spPagador: Spinner
    private lateinit var containerMiembros: LinearLayout
    private lateinit var btnGuardar: Button

    private var idGrupo: Int = 0
    private lateinit var miembrosNombres: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        etMonto = findViewById(R.id.etMonto)
        etDescripcion = findViewById(R.id.etDescripcion)
        spPagador = findViewById(R.id.spPagador)
        containerMiembros = findViewById(R.id.containerMiembros)
        btnGuardar = findViewById(R.id.btnGuardar)

        // 1. Recibir datos correctamente
        miembrosNombres = intent.getStringArrayListExtra("miembros") ?: listOf()

        // RECIBIMOS EL ID DEL GRUPO (Puede venir como "idGrupo" o "GRUPO_ID" dependiendo de tu versión anterior)
        // Probamos ambas llaves por seguridad
        idGrupo = intent.getIntExtra("idGrupo", -1)
        if (idGrupo == -1) {
            idGrupo = intent.getIntExtra("GRUPO_ID", -1)
        }

        // Configurar Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, miembrosNombres)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPagador.adapter = adapter

        // Checkboxes dinámicos (Marcados por defecto)
        for (m in miembrosNombres) {
            val check = CheckBox(this)
            check.text = m
            check.isChecked = true
            containerMiembros.addView(check)
        }

        btnGuardar.setOnClickListener {
            guardarGastoEnBD()
        }
    }

    private fun guardarGastoEnBD() {
        val montoTotal = etMonto.text.toString().toDoubleOrNull()
        val descripcion = etDescripcion.text.toString()
        val nombrePagador = spPagador.selectedItem.toString()

        // Validaciones básicas
        if (montoTotal == null || montoTotal <= 0) {
            Toast.makeText(this, "Ingresa un monto válido", Toast.LENGTH_SHORT).show()
            return
        }
        if (descripcion.isEmpty()) {
            Toast.makeText(this, "Ingresa una descripción", Toast.LENGTH_SHORT).show()
            return
        }
        if (idGrupo == -1) {
            Toast.makeText(this, "Error: No se identificó el grupo", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener quiénes comparten el gasto (Checkboxes seleccionados)
        val deudoresNombres = mutableListOf<String>()
        for (i in 0 until containerMiembros.childCount) {
            val v = containerMiembros.getChildAt(i)
            if (v is CheckBox && v.isChecked) {
                deudoresNombres.add(v.text.toString())
            }
        }

        if (deudoresNombres.isEmpty()) {
            Toast.makeText(this, "Selecciona al menos un miembro para dividir", Toast.LENGTH_SHORT).show()
            return
        }

        val db = BD(this).writableDatabase

        // --- CORRECCIÓN 1: Obtener ID real del Pagador ---
        val idPagador = obtenerIdMiembro(db, nombrePagador)
        if (idPagador == -1) {
            Toast.makeText(this, "Error al encontrar al pagador en BD", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Insertar Gasto Principal (La cabecera)
        val valuesGasto = ContentValues().apply {
            put("descripcion", descripcion)
            put("monto", montoTotal)
            put("fecha", System.currentTimeMillis())
            put("id_pagador", idPagador)
            put("id_grupo", idGrupo)
        }
        val idGasto = db.insert("gastos", null, valuesGasto)

        // --- CORRECCIÓN 2: Insertar el Detalle (La división de la deuda) ---
        // Esto es lo que faltaba y por eso salía "0" en saldos
        val montoPorPersona = montoTotal / deudoresNombres.size

        for (nombreDeudor in deudoresNombres) {
            val idDeudor = obtenerIdMiembro(db, nombreDeudor)

            val valuesDetalle = ContentValues().apply {
                put("id_gasto", idGasto.toInt()) // Vinculamos con el gasto creado arriba
                put("id_deudor", idDeudor)
                put("monto_deuda", montoPorPersona)
            }
            db.insert("gasto_detalle", null, valuesDetalle)
        }

        db.close()
        Toast.makeText(this, "Gasto guardado y dividido correctamente", Toast.LENGTH_SHORT).show()
        finish()
    }

    // Función auxiliar para buscar el ID real en la BD usando el nombre y el grupo
    private fun obtenerIdMiembro(db: android.database.sqlite.SQLiteDatabase, nombre: String): Int {
        var id = -1
        // Buscamos el miembro que tenga ese nombre Y pertenezca a este grupo
        val cursor = db.rawQuery("SELECT id FROM miembros WHERE nombre = ? AND id_grupo = ?", arrayOf(nombre, idGrupo.toString()))
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0)
        }
        cursor.close()
        return id
    }
}