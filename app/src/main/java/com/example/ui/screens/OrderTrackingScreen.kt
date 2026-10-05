package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.theme.AmberLoyalty
import com.example.ui.theme.AmberLoyaltyContainer
import com.example.ui.theme.AmberLoyaltyLight
import com.example.ui.theme.AmberLoyaltyOnContainer
import com.example.ui.theme.BadgeRed
import com.example.ui.theme.FreshGreenContainer
import com.example.ui.theme.FreshGreenDark
import com.example.ui.theme.FreshGreenLight
import com.example.ui.theme.FreshGreenOnContainer
import com.example.ui.theme.FreshGreenPrimary
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrderTrackingScreen(
    currentOrder: Order?,
    allOrders: List<Order>,
    onSelectOrder: (Order) -> Unit,
    onReorder: (Order) -> Unit,
    onNavigateToShop: () -> Unit,
    onAdvanceStatus: (String) -> Unit = {},
    onSetStatus: (String, OrderStatus) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showDriverChat by remember { mutableStateOf(false) }

    if (showDriverChat && currentOrder != null) {
        DriverContactDialog(
            driverName = currentOrder.driverName,
            driverPhone = currentOrder.driverPhone,
            onDismiss = { showDriverChat = false }
        )
    }

    if (allOrders.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.testTag("empty_orders_view")
            ) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                        .background(FreshGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📦", fontSize = 42.sp)
                }
                Text(
                    text = "No Active Orders",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "When you place an order, you can monitor the real-time status with visual markers for Ordered, Preparing, Out for Delivery, and Delivered.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = onNavigateToShop,
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Shop Groceries Now", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    val order = currentOrder ?: allOrders.first()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("active_order_tracking_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Tab Selector: Live Tracking vs Past Orders
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = FreshGreenPrimary,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("order_tracking_tab_row")
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Active Order Status",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Order History (${allOrders.size})",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        if (selectedTab == 0) {
            // 1. Order Status Header Card with Live ETA
            item {
                ActiveOrderStatusHeroCard(order = order)
            }

            // 2. PRIMARY REQUIREMENT: Visual Progress Markers for 'Ordered', 'Preparing', 'Out for Delivery', and 'Delivered'
            item {
                VisualOrderProgressStepper(
                    currentStatus = order.status,
                    onMarkerClick = { status -> onSetStatus(order.id, status) },
                    onAdvance = { onAdvanceStatus(order.id) }
                )
            }

            // 3. Live Real-Time Route Map Canvas
            item {
                LiveDeliveryMapCard(order = order)
            }

            // 4. Real-Time Telemetry Indicators (Cold-chain, Eco-courier, ETA)
            item {
                DeliveryTelemetryRow(order = order)
            }

            // 5. Detailed Timeline Cards for each of the 4 Stages
            item {
                DetailedFourStageMilestonesCard(order = order)
            }

            // 6. Courier Contact Card (Call & Message)
            item {
                DriverProfileCard(
                    order = order,
                    onOpenChat = { showDriverChat = true }
                )
            }

            // 7. Order Items Receipt & Loyalty Points Earned
            item {
                OrderReceiptSummaryCard(order = order)
            }
        } else {
            // All Orders List
            items(allOrders, key = { it.id }) { pastOrder ->
                PastOrderItemCard(
                    order = pastOrder,
                    isCurrentlyTracking = currentOrder?.id == pastOrder.id,
                    onSelectTracking = {
                        onSelectOrder(pastOrder)
                        selectedTab = 0
                    },
                    onReorder = { onReorder(pastOrder) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * VISUAL PROGRESS MARKERS COMPONENT
 * Explicitly displays the visual markers for:
 * 1. 'Ordered'
 * 2. 'Preparing'
 * 3. 'Out for Delivery'
 * 4. 'Delivered'
 */
@Composable
fun VisualOrderProgressStepper(
    currentStatus: OrderStatus,
    onMarkerClick: (OrderStatus) -> Unit,
    onAdvance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        ProgressMarkerData(OrderStatus.ORDERED, "Ordered", "Step 1", Icons.Default.Receipt, "12:30 PM"),
        ProgressMarkerData(OrderStatus.PREPARING, "Preparing", "Step 2", Icons.Default.Inventory2, "12:34 PM"),
        ProgressMarkerData(OrderStatus.OUT_FOR_DELIVERY, "Out for Delivery", "Step 3", Icons.Default.ElectricBike, "12:42 PM"),
        ProgressMarkerData(OrderStatus.DELIVERED, "Delivered", "Step 4", Icons.Default.Home, "Est. 12:55 PM")
    )

    val currentStepIndex = currentStatus.stepIndex

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("visual_order_progress_stepper")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with simulation button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Live Order Progress",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Real-time updates as your groceries move from store to door",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }

                // Interactive Simulator Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = FreshGreenContainer,
                    modifier = Modifier
                        .clickable(onClick = onAdvance)
                        .testTag("advance_order_status_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = FreshGreenPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Next Stage",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = FreshGreenOnContainer,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Horizontal Stepper Bar with connecting lines
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
            ) {
                // Background Track Line
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = 20.dp, start = 24.dp, end = 24.dp)
                ) {
                    steps.take(3).forEachIndexed { index, _ ->
                        val isSegmentCompleted = currentStepIndex > (index + 1)
                        val isSegmentActive = currentStepIndex == (index + 1)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (isSegmentCompleted) FreshGreenPrimary
                                    else if (isSegmentActive) FreshGreenLight
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                        )
                    }
                }

                // Marker Nodes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    steps.forEach { stepData ->
                        val isCompleted = currentStepIndex > stepData.status.stepIndex
                        val isCurrent = currentStepIndex == stepData.status.stepIndex

                        ProgressMarkerNode(
                            marker = stepData,
                            isCompleted = isCompleted,
                            isCurrent = isCurrent,
                            onClick = { onMarkerClick(stepData.status) }
                        )
                    }
                }
            }

            // Active Stage Summary Callout
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = when (currentStatus) {
                    OrderStatus.ORDERED -> FreshGreenContainer.copy(alpha = 0.6f)
                    OrderStatus.PREPARING -> AmberLoyaltyContainer.copy(alpha = 0.6f)
                    OrderStatus.OUT_FOR_DELIVERY -> FreshGreenContainer
                    OrderStatus.DELIVERED -> SuccessGreen.copy(alpha = 0.15f)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (currentStatus) {
                            OrderStatus.ORDERED -> "📋"
                            OrderStatus.PREPARING -> "🥦"
                            OrderStatus.OUT_FOR_DELIVERY -> "🚴"
                            OrderStatus.DELIVERED -> "🎉"
                        },
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Current Status: ${currentStatus.display}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentStatus == OrderStatus.PREPARING) AmberLoyaltyOnContainer else FreshGreenPrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (currentStatus == OrderStatus.DELIVERED) SuccessGreen else FreshGreenLight)
                            )
                        }
                        Text(
                            text = currentStatus.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

data class ProgressMarkerData(
    val status: OrderStatus,
    val title: String,
    val stepLabel: String,
    val icon: ImageVector,
    val time: String
)

@Composable
fun ProgressMarkerNode(
    marker: ProgressMarkerData,
    isCompleted: Boolean,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "marker_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(74.dp)
            .clickable(onClick = onClick)
            .testTag("progress_marker_${marker.title.lowercase().replace(" ", "_")}")
    ) {
        // Node Icon Circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .scale(if (isCurrent) pulseScale else 1.0f),
            contentAlignment = Alignment.Center
        ) {
            if (isCurrent) {
                // Pulsing outer halo ring
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(FreshGreenLight.copy(alpha = 0.35f))
                )
            }

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> FreshGreenPrimary
                            isCurrent -> FreshGreenPrimary
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                    .border(
                        width = if (isCurrent) 2.dp else 0.dp,
                        color = if (isCurrent) Color.White else Color.Transparent,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Icon(
                        imageVector = marker.icon,
                        contentDescription = marker.title,
                        tint = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Title Label
        Text(
            text = marker.title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isCurrent || isCompleted) FontWeight.Bold else FontWeight.Medium,
                color = when {
                    isCurrent -> FreshGreenPrimary
                    isCompleted -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontSize = 11.sp
            ),
            textAlign = TextAlign.Center,
            maxLines = 2
        )

        // Status badge / time
        if (isCurrent) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = FreshGreenContainer,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = "ACTIVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = FreshGreenOnContainer,
                        fontSize = 8.sp
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        } else {
            Text(
                text = marker.time,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 9.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ActiveOrderStatusHeroCard(order: Order, modifier: Modifier = Modifier) {
    val isDelivered = order.status == OrderStatus.DELIVERED
    val dateFormat = remember { SimpleDateFormat("h:mm a • MMM d", Locale.getDefault()) }
    val formattedTime = remember(order.timestamp) { dateFormat.format(Date(order.timestamp)) }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("active_order_hero_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Active Delivery #${order.id}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Placed at $formattedTime",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDelivered) SuccessGreen.copy(alpha = 0.15f) else FreshGreenContainer
                ) {
                    Text(
                        text = order.status.display,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDelivered) SuccessGreen else FreshGreenPrimary
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            // Real-Time ETA Card Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isDelivered) FreshGreenContainer else FreshGreenDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isDelivered) FreshGreenPrimary else FreshGreenLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isDelivered) "🏠" else "⚡",
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isDelivered) "Order Delivered Successfully!" else "Estimated Arrival in ${order.etaMinutes} Minutes",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDelivered) FreshGreenOnContainer else Color.White
                            )
                        )
                        Text(
                            text = if (isDelivered) "Delivered to ${order.deliveryAddress}" else "Driver Alex is carrying your chilled groceries in eco-cargo bike #14",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isDelivered) FreshGreenOnContainer.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Detailed Cards for each of the 4 Milestones
 */
