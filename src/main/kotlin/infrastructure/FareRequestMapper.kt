package org.fare.calculator.infrastructure

import org.fare.calculator.domain.dtos.FareRequest
import org.fare.calculator.domain.models.RiderType
import org.fare.calculator.domain.models.Station
import org.fare.calculator.domain.models.Trip

fun FareRequest.toTrip(): Trip {
    return Trip(
        origin = Station(0, this.origin),
        destination = Station(0, this.destination),
        timeStamp = this.timeStamp,
        riderType = RiderType.valueOf(this.riderType.uppercase()),
    )
}
