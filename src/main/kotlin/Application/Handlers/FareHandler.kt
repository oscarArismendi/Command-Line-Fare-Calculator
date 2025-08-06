package org.example.Application.Handlers

import org.example.Application.Ports.FarePort
import org.example.Domain.DTOs.FareRequest

class FareHandler(val fareService: FarePort){

    fun handleFareCalculation(args: Array<String>): Double {
        val fareRequest = parseInput(args)
        val result = fareService.calculateFare(fareRequest)
        return result
    }

    private fun parseInput(args: Array<String>): FareRequest {
        try {
            val fareRequest: FareRequest = FareRequest(args[0], args[1], args[2])
            return fareRequest
        } catch(e : Exception){
            throw IllegalArgumentException("Invalid input format. Expected: origin destination riderType")
        }
    }
    // TODO 3: The parse input should be outside the FareHandler, to follow Single Responsibility Principle

}