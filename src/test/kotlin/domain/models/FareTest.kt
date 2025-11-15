package domain.models

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.fare.calculator.domain.models.Fare
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class FareTest {
    // Happy path
    @Test
    fun `Fare with the same currency can operate between them`() {
        // Given
        val firstUsdFare = Fare(BigDecimal.TEN, "USD")
        val secondUsdFare = Fare(BigDecimal.TEN, "USD")

        // when & then
        val plusResult = firstUsdFare.plus(secondUsdFare)
        val minusResult = firstUsdFare.minus(secondUsdFare)

        // Then
        plusResult.amount shouldBe BigDecimal.valueOf(20)
        minusResult.amount shouldBe BigDecimal.valueOf(0)
    }
    // Error path
    @Test
    fun `Fare with different currencies give IllegalArgument error`() {
        // Given
        val usdFare = Fare(BigDecimal.TEN, "USD")
        val eurosFare = Fare(BigDecimal.TEN, "EUR")

        // when & then
        val plusException = shouldThrow<IllegalArgumentException> {
            usdFare.plus(eurosFare)
        }

        val minusException = shouldThrow<IllegalArgumentException> {
            usdFare.minus(eurosFare)
        }
        // Then
        plusException.message shouldBe "Cannot add fares with different currencies"
        minusException.message shouldBe "Cannot subtract fares with different currencies"
    }

}
