package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val originalPrice: Double? = null,
    val unit: String,
    val description: String,
    val rating: Float = 4.8f,
    val reviewCount: Int = 120,
    val badge: String? = null,
    val inStock: Boolean = true,
    val origin: String = "Local Organic Partner",
    val dietaryTag: String? = null, // e.g., "Organic", "Gluten Free", "Vegan", "Farm Fresh"
    val iconEmoji: String = "🥬"
) {
    val discountPercent: Int?
        get() = if (originalPrice != null && originalPrice > price) {
            (((originalPrice - price) / originalPrice) * 100).toInt()
        } else null
}
