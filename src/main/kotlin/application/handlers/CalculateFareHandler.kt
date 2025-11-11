package org.fare.calculator.application.handlers

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import io.github.oshai.kotlinlogging.KotlinLogging
import org.fare.calculator.application.ports.CalculateFarePort
import org.fare.calculator.application.ports.out.FareTariffPort
import org.fare.calculator.domain.dtos.FareCalculationResult
import org.fare.calculator.utils.error.FareRepositoryErrors
import org.fare.calculator.utils.error.FareRepositoryErrors.FareNotFound
import org.fare.calculator.utils.error.FareRepositoryErrors.InvalidJourneyError
import org.fare.calculator.utils.error.FareRepositoryErrors.InvalidStationError
import org.fare.calculator.utils.error.FareRepositoryErrors.InvalidTimeError
import org.fare.calculator.utils.error.FareRepositoryErrors.RiderTypeNotFoundError
import org.fare.calculator.utils.parseInput
import java.time.LocalTime

class CalculateFareHandler(val fareService: CalculateFarePort, val fareTariffRepository: FareTariffPort) {
    private val logger = KotlinLogging.logger {}

    fun handleFareCalculation(args: Array<String>): Result<FareCalculationResult, FareRepositoryErrors> {
        try {
            val fareRequest = parseInput(args)

            if (fareRequest.origin == fareRequest.destination) {
                throw InvalidJourneyError()
            }

            if (fareRequest.timeStamp.isBefore(
                    LocalTime.now().minusMinutes(5),
                )
            ) {
                println(fareRequest.timeStamp)
                println(LocalTime.now().minusMinutes(5))
                throw InvalidTimeError("Cannot book travel in the past")
            }

            return fareService.calculateFare(fareRequest, fareTariffRepository)
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

                is InvalidJourneyError -> {
                    logger.error { "Fare not found in the tariff -> ${e.message}" }
                }

                is InvalidTimeError -> {
                    logger.error { "Invalid time request -> ${e.message}" }
                }
            }
            return Err(e)
        }
    }
}
