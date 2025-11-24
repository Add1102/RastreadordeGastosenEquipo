package com.example.rastreadordegastosenequipo.ui

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rastreadordegastosenequipo.R
import com.example.rastreadordegastosenequipo.dataBase.BD
import com.example.rastreadordegastosenequipo.dataBase.Grupo
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GestionGruposActivity : AppCompatActivity() {
    private lateinit var listaVisual: ListaGrupos
    private val datos = mutableListOf<Grupo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_grupos)

        val rv = findViewById<RecyclerView>(R.id.rvGrupos)
        rv.layoutManager = LinearLayoutManager(this)

        listaVisual = ListaGrupos(
            datos = datos,
            clic = { grupo ->
                abrirDetalleGrupo(grupo.id, grupo.nombre)
            },
            alMantenerPresionado = { grupo ->
                mostrarDialogoBorrar(grupo)
            }
        )
        rv.adapter = listaVisual

        findViewById<FloatingActionButton>(R.id.btnAgregarGrupo).setOnClickListener {
            mostrarDialogoNombreGrupo()
        }
    }

    override fun onResume() {
        super.onResume()
        cargar()
    }

    private fun cargar() {
        datos.clear()
        val db = BD(this).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM grupos ORDER BY id DESC", null)
        if (cursor.moveToFirst()) {
            do {
                datos.add(Grupo(cursor.getInt(0), cursor.getString(1), cursor.getLong(2)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        listaVisual.actualizar(datos)
    }

    private fun mostrarDialogoNombreGrupo() {
        val input = EditText(this)
        input.hint = "Ej. Viaje a la Playa"

        AlertDialog.Builder(this)
            .setTitle("Paso 1: Nombre del Grupo")
            .setView(input)
            .setPositiveButton("Siguiente") { _, _ ->
                val nombre = input.text.toString().trim()
                if (nombre.isNotEmpty()) {
                    mostrarDialogoMiembrosIniciales(nombre)
                } else {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoMiembrosIniciales(nombreGrupo: String) {
        val context = this
        val contenedorPrincipal = LinearLayout(context)
        contenedorPrincipal.orientation = LinearLayout.VERTICAL
        contenedorPrincipal.setPadding(50, 40, 50, 10)

        val contenedorCampos = LinearLayout(context)
        contenedorCampos.orientation = LinearLayout.VERTICAL

        val scrollView = ScrollView(context)
        scrollView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            500
        )
        scrollView.addView(contenedorCampos)

        fun agregarFila() {
            val input = EditText(context)
            input.hint = "Nombre del integrante"
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 16)
            input.layoutParams = params
            contenedorCampos.addView(input)
        }

        agregarFila()
        agregarFila()

        val btnMas = Button(context)
        btnMas.text = "+ Agregar otro"
        btnMas.setOnClickListener { agregarFila() }

        contenedorPrincipal.addView(scrollView)
        contenedorPrincipal.addView(btnMas)

        AlertDialog.Builder(this)
            .setTitle("Paso 2: Agregar Miembros")
            .setMessage("Agrega los integrantes para '$nombreGrupo':")
            .setView(contenedorPrincipal)
            .setCancelable(false)
            .setPositiveButton("Crear Grupo") { _, _ ->
                val listaNombres = mutableListOf<String>()
                for (i in 0 until contenedorCampos.childCount) {
                    val vista = contenedorCampos.getChildAt(i)
                    if (vista is EditText) {
                        val nombre = vista.text.toString().trim()
                        if (nombre.isNotEmpty()) listaNombres.add(nombre)
                    }
                }

                if (listaNombres.isNotEmpty()) {
                    guardarTodo(nombreGrupo, listaNombres)
                } else {
                    Toast.makeText(this, "El grupo debe tener al menos un miembro", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancelar Operación") { _, _ ->

            }
            .show()
    }

    private fun guardarTodo(nombreGrupo: String, miembros: List<String>) {
        val db = BD(this).writableDatabase

        val valuesGrupo = ContentValues().apply {
            put("nombre", nombreGrupo)
            put("fecha_creacion", System.currentTimeMillis())
        }
        val idGrupo = db.insert("grupos", null, valuesGrupo)

        if (idGrupo > 0) {
            for (nombre in miembros) {
                val valuesMiembro = ContentValues().apply {
                    put("nombre", nombre)
                    put("id_grupo", idGrupo.toInt())
                }
                db.insert("miembros", null, valuesMiembro)
            }
            db.close()
            Toast.makeText(this, "Grupo creado", Toast.LENGTH_SHORT).show()
            cargar()
            abrirDetalleGrupo(idGrupo.toInt(), nombreGrupo)
        } else {
            db.close()
        }
    }

    private fun abrirDetalleGrupo(id: Int, nombre: String) {
        val intent = Intent(this, DetalleGrupoActivity::class.java)
        intent.putExtra("ID", id)
        intent.putExtra("NOMBRE", nombre)
        startActivity(intent)
    }

    private fun mostrarDialogoBorrar(grupo: Grupo) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Grupo")
            .setMessage("¿Borrar '${grupo.nombre}' y todos sus datos?")
            .setPositiveButton("Eliminar") { _, _ ->
                borrarGrupoDeBD(grupo.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun borrarGrupoDeBD(id: Int) {
        val db = BD(this).writableDatabase
        db.delete("grupos", "id=?", arrayOf(id.toString()))
        db.delete("miembros", "id_grupo=?", arrayOf(id.toString()))
        db.delete("gastos", "id_grupo=?", arrayOf(id.toString()))
        db.delete("gasto_detalle", "id_gasto IN (SELECT id FROM gastos WHERE id_grupo=?)", arrayOf(id.toString()))
        db.delete("pagos", "id_grupo=?", arrayOf(id.toString()))

        db.close()
        Toast.makeText(this, "Grupo eliminado", Toast.LENGTH_SHORT).show()
        cargar()
    }
}