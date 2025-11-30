package com.example.rastreadordegastosenequipo.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rastreadordegastosenequipo.R
import com.example.rastreadordegastosenequipo.control.DebtSimplifier
import com.example.rastreadordegastosenequipo.dataBase.GastosManager
import com.example.rastreadordegastosenequipo.dataBase.BD
import com.example.rastreadordegastosenequipo.ui.SettlementAdapter // <-- ¡IMPORTACIÓN NECESARIA!
import com.example.rastreadordegastosenequipo.control.SettlementTransaction // <-- ¡IMPORTACIÓN NECESARIA!

class SettlementActivity : AppCompatActivity() {

    private lateinit var rvTransactions: RecyclerView
    private lateinit var btnCompleteSettlement: Button
    private lateinit var tvNoDebts: TextView

    // Instancias de las clases de control y data
    private val debtSimplifier = DebtSimplifier()
    private lateinit var gastosManager: GastosManager

    private var currentGroupId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settlement)

        // Obtener ID del Intent y validar
        currentGroupId = intent.getIntExtra("ID", 0)

        if (currentGroupId == 0) {
            Toast.makeText(this, "Error: ID de grupo no válido.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Inicializar el Gestor de Base de Datos
        val dbHelper = BD(this)
        gastosManager = GastosManager(dbHelper.writableDatabase)


        // 3. Enlazar vistas
        rvTransactions = findViewById(R.id.rv_settlement_transactions)
        btnCompleteSettlement = findViewById(R.id.btn_complete_settlement)
        tvNoDebts = findViewById(R.id.tv_no_debts)
        rvTransactions.layoutManager = LinearLayoutManager(this)

        // Cargar la ruta de liquidación
        loadSettlementRoute()

        // Asignar el listener al botón de liquidación completa
        btnCompleteSettlement.setOnClickListener {
            completeSettlement()
        }
    }

    private fun loadSettlementRoute() {
        val netBalances = gastosManager.getNetBalances(currentGroupId)
        val transactions = debtSimplifier.simplifyDebts(netBalances)

        if (transactions.isEmpty()) {
            tvNoDebts.visibility = View.VISIBLE
            rvTransactions.visibility = View.GONE
            btnCompleteSettlement.visibility = View.GONE
        } else {
            tvNoDebts.visibility = View.GONE
            rvTransactions.visibility = View.VISIBLE
            btnCompleteSettlement.visibility = View.VISIBLE


            val adapter = SettlementAdapter(transactions)
            rvTransactions.adapter = adapter
        }
    }

    private fun completeSettlement() {
        val success = gastosManager.resetGroupBalances(currentGroupId)

        if (success) {
            Toast.makeText(this, "Liquidación completada. Saldos restablecidos a cero.", Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(this, "Error al completar la liquidación. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
        }
    }
}