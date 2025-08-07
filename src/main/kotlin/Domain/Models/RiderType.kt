package org.example.Domain.Models

class RiderType(val id: Int, val type: Type) {
}

enum class  Type(val value: String){
    ADULT("Adult"),
    CHILD("Child"),
    SENIOR("Senior")
}