@Composable
fun DetailedFourStageMilestonesCard(order: Order, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Milestone Details",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            // Stage 1: Ordered
            StageDetailRow(
                stageName = "1. Ordered",
                description = "Order received and payment captured securely via ${order.paymentMethod}.",
                icon = Icons.Default.Receipt,
                isCompleted = order.status.stepIndex > 1,
                isActive = order.status == OrderStatus.ORDERED,
                badgeText = if (order.status.stepIndex >= 1) "VERIFIED" else "PENDING"
            )

            // Stage 2: Preparing
            StageDetailRow(
                stageName = "2. Preparing",
                description = "Organic produce hand-picked, chilled at 37°F & packed in temperature-controlled tote.",
                icon = Icons.Default.Inventory2,
                isCompleted = order.status.stepIndex > 2,
                isActive = order.status == OrderStatus.PREPARING,
                badgeText = if (order.status.stepIndex >= 2) "PACKED" else "PENDING"
            )

            // Stage 3: Out for Delivery
            StageDetailRow(
                stageName = "3. Out for Delivery",
                description = "Courier Alex Rivera dispatched on Electric Eco-Cargo Bike. Live GPS telemetry streaming.",
                icon = Icons.Default.ElectricBike,
                isCompleted = order.status.stepIndex > 3,
                isActive = order.status == OrderStatus.OUT_FOR_DELIVERY,
                badgeText = if (order.status.stepIndex >= 3) "EN ROUTE" else "PENDING"
            )

            // Stage 4: Delivered
            StageDetailRow(
                stageName = "4. Delivered",
                description = "Tote safely placed at ${order.deliveryAddress}. Confirmation photo logged.",
                icon = Icons.Default.Home,
                isCompleted = order.status == OrderStatus.DELIVERED,
                isActive = order.status == OrderStatus.DELIVERED,
                badgeText = if (order.status == OrderStatus.DELIVERED) "COMPLETED" else "PENDING"
            )
        }
    }
}

