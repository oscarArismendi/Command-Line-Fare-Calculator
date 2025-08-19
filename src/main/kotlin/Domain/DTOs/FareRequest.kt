package org.example.Domain.DTOs

import java.time.LocalDateTime

data class FareRequest ( val origin : String, val destination: String, val timeStamp: LocalDateTime = LocalDateTime.now(), val riderType: String)



// TODO 2: Create a 'Repositories' folder inside infrastructure to handle data persistence