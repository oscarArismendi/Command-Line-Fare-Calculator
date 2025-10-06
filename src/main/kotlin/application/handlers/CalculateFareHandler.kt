package org.example.application.handlers

import com.github.michaelbull.result.Result
import org.example.application.ports.CalculateFarePort
import org.example.application.ports.out.FareTariffPort
import org.example.domain.dtos.FareCalculationResult
import org.example.utils.error.FareRepositoryErrors
import org.example.utils.parseInput

class CalculateFareHandler(val fareService: CalculateFarePort, val fareTariffRepository : FareTariffPort){

    fun handleFareCalculation(args: Array<String>): Result<FareCalculationResult, FareRepositoryErrors> {
        val fareRequest = parseInput(args)
        return fareService.calculateFare(fareRequest, fareTariffRepository)
    }


}