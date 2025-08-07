package org.example.Domain.DTOs

import org.example.Domain.Models.RiderType
import org.example.Domain.Models.Station
import org.example.Domain.Models.Trip
import org.example.Domain.Models.Type
import java.time.LocalDateTime

data class FareRequest ( val origin : String, val destination: String, val timeStamp: LocalDateTime = LocalDateTime.now(), val riderType: String)

fun FareRequest.toTrp(): Trip{
    return Trip(
        origin = Station(0, this.origin),
        destination = Station(0, this.destination),
        timeStamp = this.timeStamp,
        riderType = RiderType(0, Type.valueOf(this.riderType.uppercase()))
    )
}

// TODO 1: Decide if toTrip should be moved to the FareHandler class
// TODO 2: Create a 'Repositories' folder inside infrastructure to handle data persistence