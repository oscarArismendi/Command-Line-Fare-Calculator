package org.fare.calculator.application.adapters

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.oshai.kotlinlogging.KotlinLogging
import org.fare.calculator.application.ports.CalculateFarePort
import org.fare.calculator.application.ports.out.FareTariffPort
import org.fare.calculator.domain.dtos.FareCalculationResult
import org.fare.calculator.domain.dtos.FareRequest
import org.fare.calculator.infrastructure.toTrip
import org.fare.calculator.utils.error.FareRepositoryErrors
import org.fare.calculator.utils.error.FareRepositoryErrors.FareNotFound
import org.fare.calculator.utils.error.FareRepositoryErrors.InvalidJourneyError
import org.fare.calculator.utils.error.FareRepositoryErrors.InvalidStationError
import org.fare.calculator.utils.error.FareRepositoryErrors.InvalidTimeError
import org.fare.calculator.utils.error.FareRepositoryErrors.RiderTypeNotFoundError

class CalculateFareAdapter : CalculateFarePort {
    private val logger = KotlinLogging.logger {}

    override fun calculateFare(
        fareRequest: FareRequest,
        fareTariffRepository: FareTariffPort,
    ): Result<FareCalculationResult, FareRepositoryErrors> {
        try {
            val validRiderTypes = fareTariffRepository.getAllRiderTypes()

            if (!validRiderTypes.any { it.value.uppercase() == fareRequest.riderType.uppercase() }) {
                throw RiderTypeNotFoundError(fareRequest.riderType.uppercase())
            }

            val trip = fareRequest.toTrip()
            val validStations = fareTariffRepository.getAllStations()

            if (!validStations.any { trip.origin.name == it.name }) {
                throw InvalidStationError(trip.origin.name)
            }

            if (!validStations.any { it.name == trip.destination.name }) {
                throw InvalidStationError(trip.destination.name)
            }

            return Ok(fareTariffRepository.findFare(trip))
        } catch (e: FareRepositoryErrors) {
            when (e) {
                is FareNotFound -> {
                    logger.error { "Fare not found in the tariff -> ${e.message}" }
                }
                is InvalidStationError -> {
                    logger.error { "Station not found in the tariff -> ${e.message}" }
                }
                is RiderTypeNotFoundError -> {
                    logger.error { "Rider Type not found in the tariff -> ${e.message}" }
                }
                is InvalidJourneyError -> {
                    logger.error { "Fare not found in the tariff -> ${e.message}" }
                }

                is InvalidTimeError -> {
                    logger.error { "Tariff not found in the tariff -> ${e.message}" }
                }
            }

            return Err(e)
        } catch (e: IllegalArgumentException) {
            return Err(FareNotFound())
        }
    }
}
