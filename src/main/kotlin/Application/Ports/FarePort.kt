package org.example.Application.Ports

import org.example.Domain.DTOs.FareCalculationResult
import org.example.Domain.DTOs.FareRequest

interface FarePort {
    fun calculateFare(fareRequest: FareRequest): FareCalculationResult // Need to handle different currencies
}