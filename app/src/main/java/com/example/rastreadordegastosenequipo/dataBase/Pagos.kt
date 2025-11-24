package com.example.rastreadordegastosenequipo.dataBase

data class Pagos(
    val id: Int = 0,
    val idGrupo: Int,
    val idPagador: Int,
    val idBeneficiario: Int,
    val monto: Double,
    val fecha: Long = System.currentTimeMillis()
)