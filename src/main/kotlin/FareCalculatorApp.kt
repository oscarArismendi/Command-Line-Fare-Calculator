package org.example

import com.github.michaelbull.result.fold
import org.example.application.adapters.CalculateFareAdapter
import org.example.application.handlers.CalculateFareHandler
import org.example.application.ports.CalculateFarePort
import org.example.application.ports.out.FareTariffPort
import org.example.infrastructure.repositories.ExcelFareTariffRepository
import org.example.utils.ui.FareRequestCommandLineUI

fun main(args: Array<String>) {
    val fareAdapter: CalculateFarePort = CalculateFareAdapter()
    val fareTariffRepository: FareTariffPort = ExcelFareTariffRepository()

    val calculateFareHandler = CalculateFareHandler(fareAdapter, fareTariffRepository)

    val fareResult = calculateFareHandler.handleFareCalculation(args)

    fareResult.fold(
        success = {
            val fareRequestCommandLineUI = FareRequestCommandLineUI(it)
            fareRequestCommandLineUI.printFare()
        },
        failure = { println("Couldn't complete the request") },
    )
}
// TODO: Write more tests
// TODO: Package name
// TODO: High priority!! Every commit need a test and a documentation
