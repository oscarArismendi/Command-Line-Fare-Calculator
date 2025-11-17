package application.adapters

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.fare.calculator.application.adapters.CalculateFareAdapter
import org.fare.calculator.domain.dtos.FareRequest
import org.fare.calculator.infrastructure.repositories.ExcelFareTariffRepository
import org.junit.jupiter.api.Test
import java.math.BigDecimal

private const val FARE_ONE_STOP_ADULT = 10.0

private const val FARE_ONE_STOP_CHILD = 8.0

private const val FARE_TWO_STOPS_ADULT = 20.0

private const val FARE_THREE_STOPS_ADULT = 30.0

class CalculateFareAdapterTest {

    val excelFareTariffRepository = ExcelFareTariffRepository()
    val calculateFareAdapter = CalculateFareAdapter()


    //Happy path tests
    @Test
    fun `calculate Fare returns a big decimal`() {
        // Given
        val dummyFareRequest = FareRequest(origin = "A", destination = "B", riderType = "Adult")
        // When
        val result = calculateFareAdapter.calculateFare(dummyFareRequest, excelFareTariffRepository)
        // Then
        val fare = result.get().shouldNotBeNull()
        fare.total.amount shouldBe BigDecimal.valueOf(FARE_ONE_STOP_ADULT)
    }

    @Test
    fun `calculates discounted fares for Child and Senior rider types`() {
        // Given
        val dummyChildFareRequest = FareRequest(origin = "A", destination = "B", riderType = "Child")
        val dummySeniorFareRequest = FareRequest(origin = "A", destination = "B", riderType = "Senior")
        // When
        val childResult = calculateFareAdapter.calculateFare(dummyChildFareRequest, excelFareTariffRepository)
        val seniorResult = calculateFareAdapter.calculateFare(dummySeniorFareRequest, excelFareTariffRepository)
        // Then
        val childFare = childResult.get().shouldNotBeNull()
        val seniorFare = seniorResult.get().shouldNotBeNull()
        childFare.total.amount shouldBe BigDecimal.valueOf(FARE_ONE_STOP_CHILD)
        seniorFare.total.amount shouldBe BigDecimal.valueOf(6.0)
    }

    @Test
    fun `calculates correct fares for different station combinations`() {
        // Given
        val dummyAtoBFareRequest = FareRequest(origin = "A", destination = "B", riderType = "Adult")
        val dummyAtoCFareRequest = FareRequest(origin = "A", destination = "C", riderType = "Adult")
        val dummyAtoDFareRequest = FareRequest(origin = "A", destination = "D", riderType = "Adult")
        val dummyBtoCFareRequest = FareRequest(origin = "B", destination = "C", riderType = "Adult")
        val dummyBtoDFareRequest = FareRequest(origin = "B", destination = "D", riderType = "Adult")
        val dummyCtoDFareRequest = FareRequest(origin = "C", destination = "D", riderType = "Adult")
        // When
        val resultAToB = calculateFareAdapter.calculateFare(dummyAtoBFareRequest, excelFareTariffRepository)
        val resultAtoC = calculateFareAdapter.calculateFare(dummyAtoCFareRequest, excelFareTariffRepository)
        val resultAToD = calculateFareAdapter.calculateFare(dummyAtoDFareRequest, excelFareTariffRepository)
        val resultBtoC = calculateFareAdapter.calculateFare(dummyBtoCFareRequest, excelFareTariffRepository)
        val resultBToD = calculateFareAdapter.calculateFare(dummyBtoDFareRequest, excelFareTariffRepository)
        val resultCtoD = calculateFareAdapter.calculateFare(dummyCtoDFareRequest, excelFareTariffRepository)
        // Then
        val fareAToB = resultAToB.get().shouldNotBeNull()
        val fareAtoC = resultAtoC.get().shouldNotBeNull()
        val fareAToD = resultAToD.get().shouldNotBeNull()
        val fareBtoC = resultBtoC.get().shouldNotBeNull()
        val fareBToD = resultBToD.get().shouldNotBeNull()
        val fareCtoD = resultCtoD.get().shouldNotBeNull()
        fareAToB.total.amount shouldBe BigDecimal.valueOf(FARE_ONE_STOP_ADULT)
        fareAtoC.total.amount shouldBe BigDecimal.valueOf(FARE_TWO_STOPS_ADULT)
        fareAToD.total.amount shouldBe BigDecimal.valueOf(FARE_THREE_STOPS_ADULT)
        fareBtoC.total.amount shouldBe BigDecimal.valueOf(FARE_ONE_STOP_ADULT)
        fareBToD.total.amount shouldBe BigDecimal.valueOf(FARE_TWO_STOPS_ADULT)
        fareCtoD.total.amount shouldBe BigDecimal.valueOf(FARE_ONE_STOP_ADULT)
    }
    @Test
    fun `handles rider type case insensitively`() {
        // Given
        val dummyFareRequest = FareRequest(origin = "A", destination = "B", riderType = "aDuLt")
        // When
        val result = calculateFareAdapter.calculateFare(dummyFareRequest, excelFareTariffRepository)
        // Then
        val fare = result.get().shouldNotBeNull()
        fare.total.amount shouldBe BigDecimal.valueOf(FARE_ONE_STOP_ADULT)
    }

    // Error path
    @Test
    fun `returns InvalidStationError when origin station is not in tariff`() {
        // Given
        val dummyFareRequest = FareRequest(origin = "Invalid origin station", destination = "B", riderType = "Adult")
        // When
        val result = calculateFareAdapter.calculateFare(dummyFareRequest, excelFareTariffRepository)
        // Then
        result.isErr shouldBe true
        val error = result.getError().shouldNotBeNull()
        error.message shouldBe "Invalid station provided: Invalid origin station"
    }

    @Test
    fun `returns InvalidStationError when destination station is not in tariff`() {
        // Given
        val dummyFareRequest = FareRequest(origin = "A", destination = "Invalid destination station", riderType = "Adult")
        // When
        val result = calculateFareAdapter.calculateFare(dummyFareRequest, excelFareTariffRepository)
        // Then
        result.isErr shouldBe true
        val error = result.getError().shouldNotBeNull()
        error.message shouldBe "Invalid station provided: Invalid destination station"
    }

    @Test
    fun `returns RiderTypeNotFoundError when rider type is not in tariff`() {
        // Given
        val dummyFareRequest = FareRequest(origin = "A", destination = "B", riderType = "dummy riderType")
        // When
        val result = calculateFareAdapter.calculateFare(dummyFareRequest, excelFareTariffRepository)
        // Then
        result.isErr shouldBe true
        val error = result.getError().shouldNotBeNull()
        error.message shouldBe "Rider type not found: DUMMY RIDERTYPE"
    }
}
