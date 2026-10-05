package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OrderStatus(val display: String, val stepIndex: Int, val description: String) {
    ORDERED("Ordered", 1, "Your order has been received, verified, and sent to FreshMart store."),
    PREPARING("Preparing", 2, "Items are being hand-selected, inspected, and packed in temperature-controlled cooler bags."),
    OUT_FOR_DELIVERY("Out for Delivery", 3, "Courier Alex Rivera is en route on an Electric Eco-Cargo Bike."),
    DELIVERED("Delivered", 4, "Delivered safely at your doorstep. Enjoy your fresh groceries!")
}

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey val id: String, // e.g. "FC-8902"
    val timestamp: Long = System.currentTimeMillis(),
    val status: OrderStatus = OrderStatus.ORDERED,
    val subtotal: Double,
    val discount: Double = 0.0,
    val pointsRedeemed: Int = 0,
    val pointsDiscount: Double = 0.0,
    val pointsEarned: Int = 0,
    val deliveryFee: Double = 0.0,
    val tip: Double = 2.0,
    val tax: Double,
    val total: Double,
    val paymentMethod: String, // e.g. "Google Pay", "Visa •••• 4242", "Cash on Delivery"
    val paymentTxnId: String,
    val deliveryAddress: String = "742 Evergreen Terrace, Apt 4B",
    val deliverySlot: String = "Express Delivery (15-25 min)",
    val deliveryInstructions: String = "Leave at front porch, ring bell",
    val driverName: String = "Alex Rivera",
    val driverPhone: String = "(555) 234-8901",
    val driverVehicle: String = "Electric Eco-Cargo Bike #14",
    val driverRating: Float = 4.95f,
    val etaMinutes: Int = 18,
    val trackingProgress: Float = 0.15f, // 0.0 to 1.0 along the route
    val itemsSummary: String, // Comma separated items summary or count
    val itemsJson: String = "" // Full item details serialized
)

data class OrderItemRecord(
    val productId: String,
    val productName: String,
    val productEmoji: String,
    val unitPrice: Double,
    val quantity: Int,
    val unit: String
)
