package fakes

import org.fare.calculator.application.ports.out.FareTariffPort
import org.fare.calculator.domain.dtos.FareCalculationResult
import org.fare.calculator.domain.models.Fare
import org.fare.calculator.domain.models.RiderType
import org.fare.calculator.domain.models.Station
import org.fare.calculator.domain.models.Trip
import java.math.BigDecimal

class FakeFareTariffRepository: FareTariffPort {
    val currency = "USD"
    private val baseFares = mutableMapOf<Pair<String, String>, BigDecimal>()
    init {
        // Pre-populate with test data
        baseFares[Pair("A", "B")] = BigDecimal.valueOf(10.0)
        baseFares[Pair("A", "C")] = BigDecimal.valueOf(20.0)
        baseFares[Pair("A", "D")] = BigDecimal.valueOf(30.0)
        baseFares[Pair("B", "C")] = BigDecimal.valueOf(10.0)
        baseFares[Pair("B", "D")] = BigDecimal.valueOf(20.0)
        baseFares[Pair("C", "D")] = BigDecimal.valueOf(10.0)
    }

    override fun findFare(trip: Trip): FareCalculationResult {
        val baseFareAmount = baseFares[Pair(trip.origin.name, trip.destination.name)]?: throw IllegalArgumentException("Route not found: ${trip.origin.name} -> ${trip.destination.name}")
        if(baseFareAmount == BigDecimal(-1)) throw IllegalArgumentException("Invalid journey")
        val discount = getDiscountForRiderType(trip.riderType)

        val baseFare = Fare(baseFareAmount , currency)
        return FareCalculationResult(baseFare , discount)
    }

    private fun getDiscountForRiderType(riderType: RiderType): Fare = when (riderType) {
        RiderType.CHILD -> Fare(BigDecimal(2), currency)
        RiderType.SENIOR -> Fare(BigDecimal(4), currency)
        RiderType.ADULT -> Fare(BigDecimal(0), currency)
    }

    override fun getAllStations(): Set<Station> {
        return setOf(
            Station(0,"A"),
            Station(1,"B"),
            Station(2,"C"),
            Station(3,"D")
        )
    }

    override fun getAllRiderTypes(): Set<RiderType> {
        return RiderType.entries.toSet()
    }

    override fun findCurrency(): String = currency

}
