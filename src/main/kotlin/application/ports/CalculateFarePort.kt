package org.fare.calculator.application.ports

import com.github.michaelbull.result.Result
import org.fare.calculator.application.ports.out.FareTariffPort
import org.fare.calculator.domain.dtos.FareCalculationResult
import org.fare.calculator.domain.dtos.FareRequest
import org.fare.calculator.utils.error.FareRepositoryErrors

interface CalculateFarePort {
    fun calculateFare(
        fareRequest: FareRequest,
        fareTariffRepository: FareTariffPort,
    ): Result<FareCalculationResult, FareRepositoryErrors> // Need to handle different currencies
}
