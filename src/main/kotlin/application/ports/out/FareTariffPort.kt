package org.fare.calculator.application.ports.out

import org.fare.calculator.domain.dtos.FareCalculationResult
import org.fare.calculator.domain.models.RiderType
import org.fare.calculator.domain.models.Station
import org.fare.calculator.domain.models.Trip

interface FareTariffPort {
    fun findFare(trip: Trip): FareCalculationResult
    fun getAllStations(): Set<Station>
    fun getAllRiderTypes(): Set<RiderType>
}
