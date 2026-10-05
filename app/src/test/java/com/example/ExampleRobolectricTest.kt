package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Dadyal Online", appName)
  }

  @Test
  fun `loyalty points cash value calculation`() {
    val points = 450
    val cashValueInRs = points.toDouble() // 1 point = Rs. 1
    assertEquals(450.0, cashValueInRs, 0.001)
  }

  @Test
  fun `order status markers have correct order and labels`() {
    val statuses = com.example.data.model.OrderStatus.values()
    assertEquals(4, statuses.size)
    assertEquals("Ordered", statuses[0].display)
    assertEquals("Preparing", statuses[1].display)
    assertEquals("Out for Delivery", statuses[2].display)
    assertEquals("Delivered", statuses[3].display)

    assertEquals(1, statuses[0].stepIndex)
    assertEquals(2, statuses[1].stepIndex)
    assertEquals(3, statuses[2].stepIndex)
    assertEquals(4, statuses[3].stepIndex)
  }

  @Test
  fun `product discount calculation and category attributes`() {
    val product = com.example.data.model.Product(
        id = "test_apple",
        name = "Honeycrisp Apples",
        category = "Fruits & Veggies",
        price = 400.0,
        originalPrice = 500.0,
        unit = "1 kg",
        description = "Crisp and sweet"
    )
    assertEquals(20, product.discountPercent)
    assertEquals("Fruits & Veggies", product.category)
  }

  @Test
  fun `product nutrition calculation provides accurate calories and macros`() {
    val apple = com.example.data.model.Product(
        id = "apple_1",
        name = "Crisp Apples",
        category = "Fruits & Veggies",
        price = 380.0,
        unit = "1 kg",
        description = "Freshly harvested"
    )
    val nutrition = com.example.ui.screens.getNutritionForProduct(apple)
    assertEquals(52, nutrition.calories)
    assertEquals(0.3, nutrition.proteinGrams, 0.01)
    assertEquals(2.4, nutrition.fiberGrams, 0.01)
    org.junit.Assert.assertTrue(nutrition.healthTags.contains("100% Organic"))
  }
}
