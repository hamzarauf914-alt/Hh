package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.ElectricBike
import androidx.compose.material.icons.outlined.Loyalty
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.model.OrderStatus
import com.example.data.repository.FreshCartRepository
import com.example.ui.components.FreshCartTopBar
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.CheckoutPaymentDialog
import com.example.ui.screens.CustomerProfileDialog
import com.example.ui.screens.CustomerRegistrationScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrderTrackingScreen
import com.example.ui.screens.ProductDetailDialog
import com.example.ui.screens.RewardsScreen
import com.example.ui.theme.AmberLoyalty
import com.example.ui.theme.FreshGreenContainer
import com.example.ui.theme.FreshGreenOnContainer
import com.example.ui.theme.FreshGreenPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FreshCartViewModel
import com.example.ui.viewmodel.FreshCartViewModelFactory

enum class AppScreen(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    CATALOG("Catalog", Icons.Default.Storefront, Icons.Outlined.Storefront, "nav_catalog"),
    REWARDS("Rewards", Icons.Default.Loyalty, Icons.Outlined.Loyalty, "nav_rewards"),
    CART("Cart", Icons.Default.ShoppingBag, Icons.Outlined.ShoppingBag, "nav_cart"),
    TRACKING("Tracking", Icons.Default.ElectricBike, Icons.Outlined.ElectricBike, "nav_tracking")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val repository = FreshCartRepository(database.groceryDao())
        val factory = FreshCartViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                val viewModel: FreshCartViewModel = viewModel(factory = factory)
                FreshCartApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FreshCartApp(
    viewModel: FreshCartViewModel,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf(AppScreen.CATALOG) }
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Customer Identity & Registration
    val currentCustomer by viewModel.currentCustomer.collectAsStateWithLifecycle()
    val isCustomerRegistered by viewModel.isCustomerRegistered.collectAsStateWithLifecycle()

    // Mandatory Registration Gate: Customer MUST register with phone number & delivery address
    if (isCustomerRegistered == false || currentCustomer == null) {
        CustomerRegistrationScreen(
            onRegister = { fullName, phone, address, area, city ->
                viewModel.registerCustomer(
                    fullName = fullName,
                    phone = phone,
                    address = address,
                    area = area,
                    city = city
                )
            }
        )
        return
    }

    // Collect View Model States
    val products by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedDietary by viewModel.dietaryFilter.collectAsStateWithLifecycle()

    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val cartCount by viewModel.cartCount.collectAsStateWithLifecycle()
    val cartSubtotal by viewModel.cartSubtotal.collectAsStateWithLifecycle()

    val loyaltyProfile by viewModel.loyaltyProfile.collectAsStateWithLifecycle()
    val loyaltyTransactions by viewModel.loyaltyTransactions.collectAsStateWithLifecycle()

    val pointsToRedeem by viewModel.pointsToRedeem.collectAsStateWithLifecycle()
    val pointsDiscount by viewModel.pointsDiscount.collectAsStateWithLifecycle()
    val appliedCoupon by viewModel.appliedCoupon.collectAsStateWithLifecycle()
    val couponDiscount by viewModel.couponDiscount.collectAsStateWithLifecycle()
    val deliveryFee by viewModel.deliveryFee.collectAsStateWithLifecycle()
    val deliveryTip by viewModel.deliveryTip.collectAsStateWithLifecycle()
    val estimatedTax by viewModel.estimatedTax.collectAsStateWithLifecycle()
    val grandTotal by viewModel.grandTotal.collectAsStateWithLifecycle()
    val projectedPoints by viewModel.projectedPointsToEarn.collectAsStateWithLifecycle()

    val deliveryAddress by viewModel.deliveryAddress.collectAsStateWithLifecycle()
    val deliverySlot by viewModel.deliverySlot.collectAsStateWithLifecycle()
    val paymentMethod by viewModel.paymentMethod.collectAsStateWithLifecycle()
    val paymentState by viewModel.paymentState.collectAsStateWithLifecycle()

    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val selectedTrackingOrder by viewModel.selectedTrackingOrder.collectAsStateWithLifecycle()
    val selectedProductForDetail by viewModel.selectedProductForDetail.collectAsStateWithLifecycle()
    val feedbackSnackbar by viewModel.feedbackSnackbar.collectAsStateWithLifecycle()

    // Show Snackbar notifications
    LaunchedEffect(feedbackSnackbar) {
        feedbackSnackbar?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // BackHandler: return to Catalog screen if on secondary screen
    BackHandler(enabled = currentScreen != AppScreen.CATALOG) {
        currentScreen = AppScreen.CATALOG
    }

    // Active delivery indicator for navigation
    val hasActiveOrder = allOrders.any { it.status != OrderStatus.DELIVERED }

    Scaffold(
        topBar = {
            FreshCartTopBar(
                customerNumber = currentCustomer?.customerNumber,
                pointsBalance = loyaltyProfile?.pointsBalance ?: 0,
                cartCount = cartCount,
                deliveryAddress = deliveryAddress,
                onPointsClick = { currentScreen = AppScreen.REWARDS },
                onCartClick = { currentScreen = AppScreen.CART },
                onCustomerProfileClick = { showProfileDialog = true }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_navigation_bar"),
                containerColor = Color.White,
                tonalElevation = 6.dp
            ) {
                AppScreen.values().forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        label = { Text(screen.title) },
                        icon = {
                            when (screen) {
                                AppScreen.CART -> {
                                    BadgedBox(
                                        badge = {
                                            if (cartCount > 0) {
                                                Badge(
                                                    containerColor = FreshGreenPrimary,
                                                    contentColor = Color.White
                                                ) {
                                                    Text(cartCount.toString())
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                            contentDescription = screen.title
                                        )
                                    }
                                }
                                AppScreen.TRACKING -> {
                                    BadgedBox(
                                        badge = {
                                            if (hasActiveOrder) {
                                                Badge(
                                                    containerColor = AmberLoyalty,
                                                    contentColor = Color.White
                                                ) {
                                                    Text("Live")
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                            contentDescription = screen.title
                                        )
                                    }
                                }
                                else -> {
                                    Icon(
                                        imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                        contentDescription = screen.title
                                    )
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = FreshGreenOnContainer,
                            selectedTextColor = FreshGreenPrimary,
                            indicatorColor = FreshGreenContainer,
                            unselectedIconColor = Color(0xFF6B7280),
                            unselectedTextColor = Color(0xFF6B7280)
                        ),
                        modifier = Modifier.testTag(screen.tag)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.CATALOG -> {
                    CatalogScreen(
                        products = products,
                        cartItems = cartItems,
                        cartCount = cartCount,
                        cartSubtotal = cartSubtotal,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        selectedDietary = selectedDietary,
                        onSearchChange = viewModel::onSearchQueryChange,
                        onCategoryChange = viewModel::onCategorySelect,
                        onDietaryChange = viewModel::onDietarySelect,
                        onProductClick = viewModel::openProductDetail,
                        onAddToCart = viewModel::addToCart,
                        onUpdateQuantity = viewModel::updateCartQuantity,
                        onNavigateToCart = { currentScreen = AppScreen.CART }
                    )
                }

                AppScreen.REWARDS -> {
                    RewardsScreen(
                        loyaltyProfile = loyaltyProfile,
                        transactions = loyaltyTransactions,
                        onNavigateToShop = { currentScreen = AppScreen.CATALOG },
                        onNavigateToCart = { currentScreen = AppScreen.CART }
                    )
                }

                AppScreen.CART -> {
                    CartScreen(
                        cartItems = cartItems,
                        cartSubtotal = cartSubtotal,
                        loyaltyProfile = loyaltyProfile,
                        pointsToRedeem = pointsToRedeem,
                        pointsDiscount = pointsDiscount,
                        appliedCoupon = appliedCoupon,
                        couponDiscount = couponDiscount,
                        deliveryFee = deliveryFee,
                        deliveryTip = deliveryTip,
                        estimatedTax = estimatedTax,
                        grandTotal = grandTotal,
                        projectedPoints = projectedPoints,
                        deliveryAddress = deliveryAddress,
                        deliverySlot = deliverySlot,
                        onUpdateQuantity = viewModel::updateCartQuantity,
                        onRemoveItem = viewModel::removeFromCart,
                        onClearCart = viewModel::clearCart,
                        onSetPointsToRedeem = viewModel::setPointsToRedeem,
                        onClearPoints = viewModel::clearRedeemedPoints,
                        onApplyCoupon = viewModel::applyCoupon,
                        onRemoveCoupon = viewModel::removeCoupon,
                        onSetDeliveryTip = viewModel::setDeliveryTip,
                        onSetDeliverySlot = viewModel::setDeliverySlot,
                        onProceedToCheckout = { showCheckoutDialog = true },
                        onNavigateToShop = { currentScreen = AppScreen.CATALOG }
                    )
                }

                AppScreen.TRACKING -> {
                    OrderTrackingScreen(
                        currentOrder = selectedTrackingOrder,
                        allOrders = allOrders,
                        onSelectOrder = viewModel::selectOrderForTracking,
                        onReorder = { order ->
                            viewModel.reorderItems(order)
                            currentScreen = AppScreen.CART
                        },
                        onNavigateToShop = { currentScreen = AppScreen.CATALOG },
                        onAdvanceStatus = viewModel::advanceOrderStatus,
                        onSetStatus = viewModel::setOrderStatus
                    )
                }
            }

            // Checkout Payment Dialog
            if (showCheckoutDialog) {
                CheckoutPaymentDialog(
                    totalAmount = grandTotal,
                    paymentState = paymentState,
                    selectedPaymentMethod = paymentMethod,
                    onPaymentMethodChange = viewModel::setPaymentMethod,
                    onDismiss = { showCheckoutDialog = false },
                    onConfirmPayment = {
                        viewModel.processCheckout { orderId ->
                            showCheckoutDialog = false
                            currentScreen = AppScreen.TRACKING
                        }
                    }
                )
            }

            // Product Detail Dialog
            selectedProductForDetail?.let { product ->
                val qtyInCart = cartItems.find { it.product.id == product.id }?.quantity ?: 0
                ProductDetailDialog(
                    product = product,
                    quantityInCart = qtyInCart,
                    onAddToCart = { qty ->
                        viewModel.updateCartQuantity(product.id, qty)
                    },
                    onDismiss = viewModel::closeProductDetail
                )
            }

            // Customer Profile Dialog
            if (showProfileDialog && currentCustomer != null) {
                CustomerProfileDialog(
                    customer = currentCustomer!!,
                    onDismiss = { showProfileDialog = false },
                    onUpdateAddress = { newAddress, area ->
                        viewModel.updateDeliveryAddress(newAddress, area)
                    },
                    onResetAccount = {
                        viewModel.resetCustomerRegistration()
                    }
                )
            }
        }
    }
}
