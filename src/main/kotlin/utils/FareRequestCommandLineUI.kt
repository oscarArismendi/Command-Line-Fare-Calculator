package org.example.utils

import org.example.domain.dtos.FareCalculationResult

class FareRequestCommandLineUI(val fareCalculationResult: FareCalculationResult) {
     fun printFare() {
        println("Your fare is: $${fareCalculationResult.total} ${fareCalculationResult.currency}")
        println("Breakdown:")
        println("    - Base Fare: $${fareCalculationResult.baseFare} ${fareCalculationResult.currency}")
        println("    - Discount: $${fareCalculationResult.discount} ${fareCalculationResult.currency}")
    }
}