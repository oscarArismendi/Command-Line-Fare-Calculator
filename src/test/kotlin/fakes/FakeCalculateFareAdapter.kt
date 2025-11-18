package fakes

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.fare.calculator.application.ports.CalculateFarePort
import org.fare.calculator.application.ports.out.FareTariffPort
import org.fare.calculator.domain.dtos.FareCalculationResult
import org.fare.calculator.domain.dtos.FareRequest
import org.fare.calculator.infrastructure.toTrip
import org.fare.calculator.utils.error.FareRepositoryErrors

class FakeCalculateFareAdapter : CalculateFarePort {
    override fun calculateFare(
        fareRequest: FareRequest,
        fareTariffRepository: FareTariffPort,
    ): Result<FareCalculationResult, FareRepositoryErrors> {
        return try {
            val trip = fareRequest.toTrip()
            Ok(fareTariffRepository.findFare(trip))
        } catch (e: FareRepositoryErrors) {
            Err(e)
        } catch (e: IllegalArgumentException) {
            Err(FareRepositoryErrors.FareNotFound())
        }
    }
}
