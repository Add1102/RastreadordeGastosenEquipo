package com.example.rastreadordegastosenequipo.dataBase

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class GastosManager(private val db: SQLiteDatabase) {

    // Obtener todos los gastos de un grupo
    fun obtenerGastosDelGrupo(grupoId: Int): List<Gasto> {
        val gastos = mutableListOf<Gasto>()

        val cursor: Cursor = db.query(
            "gastos",  // nombre de la tabla
            null,      // todas las columnas
            "id_grupo = ?",  // condición WHERE
            arrayOf(grupoId.toString()),  // valores para el WHERE
            null, null, "fecha DESC"  // ordenar por fecha descendente
        )

        while (cursor.moveToNext()) {
            val gasto = Gasto(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                monto = cursor.getDouble(cursor.getColumnIndexOrThrow("monto")),
                fecha = cursor.getLong(cursor.getColumnIndexOrThrow("fecha")),
                idPagador = cursor.getInt(cursor.getColumnIndexOrThrow("id_pagador")),
                idGrupo = cursor.getInt(cursor.getColumnIndexOrThrow("id_grupo"))
            )
            gastos.add(gasto)
        }
        cursor.close()
        return gastos
    }

    // Guardar un nuevo gasto
    fun guardarGasto(gasto: Gasto): Long {
        val values = ContentValues().apply {
            put("descripcion", gasto.descripcion)
            put("monto", gasto.monto)
            put("fecha", gasto.fecha)
            put("id_pagador", gasto.idPagador)
            put("id_grupo", gasto.idGrupo)
        }
        return db.insert("gastos", null, values)
    }

    // Actualizar un gasto existente
    fun actualizarGasto(gasto: Gasto): Int {
        val values = ContentValues().apply {
            put("descripcion", gasto.descripcion)
            put("monto", gasto.monto)
            put("fecha", gasto.fecha)
            put("id_pagador", gasto.idPagador)
            put("id_grupo", gasto.idGrupo)
        }
        return db.update("gastos", values, "id = ?", arrayOf(gasto.id.toString()))
    }

    fun eliminarGasto(gastoId: Int): Int {
        return db.delete("gastos", "id = ?", arrayOf(gastoId.toString()))
    }

}