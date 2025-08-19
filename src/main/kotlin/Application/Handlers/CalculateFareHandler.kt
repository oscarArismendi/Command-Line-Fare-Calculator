package org.example.Application.Handlers

import org.example.Application.Ports.CalculateFarePort
import org.example.Application.Ports.out.FareTariffPort
import org.example.Domain.DTOs.FareCalculationResult
import org.example.utils.parseInput

class CalculateFareHandler(val fareService: CalculateFarePort, val fareTariffRepository : FareTariffPort){

    fun handleFareCalculation(args: Array<String>): FareCalculationResult {
        val fareRequest = parseInput(args)
        return fareService.calculateFare(fareRequest, fareTariffRepository)
    }


}