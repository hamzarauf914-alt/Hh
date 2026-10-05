package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loyalty_profile")
data class LoyaltyProfile(
    @PrimaryKey val id: Int = 1,
    val pointsBalance: Int = 450, // Initial welcome balance
    val tier: LoyaltyTier = LoyaltyTier.SILVER,
    val lifetimePointsEarned: Int = 750,
    val totalSavedWithPoints: Double = 3.00,
    val memberSince: String = "Member since Aug 2026"
) {
    // 100 points = $1.00 discount
    val pointsCashValue: Double
        get() = pointsBalance / 100.0

    val nextTierPointsNeeded: Int
        get() = when (tier) {
            LoyaltyTier.BRONZE -> 500 - lifetimePointsEarned
            LoyaltyTier.SILVER -> 1500 - lifetimePointsEarned
            LoyaltyTier.GOLD -> 3000 - lifetimePointsEarned
            LoyaltyTier.PLATINUM -> 0
        }.coerceAtLeast(0)

    val tierProgress: Float
        get() = when (tier) {
            LoyaltyTier.BRONZE -> (lifetimePointsEarned / 500f).coerceIn(0f, 1f)
            LoyaltyTier.SILVER -> ((lifetimePointsEarned - 500) / 1000f).coerceIn(0f, 1f)
            LoyaltyTier.GOLD -> ((lifetimePointsEarned - 1500) / 1500f).coerceIn(0f, 1f)
            LoyaltyTier.PLATINUM -> 1f
        }
}

enum class LoyaltyTier(
    val title: String,
    val multiplier: Double,
    val badgeColorHex: Long,
    val perkDescription: String
) {
    BRONZE("Bronze", 1.0, 0xFFCD7F32, "Earn 10 pts per $1 spent"),
    SILVER("Silver VIP", 1.25, 0xFF94A3B8, "Earn 12.5 pts per $1 + exclusive weekly coupons"),
    GOLD("Gold VIP", 1.5, 0xFFF59E0B, "Earn 15 pts per $1 + priority delivery dispatch"),
    PLATINUM("Platinum Elite", 2.0, 0xFF6366F1, "Earn 20 pts per $1 + free express delivery on all orders")
}

@Entity(tableName = "loyalty_transactions")
data class LoyaltyTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val points: Int, // positive for earn, negative for redeem
    val type: String, // "EARNED", "REDEEMED", "BONUS"
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val orderId: String? = null
)
