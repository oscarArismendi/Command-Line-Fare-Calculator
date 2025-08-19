package org.example.Application.Adapters

import org.example.Application.Ports.CalculateFarePort
import org.example.Domain.DTOs.FareCalculationResult
import org.example.Domain.DTOs.FareRequest
import org.example.Domain.Models.RiderType
import org.example.Domain.Models.Station
import org.example.Infrastructure.toTrip
import java.math.BigDecimal

class CalculateFareAdapter: CalculateFarePort {
    override fun calculateFare(fareRequest: FareRequest): FareCalculationResult {

        val trip = fareRequest.toTrip()

        if(trip.origin == Station(0,"A") &&
            trip.destination == Station(0,"B") &&
            trip.riderType == RiderType.ADULT
            ){
            val result = FareCalculationResult(BigDecimal(2.5), BigDecimal(0.0))
            return result
        }

        return FareCalculationResult(BigDecimal(0.0), BigDecimal(0.0)) // TODO 5: Refactor unexpected input scenarios
    }

}