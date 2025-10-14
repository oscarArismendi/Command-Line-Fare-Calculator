package org.example.utils

import org.example.domain.dtos.FareCalculationResult

class FareRequestCommandLineUI(val fareCalculationResult: FareCalculationResult) {
    private val currency = fareCalculationResult.total.currency
    private val total = fareCalculationResult.total.amount
    private val baseFare = fareCalculationResult.baseFare.amount
    private val discount = fareCalculationResult.discount.amount

    fun printFare() {
        println("Your fare is: $$total $currency")
        println("Breakdown:")
        println("    - Base Fare: $$baseFare $currency")
        println("    - Discount: $$discount $currency")
    }
}
