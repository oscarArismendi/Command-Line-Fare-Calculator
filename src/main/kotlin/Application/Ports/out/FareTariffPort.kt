package org.example.Application.Ports.out

import org.example.Domain.DTOs.FareCalculationResult
import org.example.Domain.DTOs.FareRequest

interface FareTariffPort {
    fun findFare(fareRequest: FareRequest): FareCalculationResult
}