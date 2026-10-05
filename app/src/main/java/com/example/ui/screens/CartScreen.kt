package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItemWithProduct
import com.example.data.model.LoyaltyProfile
import com.example.ui.components.QuantityStepper
import com.example.ui.components.SecurityTrustBadge
import com.example.ui.theme.AmberLoyalty
import com.example.ui.theme.AmberLoyaltyContainer
import com.example.ui.theme.AmberLoyaltyOnContainer
import com.example.ui.theme.BadgeRed
import com.example.ui.theme.FreshGreenContainer
import com.example.ui.theme.FreshGreenLight
import com.example.ui.theme.FreshGreenOnContainer
import com.example.ui.theme.FreshGreenPrimary
import com.example.ui.theme.SuccessGreen

@Composable
fun CartScreen(
    cartItems: List<CartItemWithProduct>,
    cartSubtotal: Double,
    loyaltyProfile: LoyaltyProfile?,
    pointsToRedeem: Int,
    pointsDiscount: Double,
    appliedCoupon: String?,
    couponDiscount: Double,
    deliveryFee: Double,
    deliveryTip: Double,
    estimatedTax: Double,
    grandTotal: Double,
    projectedPoints: Int,
    deliveryAddress: String,
    deliverySlot: String,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onClearCart: () -> Unit,
    onSetPointsToRedeem: (Int) -> Unit,
    onClearPoints: () -> Unit,
    onApplyCoupon: (String) -> Boolean,
    onRemoveCoupon: () -> Unit,
    onSetDeliveryTip: (Double) -> Unit,
    onSetDeliverySlot: (String) -> Unit,
    onProceedToCheckout: () -> Unit,
    onNavigateToShop: () -> Unit,
    modifier: Modifier = Modifier
) {
    var couponInput by remember { mutableStateOf("") }
    val profile = loyaltyProfile ?: LoyaltyProfile()

    if (cartItems.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.testTag("empty_cart_view")
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(FreshGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🛒", fontSize = 48.sp)
                }
                Text(
                    text = "Your FreshCart is Empty",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Explore organic fresh vegetables, pasture eggs, artisan bakery, and farm dairy.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(horizontal = 24.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Button(
                    onClick = onNavigateToShop,
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("start_shopping_btn")
                ) {
                    Text("Start Shopping Fresh Groceries", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("cart_items_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Cart Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Cart (${cartItems.sumOf { it.quantity }} items)",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                TextButton(
                    onClick = onClearCart,
                    modifier = Modifier.testTag("clear_cart_btn")
                ) {
                    Text(
                        text = "Clear All",
                        style = MaterialTheme.typography.labelMedium.copy(color = BadgeRed)
                    )
                }
            }
        }

        // 2. Items List
        items(cartItems, key = { it.product.id }) { itemWithProd ->
            CartItemRow(
                item = itemWithProd,
                onUpdateQuantity = { qty -> onUpdateQuantity(itemWithProd.product.id, qty) },
                onRemove = { onRemoveItem(itemWithProd.product.id) }
            )
        }

        // 3. Loyalty Points Redemption Card
        item {
            LoyaltyRedemptionCard(
                availablePoints = profile.pointsBalance,
                currentPointsToRedeem = pointsToRedeem,
                pointsDiscount = pointsDiscount,
                cartSubtotal = cartSubtotal,
                onSelectPoints = onSetPointsToRedeem,
                onClearPoints = onClearPoints
            )
        }

        // 4. Promo Code / Coupons
        item {
            PromoCodeCard(
                appliedCoupon = appliedCoupon,
                couponDiscount = couponDiscount,
                couponInput = couponInput,
                onCouponInputChange = { couponInput = it },
                onApply = { onApplyCoupon(couponInput) },
                onRemove = onRemoveCoupon
            )
        }

        // 5. Delivery Address & Slot
        item {
            DeliveryDetailsCard(
                deliveryAddress = deliveryAddress,
                selectedSlot = deliverySlot,
                onSelectSlot = onSetDeliverySlot
            )
        }

        // 6. Driver Tip
        item {
            DriverTipCard(
                selectedTip = deliveryTip,
                onTipSelect = onSetDeliveryTip
            )
        }

        // 7. Order Summary Bill
        item {
            OrderBillSummaryCard(
                subtotal = cartSubtotal,
                pointsDiscount = pointsDiscount,
                pointsRedeemed = pointsToRedeem,
                couponDiscount = couponDiscount,
                appliedCoupon = appliedCoupon,
                deliveryFee = deliveryFee,
                tip = deliveryTip,
                tax = estimatedTax,
                grandTotal = grandTotal,
                projectedPoints = projectedPoints
            )
        }

        // 8. Checkout Button
        item {
            Button(
                onClick = onProceedToCheckout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = FreshGreenPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("proceed_to_checkout_btn")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Proceed to Payment",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        text = "Rs. ${String.format("%.2f", grandTotal)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            SecurityTrustBadge()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItemWithProduct,
    onUpdateQuantity: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("cart_item_${item.product.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji Visual Box
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FreshGreenContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.product.iconEmoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = item.product.unit,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = "Rs. ${String.format("%.2f", item.product.price)} each",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = FreshGreenPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            // Quantity Stepper
            QuantityStepper(
                quantity = item.quantity,
                onIncrease = { onUpdateQuantity(item.quantity + 1) },
                onDecrease = {
                    if (item.quantity > 1) {
                        onUpdateQuantity(item.quantity - 1)
                    } else {
                        onRemove()
                    }
                }
            )

            // Total & Delete
            IconButton(
                onClick = onRemove,
                modifier = Modifier.testTag("remove_item_${item.product.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Remove item",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun LoyaltyRedemptionCard(
    availablePoints: Int,
    currentPointsToRedeem: Int,
    pointsDiscount: Double,
    cartSubtotal: Double,
    onSelectPoints: (Int) -> Unit,
    onClearPoints: () -> Unit,
    modifier: Modifier = Modifier
) {
    val maxRedeemablePoints = minOf(availablePoints, (cartSubtotal * 100).toInt())

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = AmberLoyaltyContainer.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, AmberLoyalty.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("loyalty_redemption_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AmberLoyalty),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Loyalty,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "FreshRewards Loyalty Savings",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AmberLoyaltyOnContainer
                            )
                        )
                        Text(
                            text = "$availablePoints points available (Rs. $availablePoints value)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = AmberLoyaltyOnContainer.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                if (currentPointsToRedeem > 0) {
                    TextButton(onClick = onClearPoints) {
                        Text("Remove", color = BadgeRed, fontSize = 12.sp)
                    }
                }
            }

            if (availablePoints < 100) {
                Text(
                    text = "You need at least 100 points (Rs. 100) to redeem on an order. Earn 10-20 pts per Rs. 100 spent!",
                    style = MaterialTheme.typography.bodySmall.copy(color = AmberLoyaltyOnContainer)
                )
            } else {
                Text(
                    text = "Quick Redeem Preset:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = AmberLoyaltyOnContainer
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf(
                        100 to "Rs. 100 OFF",
                        250 to "Rs. 250 OFF",
                        400 to "Rs. 400 OFF"
                    )

                    presets.forEach { (pts, label) ->
                        val isSelected = currentPointsToRedeem == pts
                        val isAllowed = maxRedeemablePoints >= pts
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) AmberLoyalty else if (isAllowed) Color.White else Color.White.copy(alpha = 0.4f),
                            border = if (!isSelected && isAllowed) androidx.compose.foundation.BorderStroke(1.dp, AmberLoyalty.copy(alpha = 0.5f)) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = isAllowed) { onSelectPoints(pts) }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else if (isAllowed) AmberLoyaltyOnContainer else Color.Gray,
                                        fontSize = 11.sp
                                    )
                                )
                                Text(
                                    text = "$pts pts",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color.Gray,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    // Max available button
                    val isMaxSelected = currentPointsToRedeem == maxRedeemablePoints && maxRedeemablePoints > 0
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isMaxSelected) AmberLoyalty else Color.White,
                        border = if (!isMaxSelected) androidx.compose.foundation.BorderStroke(1.dp, AmberLoyalty.copy(alpha = 0.5f)) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectPoints(maxRedeemablePoints) }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Max",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMaxSelected) Color.White else AmberLoyaltyOnContainer,
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = "$maxRedeemablePoints pts",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isMaxSelected) Color.White.copy(alpha = 0.9f) else Color.Gray,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                if (currentPointsToRedeem > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FreshGreenContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = FreshGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Applying $currentPointsToRedeem points for -Rs. ${String.format("%.2f", pointsDiscount)} discount!",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FreshGreenOnContainer
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PromoCodeCard(
    appliedCoupon: String?,
    couponDiscount: Double,
    couponInput: String,
    onCouponInputChange: (String) -> Unit,
    onApply: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalOffer,
                    contentDescription = null,
                    tint = FreshGreenPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Voucher & Promo Code",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (appliedCoupon != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = FreshGreenContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Coupon '$appliedCoupon' Applied",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FreshGreenOnContainer
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(-Rs. ${String.format("%.2f", couponDiscount)})",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        }
                        IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove coupon", tint = BadgeRed)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = couponInput,
                        onValueChange = onCouponInputChange,
                        placeholder = { Text("Enter FRESH50, GREEN10...") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).testTag("coupon_input_field")
                    )
                    Button(
                        onClick = onApply,
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("apply_coupon_btn")
                    ) {
                        Text("Apply", fontWeight = FontWeight.Bold)
                    }
                }

                // Quick tags
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { onCouponInputChange("FRESH50") }
                    ) {
                        Text(
                            text = "FRESH50 (Rs. 250 OFF Rs. 1000+)",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { onCouponInputChange("GREEN10") }
                    ) {
                        Text(
                            text = "GREEN10 (10% OFF)",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveryDetailsCard(
    deliveryAddress: String,
    selectedSlot: String,
    onSelectSlot: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ElectricBike,
                    contentDescription = null,
                    tint = FreshGreenPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Delivery Speed & Destination",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            // Address display
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = FreshGreenPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "Deliver to:", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                        Text(text = deliveryAddress, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                    }
                }
            }

            // Slot options
            val slots = listOf(
                "Express Delivery (15-25 min)",
                "Standard Delivery (Today 4-6 PM)",
                "Eco-Green Slot (Tomorrow Morning)"
            )

            slots.forEach { slot ->
                val isSelected = slot == selectedSlot
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) FreshGreenContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, FreshGreenPrimary) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectSlot(slot) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = if (isSelected) FreshGreenPrimary else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = slot,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) FreshGreenOnContainer else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DriverTipCard(
    selectedTip: Double,
    onTipSelect: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tip Your Electric Delivery Rider",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "100% of driver tips go directly to Alex on eco-cargo bike #14.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            )

            val tipOptions = listOf(50.0, 100.0, 200.0, 300.0)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tipOptions.forEach { tipVal ->
                    val isSelected = selectedTip == tipVal
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) FreshGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTipSelect(tipVal) }
                    ) {
                        Text(
                            text = "Rs. ${tipVal.toInt()}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderBillSummaryCard(
    subtotal: Double,
    pointsDiscount: Double,
    pointsRedeemed: Int,
    couponDiscount: Double,
    appliedCoupon: String?,
    deliveryFee: Double,
    tip: Double,
    tax: Double,
    grandTotal: Double,
    projectedPoints: Int,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Payment Breakdown",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            BillRow(label = "Items Subtotal", amount = subtotal)

            if (pointsDiscount > 0) {
                BillRow(
                    label = "FreshRewards ($pointsRedeemed pts)",
                    amount = -pointsDiscount,
                    highlightColor = SuccessGreen
                )
            }

            if (couponDiscount > 0) {
                BillRow(
                    label = "Coupon ($appliedCoupon)",
                    amount = -couponDiscount,
                    highlightColor = SuccessGreen
                )
            }

            BillRow(
                label = "Express Delivery Fee",
                amount = deliveryFee,
                freeBadge = deliveryFee == 0.0
            )

            BillRow(label = "Courier Tip", amount = tip)
            BillRow(label = "Estimated Sales Tax", amount = tax)

            HorizontalDivider(
                color = DividerDefaults.color.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Amount",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Rs. ${String.format("%.2f", grandTotal)}",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = FreshGreenPrimary
                    )
                )
            }

            // Projected Points Callout
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AmberLoyaltyContainer,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = AmberLoyalty,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "You will earn +$projectedPoints FreshRewards points on this order!",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AmberLoyaltyOnContainer
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun BillRow(
    label: String,
    amount: Double,
    highlightColor: Color? = null,
    freeBadge: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        if (freeBadge) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = FreshGreenContainer
            ) {
                Text(
                    text = "FREE (Rs. 1500+)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = FreshGreenOnContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        } else {
            Text(
                text = "${if (amount < 0) "-Rs. " else "Rs. "}${String.format("%.2f", Math.abs(amount))}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = highlightColor ?: MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}
