package application.handlers

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.kotest.matchers.shouldBe
import org.fare.calculator.application.adapters.CalculateFareAdapter
import org.fare.calculator.application.handlers.CalculateFareHandler
import org.fare.calculator.infrastructure.repositories.ExcelFareTariffRepository
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalTime
import java.time.format.DateTimeFormatter


// Question: Should all const be placed in only one object? or multiple files/object?
private const val TIME_THRESHOLD_MINUTES = 6L
private const val FARE_ONE_STOP_ADULT = 10.0

class CalculateFareHandlerTest {

    val excelFareTariffRepository = ExcelFareTariffRepository()
    val calculateFareAdapter = CalculateFareAdapter()
    val calculateFareHandler = CalculateFareHandler( calculateFareAdapter, excelFareTariffRepository)

    //happy path
    @Test
    fun `successfully handles valid fare request and returns calculated fare`() {
        // Given
        val dummyArgs = arrayOf(
            "--from", "A",
            "--to", "B",
            "--type","Adult"
        )
        // When
        val result = calculateFareHandler.handleFareCalculation(dummyArgs)
        // Then
        result.get()!!.total.amount shouldBe BigDecimal.valueOf(FARE_ONE_STOP_ADULT)
    }

    // Error path
    @Test
    fun `returns InvalidJourneyError when origin and destination are the same`() {
        // Given
        val dummyArgs = arrayOf(
            "--from", "A",
            "--to", "A",
            "--type","Adult"
        )
        // When
        val result = calculateFareHandler.handleFareCalculation(dummyArgs)
        // Then
        result.isErr shouldBe true
        val error = result.getError()!!
        error.message shouldBe "Origin and destination cannot be the same"
    }

    @Test
    fun `returns InvalidTimeError when timestamp is 6 or more minutes in the past`() {
        // Given
        val timeFormat= DateTimeFormatter.ofPattern("HH:mm")
        val fareRequestTimeStamp = LocalTime.now().minusMinutes(TIME_THRESHOLD_MINUTES).format(timeFormat)
        val dummyArgs = arrayOf(
            "--from", "A",
            "--to", "B",
            "--type","Adult",
            "--time", fareRequestTimeStamp
        )
        // When
        val result = calculateFareHandler.handleFareCalculation(dummyArgs)
        // Then
        result.isErr shouldBe true
        val error = result.getError()!!
        error.message shouldBe "It is not possible to book a trip in the past"
    }

}
