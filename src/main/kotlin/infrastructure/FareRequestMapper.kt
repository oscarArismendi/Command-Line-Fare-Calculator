package org.example.infrastructure

import org.example.domain.dtos.FareRequest
import org.example.domain.models.RiderType
import org.example.domain.models.Station
import org.example.domain.models.Trip

fun FareRequest.toTrip(): Trip {
    return Trip(
        origin = Station(0, this.origin),
        destination = Station(0, this.destination),
        timeStamp = this.timeStamp,
        riderType = RiderType.valueOf(this.riderType.uppercase()),
    )
}
// TODO: Implement to trip method
