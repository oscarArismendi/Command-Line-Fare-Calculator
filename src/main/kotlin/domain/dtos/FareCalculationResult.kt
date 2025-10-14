package org.example.domain.dtos

import org.example.domain.models.Fare

data class FareCalculationResult(
    val baseFare: Fare,
    val discount: Fare,
    val total: Fare = baseFare - discount,
)
