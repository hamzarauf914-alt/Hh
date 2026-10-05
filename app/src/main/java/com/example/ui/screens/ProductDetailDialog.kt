package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Product
import com.example.ui.components.QuantityStepper
import com.example.ui.theme.AmberLoyalty
import com.example.ui.theme.AmberLoyaltyContainer
import com.example.ui.theme.AmberLoyaltyOnContainer
import com.example.ui.theme.BadgeRed
import com.example.ui.theme.FreshGreenContainer
import com.example.ui.theme.FreshGreenDark
import com.example.ui.theme.FreshGreenLight
import com.example.ui.theme.FreshGreenOnContainer
import com.example.ui.theme.FreshGreenPrimary
import com.example.ui.theme.SuccessGreen

data class ProductNutrition(
    val servingSize: String,
    val calories: Int,
    val proteinGrams: Double,
    val carbsGrams: Double,
    val fatGrams: Double,
    val fiberGrams: Double,
    val sugarGrams: Double,
    val keyVitamins: List<Pair<String, String>>, // e.g. "Vitamin C" to "14% DV"
    val healthTags: List<String>,
    val storageTip: String
)

fun getNutritionForProduct(product: Product): ProductNutrition {
    return when {
        product.category.contains("Fruit", ignoreCase = true) || product.name.contains("Apple", ignoreCase = true) -> {
            ProductNutrition(
                servingSize = "1 medium piece (100g)",
                calories = 52,
                proteinGrams = 0.3,
                carbsGrams = 13.8,
                fatGrams = 0.2,
                fiberGrams = 2.4,
                sugarGrams = 10.4,
                keyVitamins = listOf(
                    "Vitamin C" to "14% DV",
                    "Potassium" to "3% DV",
                    "Antioxidants" to "High",
                    "Iron" to "1% DV"
                ),
                healthTags = listOf("100% Organic", "High Fiber", "Zero Cholesterol", "Heart Healthy"),
                storageTip = "Store in the refrigerator crisper drawer to maintain optimal crunch and sweetness for up to 2 weeks."
            )
        }
        product.name.contains("Avocado", ignoreCase = true) -> {
            ProductNutrition(
                servingSize = "1/2 avocado (100g)",
                calories = 160,
                proteinGrams = 2.0,
                carbsGrams = 8.5,
                fatGrams = 14.7,
                fiberGrams = 6.7,
                sugarGrams = 0.7,
                keyVitamins = listOf(
                    "Folate (B9)" to "20% DV",
                    "Potassium" to "14% DV",
                    "Vitamin K" to "26% DV",
                    "Vitamin E" to "10% DV"
                ),
                healthTags = listOf("Healthy Omega Fats", "Keto Friendly", "Low Sugar", "Potassium Rich"),
                storageTip = "Keep at room temperature until ripe, then store in the fridge for up to 5 days."
            )
        }
        product.category.contains("Dairy", ignoreCase = true) || product.name.contains("Milk", ignoreCase = true) || product.name.contains("Yogurt", ignoreCase = true) || product.name.contains("Egg", ignoreCase = true) -> {
            ProductNutrition(
                servingSize = "1 glass / 1 serving (100g)",
                calories = 64,
                proteinGrams = 3.4,
                carbsGrams = 4.8,
                fatGrams = 3.6,
                fiberGrams = 0.0,
                sugarGrams = 4.8,
                keyVitamins = listOf(
                    "Calcium" to "28% DV",
                    "Vitamin D3" to "15% DV",
                    "Vitamin B12" to "18% DV",
                    "Phosphorus" to "22% DV"
                ),
                healthTags = listOf("High Protein", "Bone Health", "Pasture Raised", "Calcium Rich"),
                storageTip = "Keep refrigerated at 2°C to 4°C. Consume within 4-5 days after opening."
            )
        }
        product.category.contains("Meat", ignoreCase = true) || product.name.contains("Chicken", ignoreCase = true) || product.name.contains("Beef", ignoreCase = true) || product.name.contains("Salmon", ignoreCase = true) -> {
            ProductNutrition(
                servingSize = "100g raw serving",
                calories = 165,
                proteinGrams = 31.0,
                carbsGrams = 0.0,
                fatGrams = 3.6,
                fiberGrams = 0.0,
                sugarGrams = 0.0,
                keyVitamins = listOf(
                    "Vitamin B6" to "30% DV",
                    "Niacin (B3)" to "45% DV",
                    "Phosphorus" to "20% DV",
                    "Iron" to "6% DV"
                ),
                healthTags = listOf("100% Lean Muscle Protein", "Zero Carbs", "Antibiotic Free", "Hygienically Cut"),
                storageTip = "Keep frozen at -18°C or refrigerated at 0°C-2°C. Cook thoroughly before consumption."
            )
        }
        product.category.contains("Bakery", ignoreCase = true) || product.name.contains("Sourdough", ignoreCase = true) || product.name.contains("Bread", ignoreCase = true) -> {
            ProductNutrition(
                servingSize = "1 thick slice (60g)",
                calories = 145,
                proteinGrams = 5.2,
                carbsGrams = 28.5,
                fatGrams = 0.8,
                fiberGrams = 1.9,
                sugarGrams = 1.2,
                keyVitamins = listOf(
                    "Folate" to "12% DV",
                    "Iron" to "8% DV",
                    "Thiamine (B1)" to "15% DV",
                    "Prebiotics" to "Active"
                ),
                healthTags = listOf("Naturally Fermented", "Easy Digestion", "Non-GMO", "No Added Sugar"),
                storageTip = "Keep in a cool, dry breadbox. Can be frozen for up to 3 months and toasted directly."
            )
        }
        else -> {
            ProductNutrition(
                servingSize = "100g standard portion",
                calories = 78,
                proteinGrams = 2.1,
                carbsGrams = 14.2,
                fatGrams = 1.5,
                fiberGrams = 3.0,
                sugarGrams = 4.0,
                keyVitamins = listOf(
                    "Vitamin A" to "10% DV",
                    "Vitamin C" to "15% DV",
                    "Minerals" to "Natural",
                    "Antioxidants" to "High"
                ),
                healthTags = listOf("100% Organic Sourcing", "Pesticide Free", "Farm Fresh", "Immunity Support"),
                storageTip = "Store in a cool, dry pantry away from direct sunlight."
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductDetailDialog(
    product: Product,
    quantityInCart: Int,
    onAddToCart: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedQty by remember { mutableIntStateOf(if (quantityInCart > 0) quantityInCart else 1) }
    var isFavorited by remember { mutableStateOf(false) }
    var selectedAngleIndex by remember { mutableIntStateOf(0) }

    val nutrition = remember(product) { getNutritionForProduct(product) }

    val discountPercent = product.discountPercent

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 12.dp)
                .testTag("product_detail_dialog")
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top Header bar with Close & Favorite
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = FreshGreenContainer
                        ) {
                            Text(
                                text = product.category,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FreshGreenOnContainer,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (discountPercent != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BadgeRed
                            ) {
                                Text(
                                    text = "$discountPercent% OFF",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Row {
                        IconButton(onClick = { isFavorited = !isFavorited }) {
                            Icon(
                                imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Add to Favorites",
                                tint = if (isFavorited) BadgeRed else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Detail View",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. High-Quality Product Image Placeholder Hero
                    HighQualityProductImagePlaceholder(
                        product = product,
                        selectedAngle = selectedAngleIndex,
                        onSelectAngle = { selectedAngleIndex = it }
                    )

                    // 2. Title, Unit & Price Section
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Text(
                            text = "Net Unit: ${product.unit} • Sourced: ${product.origin}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        )

                        // Rating & Reviews row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = AmberLoyalty,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${product.rating} Star Rating",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• (${product.reviewCount} customer reviews)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Price Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Rs. ${String.format("%.2f", product.price)}",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = FreshGreenPrimary
                                        )
                                    )
                                    if (product.originalPrice != null) {
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Rs. ${String.format("%.2f", product.originalPrice)}",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                textDecoration = TextDecoration.LineThrough,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }

                                if (product.originalPrice != null) {
                                    val savings = product.originalPrice - product.price
                                    Text(
                                        text = "You save Rs. ${String.format("%.2f", savings)} on this item!",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SuccessGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            // FreshRewards point perk badge
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = AmberLoyaltyContainer
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
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "+${(product.price * 0.1).toInt()} pts",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = AmberLoyaltyOnContainer
                                        )
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.5f))

                    // 3. Description
                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f),
                            lineHeight = 22.sp
                        )
                    )

                    // 4. Detailed Nutrition Information Section
                    NutritionInfoCard(nutrition = nutrition)

                    // 5. Health Highlights & Dietary Tags
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Dietary & Health Attributes",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            nutrition.healthTags.forEach { tag ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = FreshGreenContainer.copy(alpha = 0.6f),
                                    border = BorderStroke(1.dp, FreshGreenPrimary.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = FreshGreenPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = tag,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = FreshGreenOnContainer,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 6. Sourcing & Storage Card
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.Kitchen,
                                    contentDescription = null,
                                    tint = FreshGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Storage & Freshness Advice",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = nutrition.storageTip,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = FreshGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Dadyal Online Quality Guarantee",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "100% farm freshness satisfaction guaranteed. If not satisfied, instant doorstep replacement or full refund.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Sticky Bottom Cart Control Bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        QuantityStepper(
                            quantity = selectedQty,
                            onIncrease = { selectedQty++ },
                            onDecrease = { if (selectedQty > 1) selectedQty-- }
                        )

                        Button(
                            onClick = {
                                onAddToCart(selectedQty)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("dialog_add_to_cart_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Add to Cart • Rs. ${String.format("%.2f", product.price * selectedQty)}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
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
fun HighQualityProductImagePlaceholder(
    product: Product,
    selectedAngle: Int,
    onSelectAngle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                FreshGreenContainer.copy(alpha = 0.8f),
                                FreshGreenLight.copy(alpha = 0.25f),
                                Color(0xFFF1F8F4)
                            )
                        )
                    )
            ) {
                // Background artistic pattern badge
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.9f),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = FreshGreenPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Studio 4K HD",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = FreshGreenDark,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.9f),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = FreshGreenPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "100% Organic",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = FreshGreenDark,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                // Center Main High-Res Visual Illustration & Drop Shadow
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.65f))
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = product.iconEmoji,
                            fontSize = 62.sp
                        )
                    }

                    // Soft ambient ground shadow
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.Black.copy(alpha = 0.08f))
                    )
                }

                // Bottom angle indicators
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Front", "Fresh Cut", "Pack").forEachIndexed { index, angleName ->
                        val isSelected = selectedAngle == index
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) FreshGreenPrimary else Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.clickable { onSelectAngle(index) }
                        ) {
                            Text(
                                text = angleName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NutritionInfoCard(nutrition: ProductNutrition, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("nutrition_info_section")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = FreshGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Nutrition Facts",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = nutrition.servingSize,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // 4 Macro Pill Metric Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Calories
                MacroMetricBox(
                    label = "Calories",
                    value = "${nutrition.calories}",
                    unit = "kcal",
                    containerColor = AmberLoyaltyContainer.copy(alpha = 0.5f),
                    contentColor = AmberLoyaltyOnContainer,
                    modifier = Modifier.weight(1f)
                )

                // Protein
                MacroMetricBox(
                    label = "Protein",
                    value = "${nutrition.proteinGrams}",
                    unit = "g",
                    containerColor = FreshGreenContainer.copy(alpha = 0.6f),
                    contentColor = FreshGreenOnContainer,
                    modifier = Modifier.weight(1f)
                )

                // Carbs
                MacroMetricBox(
                    label = "Carbs",
                    value = "${nutrition.carbsGrams}",
                    unit = "g",
                    containerColor = Color(0xFFE0F2FE),
                    contentColor = Color(0xFF0369A1),
                    modifier = Modifier.weight(1f)
                )

                // Fiber / Fats
                MacroMetricBox(
                    label = "Fiber",
                    value = "${nutrition.fiberGrams}",
                    unit = "g",
                    containerColor = Color(0xFFF3E8FF),
                    contentColor = Color(0xFF6B21A8),
                    modifier = Modifier.weight(1f)
                )
            }

            // Detailed Micro Table
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                NutritionDetailRow(name = "Total Fat", amount = "${nutrition.fatGrams}g")
                NutritionDetailRow(name = "Dietary Fiber", amount = "${nutrition.fiberGrams}g")
                NutritionDetailRow(name = "Natural Sugars", amount = "${nutrition.sugarGrams}g")
            }

            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.4f))

            // Vitamins & Minerals Chips
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Vitamins & Micronutrients:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    nutrition.keyVitamins.forEach { (vit, percent) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = vit,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = percent,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = FreshGreenPrimary,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MacroMetricBox(
    label: String,
    value: String,
    unit: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = contentColor.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = contentColor
                    )
                )
                Text(
                    text = unit,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        color = contentColor.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 2.dp, start = 1.dp)
                )
            }
        }
    }
}

@Composable
fun NutritionDetailRow(name: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
