package org.fare.calculator.application.ports.out

import org.fare.calculator.domain.dtos.FareCalculationResult
import org.fare.calculator.domain.dtos.FareRequest
import org.fare.calculator.domain.models.RiderType
import org.fare.calculator.domain.models.Station

interface FareTariffPort {
    fun findFare(fareRequest: FareRequest): FareCalculationResult
    fun getAllStations(): Set<Station>
    fun getAllRiderTypes(): Set<RiderType>
}
