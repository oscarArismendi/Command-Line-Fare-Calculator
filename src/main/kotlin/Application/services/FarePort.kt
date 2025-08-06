package org.example.Application.services

import org.example.Domain.Models.FareRequest

interface FarePort {
    fun calculateFare(fareRequest: FareRequest): Double // Need to handle different currencies
}