package org.fare.calculator.domain.models

import java.math.BigDecimal

class Fare(val amount: BigDecimal, val currency: String = "USD") {
    operator fun plus(other: Fare): Fare {
        require(currency == other.currency) { "Cannot add fares with different currencies" }
        return Fare(amount.add(other.amount), currency)
    }

    operator fun minus(other: Fare): Fare {
        require(currency == other.currency) { "Cannot subtract fares with different currencies" }
        return Fare(amount.subtract(other.amount), currency)
    }
}
