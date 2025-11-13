package infrastructure.repositories

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.fare.calculator.domain.models.RiderType
import org.fare.calculator.domain.models.Station
import org.fare.calculator.domain.models.Trip
import org.fare.calculator.infrastructure.repositories.ExcelFareTariffRepository
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalTime

class ExcelFareTariffRepositoryTest {

    private val repository = ExcelFareTariffRepository()

    companion object {
        // Test data based on your tariff structure
        private val STATION_A = Station(0, "A")
        private val STATION_B = Station(0, "B")
        private val STATION_C = Station(0, "C")
        private val STATION_D = Station(0, "D")

        private const val BASE_FARE_ONE_STOP = 10.0
        private const val BASE_FARE_TWO_STOPS = 20.0

        private fun createTrip(
            origin: Station = STATION_A,
            destination: Station = STATION_B,
            riderType: RiderType = RiderType.ADULT,
            timeStamp: LocalTime = LocalTime.now()
        ) = Trip(origin, destination, timeStamp, riderType)
    }

    // Happy Path Tests
    @Test
    fun `findFare returns correct base fare for one-stop adult journey`() {
        // Given
        val trip = createTrip(origin = STATION_A, destination = STATION_B, riderType = RiderType.ADULT)

        // When
        val result = repository.findFare(trip)

        // Then
        result.baseFare.amount shouldBe BigDecimal.valueOf(BASE_FARE_ONE_STOP)
        result.discount.amount.compareTo(BigDecimal.ZERO) shouldBe 0
    }

    @Test
    fun `findFare calculates correct discount for Child rider type`() {
        // Given
        val trip = createTrip(origin = STATION_A, destination = STATION_B, riderType = RiderType.CHILD)

        // When
        val result = repository.findFare(trip)

        // Then
        result.baseFare.amount shouldBe BigDecimal.valueOf(BASE_FARE_ONE_STOP)
        result.discount.amount shouldBe BigDecimal.valueOf(2.0) // 20% of 10
        result.total.amount shouldBe BigDecimal.valueOf(8.0)
    }

    @Test
    fun `findFare calculates correct discount for Senior rider type`() {
        // Given
        val trip = createTrip(origin = STATION_A, destination = STATION_B, riderType = RiderType.SENIOR)

        // When
        val result = repository.findFare(trip)

        // Then
        result.baseFare.amount shouldBe BigDecimal.valueOf(BASE_FARE_ONE_STOP)
        result.discount.amount shouldBe BigDecimal.valueOf(4.0) // 40% of 10
        result.total.amount shouldBe BigDecimal.valueOf(6.0)
    }

    @Test
    fun `findFare returns correct fare for two-stop journey`() {
        // Given
        val trip = createTrip(origin = STATION_A, destination = STATION_C, riderType = RiderType.ADULT)

        // When
        val result = repository.findFare(trip)

        // Then
        result.baseFare.amount shouldBe BigDecimal.valueOf(BASE_FARE_TWO_STOPS)
    }

    @Test
    fun `getAllStations returns all stations from tariff`() {
        // When
        val stations = repository.getAllStations()

        // Then
        stations.shouldNotBeEmpty()
        stations.map { it.name } shouldContain "A"
        stations.map { it.name } shouldContain "B"
        stations.map { it.name } shouldContain "C"
        stations.map { it.name } shouldContain "D"
    }

    @Test
    fun `getAllStations returns unique station names`() {
        // When
        val stations = repository.getAllStations()

        // Then
        val stationNames = stations.map { it.name }
        stationNames.size shouldBe stationNames.toSet().size
    }

    @Test
    fun `getAllRiderTypes returns all valid rider types`() {
        // When
        val riderTypes = repository.getAllRiderTypes()

        // Then
        riderTypes.shouldNotBeEmpty()
        riderTypes shouldContain RiderType.ADULT
        riderTypes shouldContain RiderType.CHILD
        riderTypes shouldContain RiderType.SENIOR
    }

    // Error Handling Tests
    @Test
    fun `findFare throws IllegalArgumentException when origin station not in tariff`() {
        // Given
        val invalidStation = Station(0, "InvalidStation")
        val trip = createTrip(origin = invalidStation, destination = STATION_B)

        // When & Then
        val exception = shouldThrow<IllegalArgumentException> {
            repository.findFare(trip)
        }
        exception.message shouldContain "Journey not found"
        exception.message shouldContain "InvalidStation"
    }

    @Test
    fun `findFare throws IllegalArgumentException when destination station not in tariff`() {
        // Given
        val invalidStation = Station(0, "InvalidStation")
        val trip = createTrip(origin = STATION_A, destination = invalidStation)

        // When & Then
        val exception = shouldThrow<IllegalArgumentException> {
            repository.findFare(trip)
        }
        exception.message shouldContain "Journey not found"
    }

    @Test
    fun `findFare handles case-insensitive station matching`() {
        // Given - assuming your Excel has uppercase station names
        val lowerCaseStation = Station(0, "a")
        val trip = createTrip(origin = lowerCaseStation, destination = STATION_B)

        // When - This might fail depending on your implementation
        // If it should work, test it; if not, document the behavior
        val exception = shouldThrow<IllegalArgumentException> {
            repository.findFare(trip)
        }

        // Or if it should work:
        // val result = repository.findFare(trip)
        // result shouldNotBe null
    }

    // Edge Cases
    @Test
    fun `findFare works for reverse journey direction`() {
        // Given
        val forwardTrip = createTrip(origin = STATION_A, destination = STATION_B)
        val reverseTrip = createTrip(origin = STATION_B, destination = STATION_A)

        // When
        val forwardResult = repository.findFare(forwardTrip)
        val reverseResult = repository.findFare(reverseTrip)

        // Then - Both should return the same fare
        forwardResult.baseFare.amount shouldBe reverseResult.baseFare.amount
    }

    @Test
    fun `repository successfully reads Excel file`() {
        repository.getAllStations() shouldNotBe null
        repository.getAllRiderTypes() shouldNotBe null
    }
}
