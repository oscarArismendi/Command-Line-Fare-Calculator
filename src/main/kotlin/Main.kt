package org.example

import org.example.Application.FareAdapter
import org.example.Application.FareHandler
import org.example.Application.services.FarePort

fun main(args: Array<String>) {
    val fareAdapter : FarePort = FareAdapter()

    val fareHandler : FareHandler = FareHandler(fareAdapter)

    val result = fareHandler.handleFareCalculation(args)
    println("The calculated fare is: $result")

    // TODO 4: Create an UI folder inside infrastructure to handle CLI
}