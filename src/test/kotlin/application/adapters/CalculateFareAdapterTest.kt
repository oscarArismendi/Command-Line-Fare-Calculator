package application.adapters

import com.github.michaelbull.result.get
import io.kotest.matchers.shouldBe
import org.fare.calculator.application.adapters.CalculateFareAdapter
import org.fare.calculator.domain.dtos.FareRequest
import org.fare.calculator.infrastructure.repositories.ExcelFareTariffRepository
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CalculateFareAdapterTest {

    val excelFareTariffRepository = ExcelFareTariffRepository()

    @Test
    fun `calculate Fare returns a big decimal`() {
        // Given
        val calculateFareAdapter = CalculateFareAdapter()
        val dummyFareRequest = FareRequest(origin = "A", destination = "B", riderType = "Adult")

        // When
        val result = calculateFareAdapter.calculateFare(dummyFareRequest, excelFareTariffRepository)
        // Then
        result.get()!!.total.amount shouldBe BigDecimal.valueOf(10.0)
    }

    @Test
    fun `test A`() {
        // Given rider have RiderType is "Child"

        // When the fare is calculated

        // Then the fare should be 8
    }
}
