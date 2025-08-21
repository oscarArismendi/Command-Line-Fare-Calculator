package org.example

import org.example.application.adapters.CalculateFareAdapter
import org.example.application.handlers.CalculateFareHandler
import org.example.application.ports.CalculateFarePort
import org.example.application.ports.out.FareTariffPort
import org.example.infrastructure.repositories.ExcelFareTariffRepository
import org.example.utils.FareRequestCommandLineUI

fun main(args: Array<String>) {
    val fareAdapter : CalculateFarePort = CalculateFareAdapter()
    val fareTariffRepository: FareTariffPort = ExcelFareTariffRepository()

    val calculateFareHandler = CalculateFareHandler(fareAdapter, fareTariffRepository)


    val fareResult = calculateFareHandler.handleFareCalculation(args)

    val fareRequestCommandLineUI = FareRequestCommandLineUI(fareResult)
    fareRequestCommandLineUI.printFare()
    // TODO 1: Create an UI folder inside infrastructure to handle CLI
}