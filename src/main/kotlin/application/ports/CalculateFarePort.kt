package org.example.application.ports

import org.example.application.ports.out.FareTariffPort
import org.example.domain.dtos.FareCalculationResult
import org.example.domain.dtos.FareRequest

interface CalculateFarePort {
    fun calculateFare(fareRequest: FareRequest, fareTariffRepository: FareTariffPort): FareCalculationResult // Need to handle different currencies
}