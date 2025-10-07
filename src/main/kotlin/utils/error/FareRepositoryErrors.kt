package org.example.utils.error

sealed class FareRepositoryErrors(message: String): Exception(message) {
    class FareNotFound: FareRepositoryErrors("Fare couldn't be found")
    class InvalidStationError(stationName: String): FareRepositoryErrors("Invalid station provided: $stationName")
    class RiderTypeNotFoundError(riderType: String): FareRepositoryErrors("Rider type not found: $riderType")
}

