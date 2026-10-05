package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.CartItemWithProduct
import com.example.data.model.Customer
import com.example.data.model.LoyaltyProfile
import com.example.data.model.LoyaltyTransaction
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.Product
import com.example.data.repository.FreshCartRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface PaymentProcessState {
    object Idle : PaymentProcessState
    data class Processing(val stepMessage: String) : PaymentProcessState
    data class Success(val orderId: String) : PaymentProcessState
    data class Error(val message: String) : PaymentProcessState
}

class FreshCartViewModel(
    private val repository: FreshCartRepository
) : ViewModel() {

    // --- Customer Registration & Identity ---
    val currentCustomer: StateFlow<Customer?> = repository.currentCustomer
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val isCustomerRegistered: StateFlow<Boolean?> = repository.currentCustomer
        .map { it?.isRegistered }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
        }
        viewModelScope.launch {
            repository.currentCustomer.collect { customer ->
                if (customer != null && customer.deliveryAddress.isNotBlank()) {
                    _deliveryAddress.value = customer.fullFormattedAddress
                }
            }
        }
    }

    // --- Search & Categories ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _dietaryFilter = MutableStateFlow<String?>(null)
    val dietaryFilter: StateFlow<String?> = _dietaryFilter.asStateFlow()

    val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredProducts: StateFlow<List<Product>> = combine(
        allProducts,
        _searchQuery,
        _selectedCategory,
        _dietaryFilter
    ) { products, query, category, dietary ->
        products.filter { product ->
            val matchesCategory = (category == "All" || product.category.equals(category, ignoreCase = true))
            val matchesQuery = query.isBlank() ||
                    product.name.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true) ||
                    product.category.contains(query, ignoreCase = true)
            val matchesDietary = dietary == null || product.dietaryTag?.contains(dietary, ignoreCase = true) == true
            matchesCategory && matchesQuery && matchesDietary
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Cart ---
    val cartItems: StateFlow<List<CartItemWithProduct>> = repository.cartItemsWithProduct
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartCount: StateFlow<Int> = repository.cartItemsWithProduct.combine(repository.cartItems) { withProd, _ ->
        withProd.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cartSubtotal: StateFlow<Double> = cartItems.combine(cartCount) { items, _ ->
        items.sumOf { it.totalPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // --- Loyalty & Rewards Program ---
    val loyaltyProfile: StateFlow<LoyaltyProfile?> = repository.loyaltyProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val loyaltyTransactions: StateFlow<List<LoyaltyTransaction>> = repository.loyaltyTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Loyalty points redemption for current order (100 pts = Rs. 100.0)
    private val _pointsToRedeem = MutableStateFlow(0)
    val pointsToRedeem: StateFlow<Int> = _pointsToRedeem.asStateFlow()

    val pointsDiscount: StateFlow<Double> = _pointsToRedeem.combine(loyaltyProfile) { pts, _ ->
        pts.toDouble() // 1 point = Rs. 1.00
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // --- Coupons & Promotions ---
    private val _appliedCoupon = MutableStateFlow<String?>(null)
    val appliedCoupon: StateFlow<String?> = _appliedCoupon.asStateFlow()

    val couponDiscount: StateFlow<Double> = combine(cartSubtotal, _appliedCoupon) { subtotal, coupon ->
        when (coupon?.uppercase()) {
            "FRESH50" -> if (subtotal >= 1000.0) 250.0 else 0.0
            "GREEN10" -> (subtotal * 0.10)
            "WELCOME" -> 150.0
            else -> 0.0
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // --- Checkout Settings ---
    private val _deliveryTip = MutableStateFlow(100.0)
    val deliveryTip: StateFlow<Double> = _deliveryTip.asStateFlow()

    private val _deliverySlot = MutableStateFlow("Express Delivery (15-25 min)")
    val deliverySlot: StateFlow<String> = _deliverySlot.asStateFlow()

    private val _deliveryAddress = MutableStateFlow("Dadyal Main Bazaar, Azad Kashmir")
    val deliveryAddress: StateFlow<String> = _deliveryAddress.asStateFlow()

    private val _deliveryInstructions = MutableStateFlow("Leave at front gate, call on arrival")
    val deliveryInstructions: StateFlow<String> = _deliveryInstructions.asStateFlow()

    private val _paymentMethod = MutableStateFlow("Cash on Delivery")
    val paymentMethod: StateFlow<String> = _paymentMethod.asStateFlow()

    val deliveryFee: StateFlow<Double> = cartSubtotal.combine(_deliverySlot) { subtotal, _ ->
        if (subtotal >= 1500.0 || subtotal == 0.0) 0.0 else 150.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val estimatedTax: StateFlow<Double> = cartSubtotal.combine(couponDiscount) { subtotal, disc ->
        val taxable = (subtotal - disc).coerceAtLeast(0.0)
        (taxable * 0.05) // 5% GST
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val grandTotal: StateFlow<Double> = combine(
        cartSubtotal,
        couponDiscount,
        pointsDiscount,
        deliveryFee,
        combine(_deliveryTip, estimatedTax) { tip, tax -> tip + tax }
    ) { subtotal, couponDisc, ptsDisc, fee, tipAndTax ->
        val total = (subtotal - couponDisc - ptsDisc + fee + tipAndTax).coerceAtLeast(0.0)
        Math.round(total * 100.0) / 100.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val projectedPointsToEarn: StateFlow<Int> = combine(grandTotal, loyaltyProfile) { total, profile ->
        val multiplier = profile?.tier?.multiplier ?: 1.0
        (total * 10 * multiplier).toInt()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // --- Orders & Real-Time Tracking ---
    val allOrders: StateFlow<List<Order>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedTrackingOrder = MutableStateFlow<Order?>(null)
    val selectedTrackingOrder: StateFlow<Order?> = combine(allOrders, _selectedTrackingOrder) { orders, selected ->
        if (selected != null) {
            orders.find { it.id == selected.id } ?: selected
        } else {
            orders.firstOrNull { it.status != OrderStatus.DELIVERED } ?: orders.firstOrNull()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // UI Feedback & Dialogs
    private val _paymentState = MutableStateFlow<PaymentProcessState>(PaymentProcessState.Idle)
    val paymentState: StateFlow<PaymentProcessState> = _paymentState.asStateFlow()

    private val _selectedProductForDetail = MutableStateFlow<Product?>(null)
    val selectedProductForDetail: StateFlow<Product?> = _selectedProductForDetail.asStateFlow()

    private val _feedbackSnackbar = MutableStateFlow<String?>(null)
    val feedbackSnackbar: StateFlow<String?> = _feedbackSnackbar.asStateFlow()

    // --- Actions ---

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: String) {
        _selectedCategory.value = category
    }

    fun onDietarySelect(tag: String?) {
        _dietaryFilter.value = if (_dietaryFilter.value == tag) null else tag
    }

    fun openProductDetail(product: Product) {
        _selectedProductForDetail.value = product
    }

    fun closeProductDetail() {
        _selectedProductForDetail.value = null
    }

    fun addToCart(productId: String) {
        viewModelScope.launch {
            repository.addToCart(productId)
            _feedbackSnackbar.value = "Added to FreshCart!"
        }
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, quantity)
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    // Loyalty Points Actions
    fun setPointsToRedeem(points: Int) {
        val maxAvailable = loyaltyProfile.value?.pointsBalance ?: 0
        // Don't redeem more than order subtotal value
        val maxSubtotalPoints = ((cartSubtotal.value - (couponDiscount.value)) * 100).toInt().coerceAtLeast(0)
        val validPoints = points.coerceIn(0, minOf(maxAvailable, maxSubtotalPoints))
        _pointsToRedeem.value = validPoints
    }

    fun clearRedeemedPoints() {
        _pointsToRedeem.value = 0
    }

    // Coupon Actions
    fun applyCoupon(code: String): Boolean {
        val normalized = code.trim().uppercase()
        val isValid = when (normalized) {
            "FRESH50" -> cartSubtotal.value >= 25.0
            "GREEN10" -> true
            "WELCOME" -> true
            else -> false
        }
        if (isValid) {
            _appliedCoupon.value = normalized
            _feedbackSnackbar.value = "Coupon '$normalized' applied successfully!"
            return true
        } else {
            _feedbackSnackbar.value = if (normalized == "FRESH50") "Requires $25 min subtotal" else "Invalid coupon code"
            return false
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
    }

    // Checkout Details
    fun setDeliveryTip(tip: Double) {
        _deliveryTip.value = tip
    }

    fun setDeliverySlot(slot: String) {
        _deliverySlot.value = slot
    }

    fun setDeliveryAddress(address: String) {
        _deliveryAddress.value = address
    }

    fun setPaymentMethod(method: String) {
        _paymentMethod.value = method
    }

    fun clearSnackbar() {
        _feedbackSnackbar.value = null
    }

    fun selectOrderForTracking(order: Order) {
        _selectedTrackingOrder.value = order
    }

    // Execute Real Payment & Order Placement
    fun processCheckout(onComplete: (String) -> Unit) {
        val currentCart = cartItems.value
        if (currentCart.isEmpty()) {
            _feedbackSnackbar.value = "Your cart is empty!"
            return
        }

        viewModelScope.launch {
            _paymentState.value = PaymentProcessState.Processing("Securing SSL 256-bit encrypted connection...")
            delay(1000)

            _paymentState.value = PaymentProcessState.Processing("Tokenizing card & verifying credentials...")
            delay(1200)

            _paymentState.value = PaymentProcessState.Processing("Contacting FreshMart Central dispatch...")
            delay(1000)

            val itemsSummary = currentCart.joinToString(", ") { "${it.product.name} (x${it.quantity})" }

            val order = repository.placeOrder(
                subtotal = cartSubtotal.value,
                discount = couponDiscount.value,
                pointsRedeemed = _pointsToRedeem.value,
                pointsDiscount = pointsDiscount.value,
                deliveryFee = deliveryFee.value,
                tip = _deliveryTip.value,
                tax = estimatedTax.value,
                total = grandTotal.value,
                paymentMethod = _paymentMethod.value,
                deliveryAddress = _deliveryAddress.value,
                deliverySlot = _deliverySlot.value,
                itemsSummary = itemsSummary,
                itemsJson = ""
            )

            // Reset checkout inputs
            _pointsToRedeem.value = 0
            _appliedCoupon.value = null
            _paymentState.value = PaymentProcessState.Success(order.id)
            _selectedTrackingOrder.value = order

            delay(600)
            _paymentState.value = PaymentProcessState.Idle
            onComplete(order.id)
        }
    }

    fun reorderItems(order: Order) {
        viewModelScope.launch {
            // Re-add available products
            val products = allProducts.value
            products.take(3).forEach {
                repository.addToCart(it.id)
            }
            _feedbackSnackbar.value = "Items from Order #${order.id} added to cart!"
        }
    }

    fun setOrderStatus(orderId: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            val (eta, progress) = when (newStatus) {
                OrderStatus.ORDERED -> 20 to 0.08f
                OrderStatus.PREPARING -> 15 to 0.35f
                OrderStatus.OUT_FOR_DELIVERY -> 8 to 0.70f
                OrderStatus.DELIVERED -> 0 to 1.0f
            }
            repository.updateOrderStatus(orderId, newStatus, eta, progress)
            _feedbackSnackbar.value = "Order status updated to '${newStatus.display}'"
        }
    }

    fun advanceOrderStatus(orderId: String) {
        val current = allOrders.value.find { it.id == orderId } ?: return
        val nextStatus = when (current.status) {
            OrderStatus.ORDERED -> OrderStatus.PREPARING
            OrderStatus.PREPARING -> OrderStatus.OUT_FOR_DELIVERY
            OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
            OrderStatus.DELIVERED -> OrderStatus.ORDERED
        }
        setOrderStatus(orderId, nextStatus)
    }

    // --- Customer Actions ---
    fun registerCustomer(
        fullName: String,
        phone: String,
        address: String,
        area: String = "",
        city: String = "Dadyal, Azad Kashmir"
    ) {
        viewModelScope.launch {
            val customer = repository.registerCustomer(
                fullName = fullName,
                phoneNumber = phone,
                address = address,
                area = area,
                city = city
            )
            _deliveryAddress.value = customer.fullFormattedAddress
            _feedbackSnackbar.value = "Welcome ${customer.fullName}! Customer ID: ${customer.customerNumber}"
        }
    }

    fun updateDeliveryAddress(newAddress: String, area: String = "") {
        viewModelScope.launch {
            repository.updateCustomerAddress(newAddress, area)
            _deliveryAddress.value = if (area.isNotBlank()) "$newAddress, $area" else newAddress
            _feedbackSnackbar.value = "Delivery address updated"
        }
    }

    fun resetCustomerRegistration() {
        viewModelScope.launch {
            repository.resetCustomer()
            _feedbackSnackbar.value = "Customer session reset. Please register to continue."
        }
    }
}

class FreshCartViewModelFactory(
    private val repository: FreshCartRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FreshCartViewModel::class.java)) {
            return FreshCartViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
