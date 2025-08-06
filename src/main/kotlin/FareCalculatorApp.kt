package org.example

import org.example.Application.Adapters.FareAdapter
import org.example.Application.Handlers.FareHandler
import org.example.Application.Ports.FarePort

fun main(args: Array<String>) {
    val fareAdapter : FarePort = FareAdapter()

    val fareHandler : FareHandler = FareHandler(fareAdapter)

    val result = fareHandler.handleFareCalculation(args)
    println("The calculated fare is: $result")

    // TODO 4: Create an UI folder inside infrastructure to handle CLI
}