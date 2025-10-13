package org.example.application.ports

import com.github.michaelbull.result.Result
import org.example.application.ports.out.FareTariffPort
import org.example.domain.dtos.FareCalculationResult
import org.example.domain.dtos.FareRequest
import org.example.utils.error.FareRepositoryErrors

interface CalculateFarePort {
    fun calculateFare(
        fareRequest: FareRequest,
        fareTariffRepository: FareTariffPort,
    ): Result<FareCalculationResult, FareRepositoryErrors> // Need to handle different currencies
}
