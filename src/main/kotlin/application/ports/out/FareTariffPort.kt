package org.example.application.ports.out

import org.example.domain.dtos.FareCalculationResult
import org.example.domain.dtos.FareRequest

interface FareTariffPort {
    fun findFare(fareRequest: FareRequest): FareCalculationResult
}