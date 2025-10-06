package org.example.application.adapters

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import org.example.application.ports.CalculateFarePort
import org.example.application.ports.out.FareTariffPort
import org.example.domain.dtos.FareCalculationResult
import org.example.domain.dtos.FareRequest
import org.example.utils.error.FareRepositoryErrors
import org.example.utils.error.FareRepositoryErrors.FareNotFound
import com.github.michaelbull.result.Result

class CalculateFareAdapter : CalculateFarePort {
    override fun calculateFare(fareRequest: FareRequest, fareTariffRepository: FareTariffPort): Result<FareCalculationResult,FareRepositoryErrors> {

        // TODO: Validate fare
        try {
             return Ok( fareTariffRepository.findFare(fareRequest) )
        } catch (e: FareRepositoryErrors) {
            return when (e) {
                is FareNotFound -> Err(e)
            }
        }

    }

}

// TODO: Spotless