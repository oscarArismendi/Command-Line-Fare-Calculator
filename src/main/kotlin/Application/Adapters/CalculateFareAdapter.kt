package org.example.Application.Adapters

import org.example.Application.Ports.CalculateFarePort
import org.example.Application.Ports.out.FareTariffPort
import org.example.Domain.DTOs.FareCalculationResult
import org.example.Domain.DTOs.FareRequest
import org.example.Domain.Models.RiderType
import org.example.Domain.Models.Station
import org.example.Infrastructure.repositories.ExcelFareTariffRepository
import org.example.Infrastructure.toTrip
import java.math.BigDecimal

class CalculateFareAdapter: CalculateFarePort {
    override fun calculateFare(fareRequest: FareRequest, fareTariffRepository: FareTariffPort): FareCalculationResult {


        return fareTariffRepository.findFare(fareRequest)
    }

}