@Composable
fun StageDetailRow(
    stageName: String,
    description: String,
    icon: ImageVector,
    isCompleted: Boolean,
    isActive: Boolean,
    badgeText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> FreshGreenPrimary
                        isActive -> AmberLoyalty
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isCompleted) Icons.Default.Check else icon,
                contentDescription = null,
                tint = if (isCompleted || isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stageName,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = if (isActive || isCompleted) FontWeight.Bold else FontWeight.Medium,
                        color = if (isActive) FreshGreenPrimary else MaterialTheme.colorScheme.onSurface
                    )
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when {
                        isCompleted -> FreshGreenContainer
                        isActive -> AmberLoyaltyContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isCompleted -> FreshGreenOnContainer
                                isActive -> AmberLoyaltyOnContainer
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
fun LiveDeliveryMapCard(order: Order, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_animation")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 18f,
        targetValue = 36f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_radius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_delivery_map_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        tint = FreshGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Live GPS Courier Route",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Red.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Red,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE9F0EC))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    val startX = 60.dp.toPx()
                    val startY = canvasHeight * 0.75f

                    val endX = canvasWidth - 60.dp.toPx()
                    val endY = canvasHeight * 0.25f

                    val controlX = canvasWidth * 0.5f
                    val controlY = canvasHeight * 0.85f

                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(startX, startY)
                        quadraticTo(controlX, controlY, endX, endY)
                    }

                    drawPath(
                        path = path,
                        color = Color(0xFFBACEC0),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 10.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )

                    drawPath(
                        path = path,
                        color = FreshGreenLight,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 6.dp.toPx(),
                            cap = StrokeCap.Round,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f), 0f)
                        )
                    )

                    drawCircle(
                        color = FreshGreenPrimary,
                        radius = 12.dp.toPx(),
                        center = Offset(startX, startY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 5.dp.toPx(),
                        center = Offset(startX, startY)
                    )

                    drawCircle(
                        color = Color(0xFFD97706),
                        radius = 12.dp.toPx(),
                        center = Offset(endX, endY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 5.dp.toPx(),
                        center = Offset(endX, endY)
                    )

                    val t = order.trackingProgress.coerceIn(0f, 1f)
                    val riderX = (1 - t) * (1 - t) * startX + 2 * (1 - t) * t * controlX + t * t * endX
                    val riderY = (1 - t) * (1 - t) * startY + 2 * (1 - t) * t * controlY + t * t * endY

                    if (order.status != OrderStatus.DELIVERED) {
                        drawCircle(
                            color = FreshGreenLight.copy(alpha = pulseAlpha),
                            radius = pulseRadius * 2.5f,
                            center = Offset(riderX, riderY)
                        )
                    }

                    drawCircle(
                        color = Color.White,
                        radius = 16.dp.toPx(),
                        center = Offset(riderX, riderY)
                    )
                    drawCircle(
                        color = FreshGreenPrimary,
                        radius = 13.dp.toPx(),
                        center = Offset(riderX, riderY)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.White.copy(alpha = 0.9f),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 12.dp, bottom = 12.dp)
                ) {
                    Text(
                        text = "🏪 FreshMart Central",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.White.copy(alpha = 0.9f),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 12.dp, top = 12.dp)
                ) {
                    Text(
                        text = "🏠 Your Home",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DeliveryTelemetryRow(order: Order, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TelemetryPill(
            icon = "❄️",
            title = "Cold-Chain",
            value = "37°F Chilled Box",
            modifier = Modifier.weight(1f)
        )
        TelemetryPill(
            icon = "⚡",
            title = "Eco Courier",
            value = "Cargo E-Bike #14",
            modifier = Modifier.weight(1f)
        )
        TelemetryPill(
            icon = "📍",
            title = "Distance",
            value = if (order.status == OrderStatus.DELIVERED) "Arrived" else "0.8 miles away",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TelemetryPill(
    icon: String,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 16.sp)
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
fun DriverProfileCard(
    order: Order,
    onOpenChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(FreshGreenContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🚴", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = order.driverName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = AmberLoyalty,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${order.driverRating} • Top Courier",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "100% Eco Ride",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = FreshGreenPrimary,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onOpenChat,
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("message_courier_btn")
                ) {
                    Icon(imageVector = Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Message", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onOpenChat,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("call_courier_btn")
                ) {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = FreshGreenPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call Rider", fontWeight = FontWeight.Bold, color = FreshGreenPrimary)
                }
            }
        }
    }
}

@Composable
fun OrderReceiptSummaryCard(order: Order, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order Receipt",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Rs. ${String.format("%.2f", order.total)} Paid",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = FreshGreenPrimary
                    )
                )
            }

            Text(
                text = "Items: ${order.itemsSummary}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Text(
                text = "Payment: ${order.paymentMethod} (Txn: ${order.paymentTxnId})",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            )

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
                        text = "⭐ You earned +${order.pointsEarned} FreshRewards points on this order!",
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
fun PastOrderItemCard(
    order: Order,
    isCurrentlyTracking: Boolean,
    onSelectTracking: () -> Unit,
    onReorder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()) }
    val formattedDate = remember(order.timestamp) { dateFormat.format(Date(order.timestamp)) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("past_order_${order.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order #${order.id}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (order.status == OrderStatus.DELIVERED) SuccessGreen.copy(alpha = 0.15f) else FreshGreenContainer
                ) {
                    Text(
                        text = order.status.display,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (order.status == OrderStatus.DELIVERED) SuccessGreen else FreshGreenPrimary
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = order.itemsSummary,
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                maxLines = 2
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: Rs. ${String.format("%.2f", order.total)}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = FreshGreenPrimary
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onReorder,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reorder", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onSelectTracking,
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (order.status == OrderStatus.DELIVERED) "View Details" else "Live Track", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
