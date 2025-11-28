package com.example.rastreadordegastosenequipo.dataBase

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class GastosManager(private val db: SQLiteDatabase) {

    // Obtener todos los gastos de un grupo
    fun obtenerGastosDelGrupo(grupoId: Int): List<Gasto> {
        val gastos = mutableListOf<Gasto>()
        val cursor: Cursor = db.query(
            "gastos",
            null,
            "id_grupo = ?",
            arrayOf(grupoId.toString()),
            null, null, "fecha DESC"
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

    // --- AQUÍ ESTÁ LA CORRECCIÓN MÁGICA ---
    fun actualizarGasto(gasto: Gasto): Int {
        // 1. Actualizar la cabecera (Tabla gastos)
        val values = ContentValues().apply {
            put("descripcion", gasto.descripcion)
            put("monto", gasto.monto) // El nuevo monto (ej. 300)
            put("fecha", gasto.fecha)
            put("id_pagador", gasto.idPagador)
            put("id_grupo", gasto.idGrupo)
        }
        val filasAfectadas = db.update("gastos", values, "id = ?", arrayOf(gasto.id.toString()))

        // 2. RECALCULAR LAS DEUDAS (Tabla gasto_detalle)
        if (filasAfectadas > 0) {
            // A. Contar cuántas personas dividen este gasto actualmente
            val cursor = db.rawQuery("SELECT COUNT(*) FROM gasto_detalle WHERE id_gasto = ?", arrayOf(gasto.id.toString()))
            var numDeudores = 0
            if (cursor.moveToFirst()) {
                numDeudores = cursor.getInt(0)
            }
            cursor.close()

            // B. Si hay deudores, actualizamos el monto de cada uno
            if (numDeudores > 0) {
                val nuevoMontoIndividual = gasto.monto / numDeudores // Ej: 300 / 2 = 150

                val valuesDetalle = ContentValues().apply {
                    put("monto_deuda", nuevoMontoIndividual)
                }
                // Actualizamos TODAS las filas de detalle de este gasto con el nuevo monto
                db.update("gasto_detalle", valuesDetalle, "id_gasto = ?", arrayOf(gasto.id.toString()))
            }
        }
        return filasAfectadas
    }

    // --- TAMBIÉN CORREGIMOS ELIMINAR PARA EVITAR ERRORES FUTUROS ---
    fun eliminarGasto(gastoId: Int): Int {
        // 1. Primero borramos los detalles (hijos)
        db.delete("gasto_detalle", "id_gasto = ?", arrayOf(gastoId.toString()))

        // 2. Luego borramos el gasto (padre)
        return db.delete("gastos", "id = ?", arrayOf(gastoId.toString()))
    }
}