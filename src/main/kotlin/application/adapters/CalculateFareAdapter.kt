package org.example.application.adapters

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import org.example.application.ports.CalculateFarePort
import org.example.application.ports.out.FareTariffPort
import org.example.domain.dtos.FareCalculationResult
import org.example.domain.dtos.FareRequest
import org.example.utils.error.FareRepositoryErrors
import org.example.utils.error.FareRepositoryErrors.FareNotFound
import org.example.utils.error.FareRepositoryErrors.InvalidStationError
import org.example.utils.error.FareRepositoryErrors.RiderTypeNotFoundError
import com.github.michaelbull.result.Result
import io.github.oshai.kotlinlogging.KotlinLogging

class CalculateFareAdapter : CalculateFarePort {
    private val logger = KotlinLogging.logger {}

    override fun calculateFare(fareRequest: FareRequest, fareTariffRepository: FareTariffPort): Result<FareCalculationResult,FareRepositoryErrors> {

        try {
            val validStations = fareTariffRepository.getAllStations()

            if ( !validStations.any { it.name == fareRequest.origin }) {
                throw InvalidStationError(fareRequest.origin)
            }

            if ( !validStations.any { it.name == fareRequest.destination }) {
                throw InvalidStationError(fareRequest.destination)
            }
            val validRiderTypes = fareTariffRepository.getAllRiderTypes()

            if ( !validRiderTypes.any { it.value.uppercase() == fareRequest.riderType.uppercase() }) {
                throw RiderTypeNotFoundError(fareRequest.riderType)
            }

            return Ok( fareTariffRepository.findFare(fareRequest) )
        } catch (e: FareRepositoryErrors) {
            when (e) {
                is FareNotFound -> {
                    logger.error { "Fare not found in the tariff -> ${e.message}" }
                }
                is InvalidStationError -> {
                    logger.error { "Tariff not found in the tariff -> ${e.message}" }
                }
                is RiderTypeNotFoundError -> {
                    logger.error { "Rider Type not found in the tariff -> ${e.message}" }
                }
            }

            return Err(e)
        }

    }

}

// TODO: Spotless