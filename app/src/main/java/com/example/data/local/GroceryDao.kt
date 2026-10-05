package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CartItem
import com.example.data.model.Customer
import com.example.data.model.LoyaltyProfile
import com.example.data.model.LoyaltyTransaction
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryDao {

    // --- Products ---
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE category = :category ORDER BY name ASC")
    fun getProductsByCategory(category: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: String): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int

    // --- Cart ---
    @Query("SELECT * FROM cart_items ORDER BY addedAt DESC")
    fun getCartItems(): Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateCartItemQuantity(productId: String, quantity: Int)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteCartItem(productId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // --- Loyalty Program ---
    @Query("SELECT * FROM loyalty_profile WHERE id = 1 LIMIT 1")
    fun getLoyaltyProfile(): Flow<LoyaltyProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLoyaltyProfile(profile: LoyaltyProfile)

    @Query("SELECT * FROM loyalty_transactions ORDER BY timestamp DESC")
    fun getLoyaltyTransactions(): Flow<List<LoyaltyTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoyaltyTransaction(transaction: LoyaltyTransaction)

    // --- Orders ---
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    fun getOrderById(orderId: String): Flow<Order?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Update
    suspend fun updateOrder(order: Order)

    @Query("UPDATE orders SET status = :status, etaMinutes = :etaMinutes, trackingProgress = :progress WHERE id = :orderId")
    suspend fun updateOrderTelemetry(orderId: String, status: OrderStatus, etaMinutes: Int, progress: Float)

    // --- Customer Registration & Profile ---
    @Query("SELECT * FROM customer_profile WHERE id = 1 LIMIT 1")
    fun getCustomerProfile(): Flow<Customer?>

    @Query("SELECT * FROM customer_profile WHERE id = 1 LIMIT 1")
    suspend fun getCustomerProfileSync(): Customer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCustomer(customer: Customer)

    @Query("DELETE FROM customer_profile")
    suspend fun deleteCustomer()
}
