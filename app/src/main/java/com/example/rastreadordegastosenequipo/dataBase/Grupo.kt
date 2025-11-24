package com.example.rastreadordegastosenequipo.dataBase

data class Grupo(
    val id: Int = 0,
    val nombre: String,
    val fechaCreacion: Long = System.currentTimeMillis()
)