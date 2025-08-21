package org.example.domain.dtos

import java.time.LocalDateTime

data class FareRequest ( val origin : String, val destination: String, val timeStamp: LocalDateTime = LocalDateTime.now(), val riderType: String)
