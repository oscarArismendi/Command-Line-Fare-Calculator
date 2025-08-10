package org.example

import org.example.Application.Adapters.CalculateFareAdapter
import org.example.Application.Handlers.CalculateFareHandler
import org.example.Application.Ports.CalculateFarePort

fun main(args: Array<String>) {
    val fareAdapter : CalculateFarePort = CalculateFareAdapter()

    val calculateFareHandler : CalculateFareHandler = CalculateFareHandler(fareAdapter)

    val result = calculateFareHandler.handleFareCalculation(args)
    println("Your fare is: $${result.total} ${result.currency}")
    println("Breakdown:")
    println("    - Base Fare: $${result.baseFare} ${result.currency}")
    println("    - Discount: $${result.discount} ${result.currency}")
    // TODO 4: Create an UI folder inside infrastructure to handle CLI
}