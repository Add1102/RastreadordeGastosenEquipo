package com.example.rastreadordegastosenequipo.dataBase

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class GastosManager(private val db: SQLiteDatabase) {



    fun actualizarGasto(gasto: Gasto): Int {
        val values = ContentValues().apply {
            put("descripcion", gasto.descripcion)
            put("monto", gasto.monto)

        }


        return db.update("gastos", values, "id = ?", arrayOf(gasto.id.toString()))
    }


    fun eliminarGasto(gastoId: Int): Int {
        db.beginTransaction()
        var rowsDeleted = 0
        try {

            db.delete("gasto_detalle", "id_gasto = ?", arrayOf(gastoId.toString()))


            rowsDeleted = db.delete("gastos", "id = ?", arrayOf(gastoId.toString()))

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e("GastosManager", "Error al eliminar gasto $gastoId: ${e.message}")
        } finally {
            db.endTransaction()
        }
        return rowsDeleted
    }


    fun obtenerGastosDelGrupo(groupId: Int): List<Gasto> {
        val gastosList = mutableListOf<Gasto>()
        val cursor = db.rawQuery("SELECT id, descripcion, monto, fecha, id_pagador, id_grupo FROM gastos WHERE id_grupo = ? ORDER BY fecha DESC", arrayOf(groupId.toString()))

        while (cursor.moveToNext()) {
            val gasto = Gasto(
                id = cursor.getInt(0),
                descripcion = cursor.getString(1),
                monto = cursor.getDouble(2),
                fecha = cursor.getLong(3),
                idPagador = cursor.getInt(4),
                idGrupo = cursor.getInt(5)
            )
            gastosList.add(gasto)
        }
        cursor.close()
        return gastosList
    }



    fun getNetBalances(groupId: Int): Map<String, Double> {
        val balances = mutableMapOf<String, Double>()
        val cursorMiembros = db.rawQuery(
            "SELECT id, nombre FROM miembros WHERE id_grupo = ?",
            arrayOf(groupId.toString())
        )

        while (cursorMiembros.moveToNext()) {
            val idMiembro = cursorMiembros.getInt(0)
            val nombre = cursorMiembros.getString(1)

            // Calcular Total Pagado : Suma de gastos donde él fue el pagador.
            val queryPagado = "SELECT COALESCE(SUM(monto), 0.0) FROM gastos WHERE id_pagador = ? AND id_grupo = ?"
            val totalPagado = obtenerSuma(db, queryPagado, arrayOf(idMiembro.toString(), groupId.toString()))

            //  Calcular Total Consumido : Suma de deudas individuales en gasto_detalle.
            val queryConsumido = "SELECT COALESCE(SUM(gd.monto_deuda), 0.0) FROM gasto_detalle gd JOIN gastos g ON gd.id_gasto = g.id WHERE gd.id_deudor = ? AND g.id_grupo = ?"
            val totalConsumido = obtenerSuma(db, queryConsumido, arrayOf(idMiembro.toString(), groupId.toString()))

            val saldoNeto = totalPagado - totalConsumido
            balances[nombre] = saldoNeto
        }

        cursorMiembros.close()
        return balances
    }


    fun resetGroupBalances(id: Int): Boolean {
        db.beginTransaction()
        return try {

            db.execSQL("DELETE FROM gasto_detalle WHERE id_gasto IN (SELECT id FROM gastos WHERE id_grupo=?)", arrayOf(id.toString()))


            db.delete("gastos", "id_grupo=?", arrayOf(id.toString()))


            db.delete("pagos", "id_grupo=?", arrayOf(id.toString()))

            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            Log.e("GastosManager", "Error al resetear saldos del grupo $id: ${e.message}")
            false
        } finally {
            db.endTransaction()
        }
    }


    private fun obtenerSuma(db: SQLiteDatabase, query: String, args: Array<String>): Double {
        var total = 0.0
        val cursor = db.rawQuery(query, args)
        if (cursor.moveToFirst()) total = cursor.getDouble(0)
        cursor.close()
        return total
    }
}