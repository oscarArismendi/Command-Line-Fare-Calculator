package org.fare.calculator

import com.github.michaelbull.result.fold
import org.fare.calculator.application.adapters.CalculateFareAdapter
import org.fare.calculator.application.handlers.CalculateFareHandler
import org.fare.calculator.application.ports.CalculateFarePort
import org.fare.calculator.application.ports.out.FareTariffPort
import org.fare.calculator.infrastructure.repositories.ExcelFareTariffRepository
import org.fare.calculator.utils.ui.FareRequestCommandLineUI

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
// TODO: High priority!! Every commit need a test and a documentation
