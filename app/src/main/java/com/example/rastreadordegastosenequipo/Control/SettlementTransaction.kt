package com.example.rastreadordegastosenequipo.control

/**
 * Representa una transacción de pago simplificada.
 * Es la instrucción clara que se mostrará al usuario.
 */
data class SettlementTransaction(
    val from: String,    // Quién paga (Deudor)
    val to: String,      // A quién paga (Acreedor)
    val amount: Double   // La cantidad a pagar
)