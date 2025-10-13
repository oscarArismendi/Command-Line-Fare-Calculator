package org.example.application.ports.out

import org.example.domain.dtos.FareCalculationResult
import org.example.domain.dtos.FareRequest
import org.example.domain.models.RiderType
import org.example.domain.models.Station

interface FareTariffPort {
    fun findFare(fareRequest: FareRequest): FareCalculationResult
    fun getAllStations(): Set<Station>
    fun getAllRiderTypes(): Set<RiderType>
}
