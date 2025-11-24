package com.example.rastreadordegastosenequipo.dataBase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BD(context: Context) : SQLiteOpenHelper(context, "GastosTeam.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE grupos (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, fecha_creacion LONG)")
        db?.execSQL("CREATE TABLE miembros (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, id_grupo INTEGER)")
        db?.execSQL("CREATE TABLE gastos (id INTEGER PRIMARY KEY AUTOINCREMENT, descripcion TEXT, monto REAL, fecha LONG, id_pagador INTEGER, id_grupo INTEGER)")
        db?.execSQL("CREATE TABLE gasto_detalle (id INTEGER PRIMARY KEY AUTOINCREMENT, id_gasto INTEGER, id_deudor INTEGER, monto_deuda REAL)")
        db?.execSQL("CREATE TABLE pagos (id INTEGER PRIMARY KEY AUTOINCREMENT, id_grupo INTEGER, id_pagador INTEGER, id_beneficiario INTEGER, monto REAL, fecha LONG)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
}