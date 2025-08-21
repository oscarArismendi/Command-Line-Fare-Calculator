package org.example.utils

import org.example.domain.dtos.FareRequest


fun parseInput(args: Array<String>): FareRequest {
    try {
        val fareRequest= FareRequest(args[0], args[1], riderType = args[2])
        return fareRequest
    } catch(e : Exception){
        throw IllegalArgumentException("Invalid input format. Expected: origin destination riderType /n error message: ${e.message}")
    }
}