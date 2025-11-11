package org.fare.calculator.domain.dtos

import java.time.LocalTime

data class FareRequest(val origin: String, val destination: String, val timeStamp: LocalTime = LocalTime.now(), val riderType: String)
