package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomerRegistrationScreen(
    onRegister: (fullName: String, phone: String, address: String, area: String, city: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var areaOrLandmark by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Dadyal, Azad Kashmir") }

    var addressTouched by remember { mutableStateOf(false) }
    var phoneTouched by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // Validation
    val cleanDigits = remember(phoneNumber) { phoneNumber.filter { it.isDigit() } }
    val isPhoneValid by remember(cleanDigits) {
        derivedStateOf { cleanDigits.length in 10..12 }
    }
    val isAddressValid by remember(deliveryAddress) {
        derivedStateOf { deliveryAddress.trim().length >= 5 }
    }
    val isNameValid by remember(fullName) {
        derivedStateOf { fullName.trim().length >= 2 }
    }
    val canSubmit by remember(isPhoneValid, isAddressValid, isNameValid) {
        derivedStateOf { isPhoneValid && isAddressValid && isNameValid }
    }

    // Dynamic predicted customer ID based on phone digits
    val predictedCustomerNumber by remember(cleanDigits) {
        derivedStateOf {
            if (cleanDigits.length >= 4) {
                "DO-88${cleanDigits.takeLast(4)}"
            } else {
                "DO-88xxxx"
            }
        }
    }

    val popularAreas = listOf(
        "Main Bazaar",
        "Kashmir Road",
        "Thara Mor",
        "Near Civil Hospital",
        "Chattroh",
        "Ratta",
        "Siakh",
        "Kathar"
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .testTag("customer_registration_screen"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. App Header Banner
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(FreshGreenDark, FreshGreenPrimary, Color(0xFF1E5E3A))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🛒", fontSize = 30.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Dadyal Online",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        )

                        Text(
                            text = "Customer Registration & Doorstep Delivery",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Explanation & Specific Customer Number Callout
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AmberLoyaltyContainer.copy(alpha = 0.6f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberLoyalty.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Badge,
                        contentDescription = null,
                        tint = AmberLoyalty,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Specific Customer Number Assigned",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AmberLoyaltyOnContainer
                            )
                        )
                        Text(
                            text = "Every registered customer receives a specific ID ($predictedCustomerNumber) for instant order tracking, VIP rewards, and priority delivery.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = AmberLoyaltyOnContainer.copy(alpha = 0.9f),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Registration Form Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Customer Profile Setup",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = FreshGreenContainer
                ) {
                    Text(
                        text = "Mandatory for Delivery",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = FreshGreenOnContainer,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Full Name Field
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Customer Full Name *") },
                placeholder = { Text("e.g. Muhammad Usman") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = FreshGreenPrimary
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FreshGreenPrimary,
                    focusedLabelColor = FreshGreenPrimary
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_customer_name")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Phone Number Field
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    phoneNumber = it
                    phoneTouched = true
                },
                label = { Text("Mobile Phone Number *") },
                placeholder = { Text("0301 2345678") },
                leadingIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 12.dp, end = 6.dp)
                    ) {
                        Text(text = "🇵🇰", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = FreshGreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                supportingText = {
                    if (phoneTouched && !isPhoneValid) {
                        Text("Please enter a valid 10-11 digit Pakistani phone number", color = BadgeRed)
                    } else {
                        Text("Used to generate your specific customer number & SMS updates")
                    }
                },
                isError = phoneTouched && !isPhoneValid,
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FreshGreenPrimary,
                    focusedLabelColor = FreshGreenPrimary
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_customer_phone")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Delivery Address Field (Mandatory - MUST be filled)
            OutlinedTextField(
                value = deliveryAddress,
                onValueChange = {
                    deliveryAddress = it
                    addressTouched = true
                },
                label = { Text("Complete Delivery Address (Mandatory) *") },
                placeholder = { Text("House / Shop #, Street, Mohallah (e.g. House #14, Main Ward)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = FreshGreenPrimary
                    )
                },
                supportingText = {
                    if (addressTouched && !isAddressValid) {
                        Text("Delivery address is required to enter the app and place orders", color = BadgeRed)
                    } else {
                        Text("Your groceries will be delivered directly to this location")
                    }
                },
                isError = addressTouched && !isAddressValid,
                minLines = 2,
                maxLines = 3,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FreshGreenPrimary,
                    focusedLabelColor = FreshGreenPrimary
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_customer_address")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Area / Landmark Field
            OutlinedTextField(
                value = areaOrLandmark,
                onValueChange = { areaOrLandmark = it },
                label = { Text("Area / Nearby Landmark (Optional)") },
                placeholder = { Text("e.g. Near Jamia Masjid, Thara Mor") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationCity,
                        contentDescription = null,
                        tint = FreshGreenPrimary
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FreshGreenPrimary,
                    focusedLabelColor = FreshGreenPrimary
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_customer_area")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Area Suggestions in Dadyal
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Quick Select Dadyal Area:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    popularAreas.forEach { area ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (areaOrLandmark == area) FreshGreenContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (areaOrLandmark == area) androidx.compose.foundation.BorderStroke(1.dp, FreshGreenPrimary) else null,
                            modifier = Modifier.clickable {
                                areaOrLandmark = area
                            }
                        ) {
                            Text(
                                text = area,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    color = if (areaOrLandmark == area) FreshGreenOnContainer else MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // City Field
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City / Region") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = FreshGreenPrimary
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FreshGreenPrimary,
                    focusedLabelColor = FreshGreenPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_customer_city")
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Requirement Notice if incomplete
            if (!canSubmit) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = BadgeRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Please enter your Name, Phone Number, and Delivery Address to open the app.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 5. Submit Button (Disabled until address & phone are provided)
            Button(
                onClick = {
                    if (canSubmit) {
                        onRegister(fullName, phoneNumber, deliveryAddress, areaOrLandmark, city)
                    } else {
                        phoneTouched = true
                        addressTouched = true
                    }
                },
                enabled = canSubmit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = FreshGreenPrimary,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_registration_btn")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Register & Open Dadyal Online",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Trust badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Fast Delivery in Dadyal • Cash on Delivery • 100% Fresh Guarantee",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CustomerProfileDialog(
    customer: com.example.data.model.Customer,
    onDismiss: () -> Unit,
    onUpdateAddress: (newAddress: String, area: String) -> Unit,
    onResetAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditingAddress by remember { mutableStateOf(false) }
    var editAddressText by remember { mutableStateOf(customer.deliveryAddress) }
    var editAreaText by remember { mutableStateOf(customer.areaOrLandmark) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("customer_profile_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header with Customer Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = customer.fullName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = customer.phoneNumber,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = FreshGreenContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, FreshGreenPrimary)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "CUSTOMER ID",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FreshGreenOnContainer
                                )
                            )
                            Text(
                                text = customer.customerNumber,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = FreshGreenPrimary
                                )
                            )
                        }
                    }
                }

                androidx.compose.material3.HorizontalDivider(color = androidx.compose.material3.DividerDefaults.color.copy(alpha = 0.5f))

                // Delivery Address Section
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Registered Delivery Address",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        androidx.compose.material3.TextButton(onClick = { isEditingAddress = !isEditingAddress }) {
                            Text(if (isEditingAddress) "Cancel" else "Change", fontSize = 12.sp, color = FreshGreenPrimary)
                        }
                    }

                    if (isEditingAddress) {
                        OutlinedTextField(
                            value = editAddressText,
                            onValueChange = { editAddressText = it },
                            label = { Text("House / Street / Mohallah") },
                            modifier = Modifier.fillMaxWidth().testTag("edit_customer_address_input")
                        )
                        OutlinedTextField(
                            value = editAreaText,
                            onValueChange = { editAreaText = it },
                            label = { Text("Area / Landmark") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                if (editAddressText.isNotBlank()) {
                                    onUpdateAddress(editAddressText, editAreaText)
                                    isEditingAddress = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Updated Address")
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = null,
                                    tint = FreshGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = customer.fullFormattedAddress,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    Text(
                                        text = "Doorstep Grocery Delivery Zone",
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

                // Account Reset Option
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            onResetAccount()
                            onDismiss()
                        }
                    ) {
                        Text("Switch Customer Phone / Re-register", color = BadgeRed, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}
