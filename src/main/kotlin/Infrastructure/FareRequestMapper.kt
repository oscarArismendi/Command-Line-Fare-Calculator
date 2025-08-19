package org.example.Infrastructure

import org.example.Domain.DTOs.FareRequest
import org.example.Domain.Models.RiderType
import org.example.Domain.Models.Station
import org.example.Domain.Models.Trip

fun FareRequest.toTrip(): Trip{
    return Trip(
        origin = Station(0, this.origin),
        destination = Station(0, this.destination),
        timeStamp = this.timeStamp,
        riderType = RiderType.valueOf(this.riderType.uppercase())
    )
}