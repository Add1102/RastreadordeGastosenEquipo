package com.example.rastreadordegastosenequipo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.rastreadordegastosenequipo.R
import com.example.rastreadordegastosenequipo.dataBase.BD
import com.example.rastreadordegastosenequipo.dataBase.GastosManager
import com.example.rastreadordegastosenequipo.dataBase.Gasto
import java.text.SimpleDateFormat
import java.util.*

class DetallesGastoActivity : AppCompatActivity() {

    private lateinit var gasto: Gasto
    private lateinit var etDescripcion: EditText
    private lateinit var etMonto: EditText
    private lateinit var tvFecha: TextView
    private lateinit var btnGuardar: Button
    private lateinit var btnEliminar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_gasto)

        // Obtener el gasto de los extras
        try {
            @Suppress("DEPRECATION")
            gasto = intent.getSerializableExtra("GASTO") as Gasto
        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar el gasto", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
        cargarDatosGasto()
        setupButtons()
    }

    private fun initViews() {
        etDescripcion = findViewById(R.id.etDescripcion)
        etMonto = findViewById(R.id.etMonto)
        tvFecha = findViewById(R.id.tvFecha)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnEliminar = findViewById(R.id.btnEliminar)
    }

    private fun cargarDatosGasto() {
        etDescripcion.setText(gasto.descripcion)
        etMonto.setText(gasto.monto.toString())

        // Mostrar fecha formateada
        val fechaFormateada = convertirTimestampAFecha(gasto.fecha)
        tvFecha.text = "Fecha: $fechaFormateada"
    }

    private fun setupButtons() {
        // Botón Guardar
        btnGuardar.setOnClickListener {
            guardarCambios()
        }

        // Botón Eliminar
        btnEliminar.setOnClickListener {
            eliminarGasto()
        }
    }

    private fun guardarCambios() {
        try {
            val nuevaDescripcion = etDescripcion.text.toString().trim()
            val nuevoMonto = etMonto.text.toString().toDouble()

            if (nuevaDescripcion.isEmpty()) {
                Toast.makeText(this, "La descripción no puede estar vacía", Toast.LENGTH_SHORT).show()
                return
            }

            if (nuevoMonto <= 0) {
                Toast.makeText(this, "El monto debe ser mayor a 0", Toast.LENGTH_SHORT).show()
                return
            }

            val dbHelper = BD(this)
            val database = dbHelper.writableDatabase
            val gastosManager = GastosManager(database)

            // Crear un NUEVO objeto Gasto con los datos actualizados
            val gastoActualizado = Gasto(
                id = gasto.id,
                descripcion = nuevaDescripcion,
                monto = nuevoMonto,
                fecha = gasto.fecha,
                idPagador = gasto.idPagador,
                idGrupo = gasto.idGrupo
            )

            val filasActualizadas = gastosManager.actualizarGasto(gastoActualizado)

            database.close()

            if (filasActualizadas > 0) {
                Toast.makeText(this, "Gasto actualizado correctamente", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Error al actualizar el gasto", Toast.LENGTH_SHORT).show()
            }

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "El monto debe ser un número válido", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun eliminarGasto() {
        try {
            val dbHelper = BD(this)
            val database = dbHelper.writableDatabase
            val gastosManager = GastosManager(database)

            val filasEliminadas = gastosManager.eliminarGasto(gasto.id)

            database.close()

            if (filasEliminadas > 0) {
                Toast.makeText(this, "Gasto eliminado correctamente", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Error al eliminar el gasto", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertirTimestampAFecha(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }
}