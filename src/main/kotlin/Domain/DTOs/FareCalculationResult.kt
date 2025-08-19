package org.example.Domain.DTOs

import java.math.BigDecimal

data class FareCalculationResult(val baseFare: BigDecimal , val discount: BigDecimal, val total: BigDecimal = baseFare - discount, val currency: String = "USD")