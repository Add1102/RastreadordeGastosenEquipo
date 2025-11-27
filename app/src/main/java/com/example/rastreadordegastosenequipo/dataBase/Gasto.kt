package com.example.rastreadordegastosenequipo.dataBase
import java.io.Serializable
data class Gasto(
    val id: Int = 0,
    val descripcion: String,
    val monto: Double,
    val fecha: Long = System.currentTimeMillis(),
    val idPagador: Int,
    val idGrupo: Int
): Serializable