package org.example.application.adapters

import org.example.application.ports.CalculateFarePort
import org.example.application.ports.out.FareTariffPort
import org.example.domain.dtos.FareCalculationResult
import org.example.domain.dtos.FareRequest

class CalculateFareAdapter: CalculateFarePort {
    override fun calculateFare(fareRequest: FareRequest, fareTariffRepository: FareTariffPort): FareCalculationResult {


        return fareTariffRepository.findFare(fareRequest)
    }

}