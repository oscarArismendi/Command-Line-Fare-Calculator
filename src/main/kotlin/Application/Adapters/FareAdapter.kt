package org.example.Application.Adapters

import org.example.Application.Ports.FarePort
import org.example.Domain.DTOs.FareRequest

class FareAdapter: FarePort {
    override fun calculateFare(fareRequest: FareRequest): Double {
        if(fareRequest.origin == "A"&& fareRequest.destination == "B" && fareRequest.riderType == "Adult" ){
            return 2.5
        }

        return 0.0 // TODO 5: Refactor unexpected input scenarios
    }

}