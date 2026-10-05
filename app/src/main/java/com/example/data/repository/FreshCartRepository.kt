package com.example.data.repository

import com.example.data.local.GroceryDao
import com.example.data.model.CartItem
import com.example.data.model.CartItemWithProduct
import com.example.data.model.Customer
import com.example.data.model.LoyaltyProfile
import com.example.data.model.LoyaltyTier
import com.example.data.model.LoyaltyTransaction
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.random.Random

class FreshCartRepository(
    private val groceryDao: GroceryDao,
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    val allProducts: Flow<List<Product>> = groceryDao.getAllProducts()
    val cartItems: Flow<List<CartItem>> = groceryDao.getCartItems()
    val loyaltyProfile: Flow<LoyaltyProfile?> = groceryDao.getLoyaltyProfile()
    val loyaltyTransactions: Flow<List<LoyaltyTransaction>> = groceryDao.getLoyaltyTransactions()
    val allOrders: Flow<List<Order>> = groceryDao.getAllOrders()

    // Combined Flow: Cart Items with their full Product data
    val cartItemsWithProduct: Flow<List<CartItemWithProduct>> = combine(
        groceryDao.getCartItems(),
        groceryDao.getAllProducts()
    ) { cartList, productList ->
        val productMap = productList.associateBy { it.id }
        cartList.mapNotNull { cartItem ->
            productMap[cartItem.productId]?.let { product ->
                CartItemWithProduct(product = product, quantity = cartItem.quantity)
            }
        }
    }

    suspend fun initializeSeedDataIfNeeded() {
        if (groceryDao.getProductCount() == 0) {
            seedInitialCatalog()
            seedInitialLoyalty()
            seedInitialOrder()
        }
    }

    private suspend fun seedInitialCatalog() {
        val sampleProducts = listOf(
            // Produce
            Product(
                id = "prod_honeycrisp",
                name = "Honeycrisp Apples",
                category = "Fruits & Veggies",
                price = 3.99,
                originalPrice = 4.99,
                unit = "2 lb bag (approx. 5 apples)",
                description = "Crisp, sweet, and locally harvested crisp apples with an ultra-satisfying crunch. Perfect for snacking or baking.",
                rating = 4.9f,
                reviewCount = 240,
                badge = "20% OFF",
                dietaryTag = "Farm Fresh",
                iconEmoji = "🍎",
                origin = "Yakima Valley Orchards"
            ),
            Product(
                id = "prod_avocado",
                name = "Organic Hass Avocados",
                category = "Fruits & Veggies",
                price = 4.49,
                originalPrice = 5.29,
                unit = "Pack of 4 ripe",
                description = "Creamy, rich Hass avocados carefully selected at peak ripeness. Ideal for toast, salads, and homemade guacamole.",
                rating = 4.8f,
                reviewCount = 188,
                badge = "Organic",
                dietaryTag = "Organic",
                iconEmoji = "🥑",
                origin = "Fair Trade Certified"
            ),
            Product(
                id = "prod_spinach",
                name = "Organic Baby Spinach",
                category = "Fruits & Veggies",
                price = 2.99,
                originalPrice = 3.49,
                unit = "10 oz clam-shell",
                description = "Triple-washed tender baby spinach leaves packed with vitamins and iron. Ready to toss into green smoothies or sauté.",
                rating = 4.7f,
                reviewCount = 95,
                badge = "Organic",
                dietaryTag = "Organic",
                iconEmoji = "🥬",
                origin = "Sunrise Organic Fields"
            ),
            Product(
                id = "prod_strawberries",
                name = "Sweet Driscoll's Strawberries",
                category = "Fruits & Veggies",
                price = 3.79,
                originalPrice = 4.50,
                unit = "1 lb container",
                description = "Sun-ripened, intensely sweet and juicy red strawberries picked at optimal sugar sweetness.",
                rating = 4.9f,
                reviewCount = 310,
                badge = "Bestseller",
                dietaryTag = "Farm Fresh",
                iconEmoji = "🍓",
                origin = "California Berry Cooperative"
            ),
            Product(
                id = "prod_bananas",
                name = "Organic Cavendish Bananas",
                category = "Fruits & Veggies",
                price = 1.49,
                originalPrice = null,
                unit = "Approx. 2 lbs (1 bunch)",
                description = "Certified organic yellow bananas with high potassium and natural sweetness.",
                rating = 4.8f,
                reviewCount = 420,
                badge = "Essential",
                dietaryTag = "Organic",
                iconEmoji = "🍌",
                origin = "Equatorial Organic Co."
            ),
            Product(
                id = "prod_tomatoes",
                name = "Heirloom Vine Tomatoes",
                category = "Fruits & Veggies",
                price = 3.29,
                originalPrice = 3.99,
                unit = "1.5 lbs cluster",
                description = "Fragrant, plump vine-ripened heirloom tomatoes bursting with natural umami flavor.",
                rating = 4.6f,
                reviewCount = 84,
                badge = null,
                dietaryTag = "Farm Fresh",
                iconEmoji = "🍅",
                origin = "Green Valley Hydroponics"
            ),

            // Dairy & Eggs
            Product(
                id = "prod_milk",
                name = "Organic Whole Milk",
                category = "Dairy & Eggs",
                price = 4.79,
                originalPrice = 5.29,
                unit = "1 Gallon (128 fl oz)",
                description = "Pasture-raised, hormone-free grade A organic whole milk. Rich, creamy, and gently pasteurized.",
                rating = 4.9f,
                reviewCount = 512,
                badge = "Bestseller",
                dietaryTag = "Organic",
                iconEmoji = "🥛",
                origin = "Maplewood Pastures"
            ),
            Product(
                id = "prod_eggs",
                name = "Pasture-Raised Brown Eggs",
                category = "Dairy & Eggs",
                price = 5.49,
                originalPrice = 5.99,
                unit = "Grade A Large (Dozen)",
                description = "Golden, vibrant yolks from hens granted 108 sq. ft. of sunny outdoor pasture each. Certified humane.",
                rating = 5.0f,
                reviewCount = 630,
                badge = "Top Rated",
                dietaryTag = "Pasture Raised",
                iconEmoji = "🥚",
                origin = "Vital Pasture Farms"
            ),
            Product(
                id = "prod_greek_yogurt",
                name = "Plain Greek Whole Milk Yogurt",
                category = "Dairy & Eggs",
                price = 3.99,
                originalPrice = null,
                unit = "32 oz tub",
                description = "Strained the traditional Mediterranean way for a thick, velvety texture. 18g protein per serving.",
                rating = 4.8f,
                reviewCount = 142,
                badge = "High Protein",
                dietaryTag = "Gluten Free",
                iconEmoji = "🥣",
                origin = "Olympus Creameries"
            ),
            Product(
                id = "prod_cheddar",
                name = "Aged Sharp White Cheddar",
                category = "Dairy & Eggs",
                price = 4.99,
                originalPrice = 5.99,
                unit = "8 oz artisanal block",
                description = "Naturally aged over 12 months for complex savory notes and subtle crystalline crunch.",
                rating = 4.9f,
                reviewCount = 98,
                badge = "Aged 12 Mo",
                dietaryTag = "Gluten Free",
                iconEmoji = "🧀",
                origin = "Vermont Cheddar Guild"
            ),

            // Bakery
            Product(
                id = "prod_sourdough",
                name = "Artisan Country Sourdough",
                category = "Bakery",
                price = 5.99,
                originalPrice = 6.99,
                unit = "24 oz rustic round loaf",
                description = "Naturally fermented for 36 hours with wild yeast starter. Blistered crispy crust with tender, airy crumb.",
                rating = 4.9f,
                reviewCount = 380,
                badge = "Freshly Baked",
                dietaryTag = "Vegan",
                iconEmoji = "🍞",
                origin = "StoneMill Artisan Bakers"
            ),
            Product(
                id = "prod_croissants",
                name = "Pure Butter French Croissants",
                category = "Bakery",
                price = 4.99,
                originalPrice = null,
                unit = "Pack of 4 large",
                description = "Layered with European cultured butter for an irresistibly flaky, golden pastry experience.",
                rating = 4.8f,
                reviewCount = 160,
                badge = null,
                dietaryTag = "Vegetarian",
                iconEmoji = "🥐",
                origin = "Le Petit Four Bakery"
            ),

            // Meat & Seafood
            Product(
                id = "prod_salmon",
                name = "Wild Atlantic Salmon Fillet",
                category = "Meat & Seafood",
                price = 11.99,
                originalPrice = 13.99,
                unit = "1 lb fresh fillet",
                description = "Responsibly sourced, ocean-fresh wild salmon high in heart-healthy Omega-3 fatty acids.",
                rating = 4.9f,
                reviewCount = 175,
                badge = "Wild Caught",
                dietaryTag = "Gluten Free",
                iconEmoji = "🐟",
                origin = "North Atlantic Fishery"
            ),
            Product(
                id = "prod_beef",
                name = "100% Grass-Fed Ground Beef (85/15)",
                category = "Meat & Seafood",
                price = 7.49,
                originalPrice = 8.49,
                unit = "1 lb vacuum sealed",
                description = "Never given antibiotics or added hormones. Rich flavor, juicy marbling, and tender grind.",
                rating = 4.8f,
                reviewCount = 210,
                badge = "Grass Fed",
                dietaryTag = "Keto Friendly",
                iconEmoji = "🥩",
                origin = "Silver Creek Ranch"
            ),

            // Pantry & Snacks
            Product(
                id = "prod_olive_oil",
                name = "Cold-Pressed Extra Virgin Olive Oil",
                category = "Pantry",
                price = 12.99,
                originalPrice = 15.99,
                unit = "750 ml glass bottle",
                description = "Single-estate early harvest Koroneiki olives. Peppery finish with high polyphenol antioxidants.",
                rating = 5.0f,
                reviewCount = 290,
                badge = "Cold Pressed",
                dietaryTag = "Organic",
                iconEmoji = "🫒",
                origin = "Kalamata Orchards"
            ),
            Product(
                id = "prod_raw_honey",
                name = "Pure Unfiltered Wildflower Honey",
                category = "Pantry",
                price = 8.49,
                originalPrice = 9.99,
                unit = "16 oz jar",
                description = "Raw, unpasteurized local wildflower honey retaining all natural enzymes and pollen grains.",
                rating = 4.9f,
                reviewCount = 145,
                badge = "Raw & Pure",
                dietaryTag = "Farm Fresh",
                iconEmoji = "🍯",
                origin = "Highland Bee Sanctuaries"
            ),
            Product(
                id = "prod_almonds",
                name = "Roasted Sea Salt California Almonds",
                category = "Pantry",
                price = 5.99,
                originalPrice = 6.49,
                unit = "12 oz resealable pouch",
                description = "Dry-roasted with a touch of mineral sea salt. Crunchy, wholesome, and fiber-rich.",
                rating = 4.7f,
                reviewCount = 110,
                badge = null,
                dietaryTag = "Vegan",
                iconEmoji = "🥜",
                origin = "San Joaquin Valley"
            ),

            // Beverages
            Product(
                id = "prod_oj",
                name = "Fresh Squeezed Florida OJ (No Pulp)",
                category = "Beverages",
                price = 4.49,
                originalPrice = null,
                unit = "52 fl oz jug",
                description = "100% pure squeezed Florida oranges with no added sugar, artificial flavors, or concentrates.",
                rating = 4.8f,
                reviewCount = 230,
                badge = "100% Juice",
                dietaryTag = "Gluten Free",
                iconEmoji = "🍊",
                origin = "Citrus Sun Groves"
            ),
            Product(
                id = "prod_cold_brew",
                name = "Nitro Cold Brew Coffee",
                category = "Beverages",
                price = 3.99,
                originalPrice = 4.50,
                unit = "12 fl oz can",
                description = "Steeped for 20 hours with micro-infused nitrogen for a silky cascading foam and zero bitterness.",
                rating = 4.9f,
                reviewCount = 180,
                badge = "Single Origin",
                dietaryTag = "Vegan",
                iconEmoji = "☕",
                origin = "Equator Roasters"
            )
        )
        groceryDao.insertProducts(sampleProducts)

        // Seed initial items in cart so user can test checkout right away
        groceryDao.insertCartItem(CartItem(productId = "prod_honeycrisp", quantity = 1))
        groceryDao.insertCartItem(CartItem(productId = "prod_sourdough", quantity = 1))
        groceryDao.insertCartItem(CartItem(productId = "prod_milk", quantity = 1))
    }

    private suspend fun seedInitialLoyalty() {
        val initialProfile = LoyaltyProfile(
            id = 1,
            pointsBalance = 450,
            tier = LoyaltyTier.SILVER,
            lifetimePointsEarned = 750,
            totalSavedWithPoints = 300.00,
            memberSince = "Member since Aug 2026"
        )
        groceryDao.insertOrUpdateLoyaltyProfile(initialProfile)

        val tx1 = LoyaltyTransaction(
            points = 250,
            type = "BONUS",
            description = "Welcome to FreshRewards Loyalty Program",
            timestamp = System.currentTimeMillis() - 86400000L * 7
        )
        val tx2 = LoyaltyTransaction(
            points = 500,
            type = "EARNED",
            description = "Earned 12.5x Silver VIP pts on Order #FC-7102",
            timestamp = System.currentTimeMillis() - 86400000L * 2,
            orderId = "FC-7102"
        )
        val tx3 = LoyaltyTransaction(
            points = -300,
            type = "REDEEMED",
            description = "Redeemed 300 pts for Rs. 300 off on Order #FC-7102",
            timestamp = System.currentTimeMillis() - 86400000L * 2,
            orderId = "FC-7102"
        )
        groceryDao.insertLoyaltyTransaction(tx1)
        groceryDao.insertLoyaltyTransaction(tx2)
        groceryDao.insertLoyaltyTransaction(tx3)
    }

    private suspend fun seedInitialOrder() {
        val pastOrder = Order(
            id = "FC-7102",
            timestamp = System.currentTimeMillis() - 86400000L * 2,
            status = OrderStatus.DELIVERED,
            subtotal = 1450.00,
            discount = 300.00,
            pointsRedeemed = 300,
            pointsDiscount = 300.00,
            pointsEarned = 480,
            deliveryFee = 0.00,
            tip = 100.00,
            tax = 60.00,
            total = 1310.00,
            paymentMethod = "Visa •••• 4242",
            paymentTxnId = "TXN-SEC-710294",
            deliveryAddress = "Dadyal Central, Azad Kashmir",
            deliverySlot = "Delivered (On Time)",
            driverName = "Alex Rivera",
            driverPhone = "(555) 234-8901",
            driverVehicle = "Electric Eco-Cargo Bike #14",
            driverRating = 5.0f,
            etaMinutes = 0,
            trackingProgress = 1.0f,
            itemsSummary = "Pasture Eggs, Wild Salmon, Honeycrisp Apples (3 items)"
        )
        groceryDao.insertOrder(pastOrder)
    }

    suspend fun addToCart(productId: String) {
        val currentItems = groceryDao.getCartItems().firstOrNull() ?: emptyList()
        val existing = currentItems.find { it.productId == productId }
        if (existing != null) {
            groceryDao.updateCartItemQuantity(productId, existing.quantity + 1)
        } else {
            groceryDao.insertCartItem(CartItem(productId = productId, quantity = 1))
        }
    }

    suspend fun updateCartQuantity(productId: String, newQty: Int) {
        if (newQty <= 0) {
            groceryDao.deleteCartItem(productId)
        } else {
            groceryDao.updateCartItemQuantity(productId, newQty)
        }
    }

    suspend fun removeFromCart(productId: String) {
        groceryDao.deleteCartItem(productId)
    }

    suspend fun clearCart() {
        groceryDao.clearCart()
    }

    // Place Order & Process Real-Time Tracking Simulation
    suspend fun placeOrder(
        subtotal: Double,
        discount: Double,
        pointsRedeemed: Int,
        pointsDiscount: Double,
        deliveryFee: Double,
        tip: Double,
        tax: Double,
        total: Double,
        paymentMethod: String,
        deliveryAddress: String,
        deliverySlot: String,
        itemsSummary: String,
        itemsJson: String
    ): Order {
        val orderNum = Random.nextInt(1000, 9999)
        val orderId = "FC-$orderNum"
        val currentProfile = groceryDao.getLoyaltyProfile().firstOrNull() ?: LoyaltyProfile()

        // Calculate points to earn: 10 base points per dollar x tier multiplier
        val calculatedEarnedPoints = (total * 10 * currentProfile.tier.multiplier).toInt().coerceAtLeast(10)

        val newOrder = Order(
            id = orderId,
            timestamp = System.currentTimeMillis(),
            status = OrderStatus.ORDERED,
            subtotal = subtotal,
            discount = discount,
            pointsRedeemed = pointsRedeemed,
            pointsDiscount = pointsDiscount,
            pointsEarned = calculatedEarnedPoints,
            deliveryFee = deliveryFee,
            tip = tip,
            tax = tax,
            total = total,
            paymentMethod = paymentMethod,
            paymentTxnId = "TXN-SEC-" + Random.nextInt(100000, 999999),
            deliveryAddress = deliveryAddress,
            deliverySlot = deliverySlot,
            driverName = "Alex Rivera",
            driverPhone = "(555) 234-8901",
            driverVehicle = "Electric Eco-Cargo Bike #14",
            driverRating = 4.95f,
            etaMinutes = 20,
            trackingProgress = 0.05f,
            itemsSummary = itemsSummary,
            itemsJson = itemsJson
        )

        // 1. Save order to Room
        groceryDao.insertOrder(newOrder)

        // 2. Clear cart
        groceryDao.clearCart()

        // 3. Update Loyalty Profile
        val updatedBalance = (currentProfile.pointsBalance - pointsRedeemed + calculatedEarnedPoints).coerceAtLeast(0)
        val updatedLifetime = currentProfile.lifetimePointsEarned + calculatedEarnedPoints
        val updatedSaved = currentProfile.totalSavedWithPoints + pointsDiscount
        val updatedTier = when {
            updatedLifetime >= 3000 -> LoyaltyTier.PLATINUM
            updatedLifetime >= 1500 -> LoyaltyTier.GOLD
            updatedLifetime >= 500 -> LoyaltyTier.SILVER
            else -> LoyaltyTier.BRONZE
        }

        groceryDao.insertOrUpdateLoyaltyProfile(
            currentProfile.copy(
                pointsBalance = updatedBalance,
                tier = updatedTier,
                lifetimePointsEarned = updatedLifetime,
                totalSavedWithPoints = updatedSaved
            )
        )

        // 4. Record transactions
        if (pointsRedeemed > 0) {
            groceryDao.insertLoyaltyTransaction(
                LoyaltyTransaction(
                    points = -pointsRedeemed,
                    type = "REDEEMED",
                    description = "Redeemed $pointsRedeemed pts for $$pointsDiscount discount on Order #$orderId",
                    orderId = orderId
                )
            )
        }

        groceryDao.insertLoyaltyTransaction(
            LoyaltyTransaction(
                points = calculatedEarnedPoints,
                type = "EARNED",
                description = "Earned $calculatedEarnedPoints pts (${currentProfile.tier.title}) on Order #$orderId",
                orderId = orderId
            )
        )

        // 5. Launch asynchronous background real-time tracking simulation
        startLiveTrackingSimulation(orderId)

        return newOrder
    }

    private fun startLiveTrackingSimulation(orderId: String) {
        externalScope.launch {
            // Milestone 1: Ordered
            delay(4000)
            groceryDao.updateOrderTelemetry(orderId, OrderStatus.PREPARING, 16, 0.25f)

            // Milestone 2: Preparing
            delay(5000)
            groceryDao.updateOrderTelemetry(orderId, OrderStatus.PREPARING, 14, 0.40f)

            // Milestone 3: Out for Delivery
            delay(5000)
            groceryDao.updateOrderTelemetry(orderId, OrderStatus.OUT_FOR_DELIVERY, 10, 0.60f)

            delay(5000)
            groceryDao.updateOrderTelemetry(orderId, OrderStatus.OUT_FOR_DELIVERY, 5, 0.85f)

            // Milestone 4: Delivered
            delay(5000)
            groceryDao.updateOrderTelemetry(orderId, OrderStatus.DELIVERED, 0, 1.0f)
        }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus, etaMinutes: Int, progress: Float) {
        groceryDao.updateOrderTelemetry(orderId, newStatus, etaMinutes, progress)
    }

    suspend fun getProductById(id: String): Product? = groceryDao.getProductById(id)

    // --- Customer Registration & Management ---
    val currentCustomer: Flow<Customer?> = groceryDao.getCustomerProfile()

    suspend fun registerCustomer(
        fullName: String,
        phoneNumber: String,
        address: String,
        area: String = "",
        city: String = "Dadyal, Azad Kashmir"
    ): Customer {
        val cleanPhone = phoneNumber.filter { it.isDigit() }
        val suffix = if (cleanPhone.length >= 4) {
            cleanPhone.takeLast(4)
        } else {
            String.format("%04d", (1000..9999).random())
        }
        val prefixCode = (10..99).random()
        val customerNumber = "DO-$prefixCode$suffix"

        val customer = Customer(
            id = 1,
            customerNumber = customerNumber,
            fullName = fullName.trim(),
            phoneNumber = phoneNumber.trim(),
            deliveryAddress = address.trim(),
            areaOrLandmark = area.trim(),
            city = city.trim(),
            isRegistered = true,
            registeredAt = System.currentTimeMillis()
        )
        groceryDao.insertOrUpdateCustomer(customer)
        return customer
    }

    suspend fun updateCustomerAddress(newAddress: String, area: String = "") {
        val existing = groceryDao.getCustomerProfileSync()
        if (existing != null) {
            val updated = existing.copy(
                deliveryAddress = newAddress.trim(),
                areaOrLandmark = area.trim()
            )
            groceryDao.insertOrUpdateCustomer(updated)
        }
    }

    suspend fun resetCustomer() {
        groceryDao.deleteCustomer()
    }
}
