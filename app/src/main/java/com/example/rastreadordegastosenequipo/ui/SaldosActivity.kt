package com.example.rastreadordegastosenequipo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rastreadordegastosenequipo.R
import com.example.rastreadordegastosenequipo.dataBase.BD
import com.example.rastreadordegastosenequipo.dataBase.Miembro

class SaldosActivity : AppCompatActivity() {

    private var idGrupo = 0
    private lateinit var rvSaldos: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saldos)

        idGrupo = intent.getIntExtra("ID_GRUPO", 0)

        rvSaldos = findViewById(R.id.rvListaSaldos)
        rvSaldos.layoutManager = LinearLayoutManager(this)

        calcularYMostrarSaldos()
    }

    private fun calcularYMostrarSaldos() {
        val listaSaldos = mutableListOf<SaldoMiembro>()
        val db = BD(this).readableDatabase
        val cursorMiembros = db.rawQuery("SELECT * FROM miembros WHERE id_grupo = ?", arrayOf(idGrupo.toString()))

        while (cursorMiembros.moveToNext()) {
            val idMiembro = cursorMiembros.getInt(0)
            val nombre = cursorMiembros.getString(1)
            val miembro = Miembro(idMiembro, nombre, idGrupo)

            val totalPagado = obtenerSuma(db, "SELECT SUM(monto) FROM gastos WHERE id_pagador = ? AND id_grupo = ?", arrayOf(idMiembro.toString(), idGrupo.toString()))
            val totalConsumido = obtenerSuma(db, "SELECT SUM(gd.monto_deuda) FROM gasto_detalle gd JOIN gastos g ON gd.id_gasto = g.id WHERE gd.id_deudor = ? AND g.id_grupo = ?", arrayOf(idMiembro.toString(), idGrupo.toString()))
            val pagosEnviados = obtenerSuma(db, "SELECT SUM(monto) FROM pagos WHERE id_pagador = ? AND id_grupo = ?", arrayOf(idMiembro.toString(), idGrupo.toString()))
            val pagosRecibidos = obtenerSuma(db, "SELECT SUM(monto) FROM pagos WHERE id_beneficiario = ? AND id_grupo = ?", arrayOf(idMiembro.toString(), idGrupo.toString()))

            val saldoFinal = (totalPagado + pagosEnviados) - (totalConsumido + pagosRecibidos)
            listaSaldos.add(SaldoMiembro(miembro, saldoFinal))
        }
        cursorMiembros.close()
        db.close()

        rvSaldos.adapter = ListaSaldos(listaSaldos)
    }

    private fun obtenerSuma(db: android.database.sqlite.SQLiteDatabase, query: String, args: Array<String>): Double {
        var total = 0.0
        val cursor = db.rawQuery(query, args)
        if (cursor.moveToFirst()) total = cursor.getDouble(0)
        cursor.close()
        return total
    }
}