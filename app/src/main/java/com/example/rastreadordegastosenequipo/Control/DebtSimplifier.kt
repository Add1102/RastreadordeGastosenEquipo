package com.example.rastreadordegastosenequipo.control

import kotlin.math.absoluteValue

class DebtSimplifier {

    /**
     * Calcula la ruta de liquidación óptima para el grupo (Minimum Cash Flow).
     *
     * @param netBalances Mapa de (Nombre del Miembro, Saldo Neto).
     * Positivo: le deben a la persona. Negativo: la persona debe.
     * @return Lista de SettlementTransaction que representan los pagos simplificados.
     */
    fun simplifyDebts(netBalances: Map<String, Double>): List<SettlementTransaction> {
        val transactions = mutableListOf<SettlementTransaction>()

        // Usamos un mapa mutable para actualizar los saldos a medida que se liquidan.
        val balances = netBalances.filterValues { it.absoluteValue > 0.01 }.toMutableMap()

        // Continuar mientras haya saldos significativos por liquidar.
        while (balances.values.any { it.absoluteValue > 0.01 }) {

            // 1. Encontrar al mayor DEUDOR (saldo más negativo).
            val debtor = balances.entries.minByOrNull { it.value } ?: break

            // 2. Encontrar al mayor ACREEDOR (saldo más positivo).
            val creditor = balances.entries.maxByOrNull { it.value } ?: break

            // Si el mayor deudor ya no debe, o el mayor acreedor ya no es acreedor, se termina.
            if (debtor.value >= 0.01 || creditor.value <= -0.01) break

            // 3. Determinar la cantidad de liquidación
            // Es el mínimo entre lo que el deudor debe y lo que se le debe al acreedor.
            val settlementAmount = minOf(debtor.value.absoluteValue, creditor.value)

            // 4. Registrar la transacción
            transactions.add(SettlementTransaction(
                from = debtor.key,
                to = creditor.key,
                amount = settlementAmount
            ))

            // 5. Actualizar saldos en el mapa
            // Al deudor se le reduce la deuda (se le suma la cantidad, haciéndola menos negativa).
            balances[debtor.key] = balances.getValue(debtor.key) + settlementAmount

            // Al acreedor se le reduce lo que se le debe (se le resta la cantidad, haciéndola menos positiva).
            balances[creditor.key] = balances.getValue(creditor.key) - settlementAmount
        }

        return transactions
    }
}