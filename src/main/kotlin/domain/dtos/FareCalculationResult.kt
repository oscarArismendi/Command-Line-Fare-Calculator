package org.fare.calculator.domain.dtos

import org.fare.calculator.domain.models.Fare

data class FareCalculationResult(
    val baseFare: Fare,
    val discount: Fare,
    val total: Fare = baseFare - discount,
)
