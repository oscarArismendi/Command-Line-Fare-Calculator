package org.example.Application.Ports

import org.example.Application.Ports.out.FareTariffPort
import org.example.Domain.DTOs.FareCalculationResult
import org.example.Domain.DTOs.FareRequest

interface CalculateFarePort {
    fun calculateFare(fareRequest: FareRequest, fareTariffRepository: FareTariffPort): FareCalculationResult // Need to handle different currencies
}