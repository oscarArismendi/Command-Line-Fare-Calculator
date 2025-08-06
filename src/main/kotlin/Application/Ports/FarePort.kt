package org.example.Application.Ports

import org.example.Domain.DTOs.FareRequest

interface FarePort {
    fun calculateFare(fareRequest: FareRequest): Double // Need to handle different currencies
}