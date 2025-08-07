package org.example.Domain.Models

import java.time.LocalDateTime

class Trip(val origin: Station, val destination: Station, val timeStamp: LocalDateTime, val riderType: RiderType) {
}