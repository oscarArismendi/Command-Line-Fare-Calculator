package org.example

import org.example.application.adapters.CalculateFareAdapter
import org.example.application.handlers.CalculateFareHandler
import org.example.application.ports.CalculateFarePort
import org.example.application.ports.out.FareTariffPort
import org.example.infrastructure.repositories.ExcelFareTariffRepository

fun main(args: Array<String>) {
    val fareAdapter : CalculateFarePort = CalculateFareAdapter()
    val fareTariffRepository: FareTariffPort = ExcelFareTariffRepository()

    val calculateFareHandler = CalculateFareHandler(fareAdapter, fareTariffRepository)


    val result = calculateFareHandler.handleFareCalculation(args)
    println("Your fare is: $${result.total} ${result.currency}")
    println("Breakdown:")
    println("    - Base Fare: $${result.baseFare} ${result.currency}")
    println("    - Discount: $${result.discount} ${result.currency}")
    // TODO 1: Create an UI folder inside infrastructure to handle CLI
}