package org.fare.calculator.domain.models

import java.time.LocalTime

class Trip(val origin: Station, val destination: Station, val timeStamp: LocalTime, val riderType: RiderType)
