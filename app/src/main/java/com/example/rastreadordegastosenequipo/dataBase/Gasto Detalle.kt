package com.example.rastreadordegastosenequipo.dataBase

data class GastoDetalle(
    val id: Int = 0,
    val idGasto: Int,
    val idDeudor: Int,
    val montoDeuda: Double
)