package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer_profile")
data class Customer(
    @PrimaryKey val id: Int = 1,
    val customerNumber: String, // Unique specific customer ID (e.g., "DO-78214")
    val fullName: String,
    val phoneNumber: String, // e.g. "0301-2345678"
    val deliveryAddress: String, // Mandatory complete address
    val areaOrLandmark: String = "",
    val city: String = "Dadyal, Azad Kashmir",
    val isRegistered: Boolean = true,
    val registeredAt: Long = System.currentTimeMillis()
) {
    val fullFormattedAddress: String
        get() = if (areaOrLandmark.isNotBlank()) {
            "$deliveryAddress, $areaOrLandmark, $city"
        } else {
            "$deliveryAddress, $city"
        }
}
