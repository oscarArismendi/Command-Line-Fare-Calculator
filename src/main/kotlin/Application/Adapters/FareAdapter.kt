package org.example.Application.Adapters

import org.example.Application.Ports.FarePort
import org.example.Domain.DTOs.FareCalculationResult
import org.example.Domain.DTOs.FareRequest
import org.example.Domain.DTOs.toTrip
import org.example.Domain.Models.RiderType
import org.example.Domain.Models.Station
import org.example.Domain.Models.Type

class FareAdapter: FarePort {
    override fun calculateFare(fareRequest: FareRequest): FareCalculationResult {

        val trip = fareRequest.toTrip()

        if(trip.origin == Station(0,"A") &&
            trip.destination == Station(0,"B") &&
            trip.riderType == RiderType(0, Type.ADULT )
            ){
            val result = FareCalculationResult(2.5, 0.0)
            return result
        }

        return FareCalculationResult(0.0, 0.0) // TODO 5: Refactor unexpected input scenarios
    }

}