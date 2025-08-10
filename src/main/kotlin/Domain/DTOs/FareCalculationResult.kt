package org.example.Domain.DTOs

data class FareCalculationResult(val baseFare: Double, val discount: Double, val total: Double = baseFare - discount, val currency: String = "USD")