package com.example.rastreadordegastosenequipo.dataBase

data class Miembro(
    val id: Int = 0,
    val nombre: String,
    val idGrupo: Int
) {
    override fun toString(): String = nombre
}