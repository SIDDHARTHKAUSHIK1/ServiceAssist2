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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.model.Offer
import com.example.data.model.SavedAddress
import com.example.data.model.ServiceItem
import com.example.data.model.ServicePackage
import com.example.ui.theme.ServoraBorder
import com.example.ui.theme.ServoraCharcoal
import com.example.ui.theme.ServoraCoral
import com.example.ui.theme.ServoraGreen
import com.example.ui.theme.ServoraPeach
import com.example.ui.theme.ServoraPeachLight
import com.example.ui.theme.ServoraSubtext

@Composable
fun BookingFlowScreen(
    service: ServiceItem,
    selectedPackage: ServicePackage?,
    savedAddresses: List<SavedAddress>,
    availableOffers: List<Offer>,
    appliedOffer: Offer?,
    selectedAddress: SavedAddress?,
    selectedDate: String,
    selectedTimeSlot: String,
    paymentMethod: String,
    onDateChange: (String) -> Unit,
    onTimeSlotChange: (String) -> Unit,
    onAddressChange: (SavedAddress) -> Unit,
    onAddNewAddress: (String, String, String, String) -> Unit,
    onApplyPromo: (String) -> Boolean,
    onRemovePromo: () -> Unit,
    onPaymentMethodChange: (String) -> Unit,
    onConfirmBooking: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateOptions = listOf("Today", "Tomorrow", "Wed, 17 Sep", "Thu, 18 Sep", "Fri, 19 Sep", "Sat, 20 Sep")
    val timeOptions = listOf("09:00 AM", "11:30 AM", "02:00 PM", "04:30 PM", "06:30 PM")

    var couponInput by remember { mutableStateOf("") }
    var couponError by remember { mutableStateOf<String?>(null) }
    var showAddAddressForm by remember { mutableStateOf(false) }

    // New Address Form Fields
    var newAddressTitle by remember { mutableStateOf("Home") }
    var newAddressText by remember { mutableStateOf("") }
    var newAddressLocality by remember { mutableStateOf("Taj Nagri") }
    var newAddressLandmark by remember { mutableStateOf("") }

    val basePrice = selectedPackage?.price ?: service.startingPrice
    val discount = appliedOffer?.let { off ->
        if (off.percentageDiscount > 0) (basePrice * off.percentageDiscount) / 100
        else off.flatDiscount
    } ?: 0
    val finalTotal = (basePrice - discount).coerceAtLeast(49)

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 110.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ServoraCharcoal
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Schedule Booking",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = ServoraCharcoal
                    )
                    Text(
                        text = "${service.name} • ${selectedPackage?.name ?: "Standard"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ServoraSubtext,
                        maxLines = 1
                    )
                }
            }

            HorizontalDivider(color = ServoraBorder)

            // STEP 1: DATE SELECTION
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(ServoraCoral),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "1",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SELECT DATE",
                        style = MaterialTheme.typography.labelSmall,
                        color = ServoraCharcoal,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(dateOptions) { date ->
                        val isSelected = selectedDate == date
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) ServoraCoral else MaterialTheme.colorScheme.surface)
                                .border(
                                    1.dp,
                                    if (isSelected) ServoraCoral else ServoraBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onDateChange(date) }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                .testTag("date_chip_$date")
                        ) {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Color.White else ServoraCharcoal
                            )
                        }
                    }
                }
            }

            // STEP 2: TIME SLOT SELECTION
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(ServoraCoral),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "2",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SELECT TIME SLOT",
                        style = MaterialTheme.typography.labelSmall,
                        color = ServoraCharcoal,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(timeOptions) { slot ->
                        val isSelected = selectedTimeSlot == slot
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) ServoraPeach else MaterialTheme.colorScheme.surface)
                                .border(
                                    1.dp,
                                    if (isSelected) ServoraCoral else ServoraBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onTimeSlotChange(slot) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                .testTag("time_chip_$slot")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = if (isSelected) ServoraCoral else ServoraSubtext,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = slot,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) ServoraCoral else ServoraCharcoal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // STEP 3: ADDRESS SELECTION
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(ServoraCoral),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "3",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SERVICE ADDRESS (AGRA)",
                            style = MaterialTheme.typography.labelSmall,
                            color = ServoraCharcoal,
                            letterSpacing = 1.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { showAddAddressForm = !showAddAddressForm },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = ServoraCoral
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add New", color = ServoraCoral, style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Saved Addresses List
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    savedAddresses.forEach { addr ->
                        val isAddrSelected = selectedAddress?.id == addr.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAddressChange(addr) }
                                .testTag("address_item_${addr.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isAddrSelected) ServoraPeachLight else MaterialTheme.colorScheme.surface
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isAddrSelected) ServoraCoral else ServoraBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isAddrSelected,
                                    onClick = { onAddressChange(addr) },
                                    colors = RadioButtonDefaults.colors(selectedColor = ServoraCoral)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = addr.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                        color = ServoraCharcoal
                                    )
                                    Text(
                                        text = "${addr.fullAddress}, ${addr.locality}, ${addr.city}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                        color = ServoraSubtext
                                    )
                                }
                            }
                        }
                    }
                }

                // Add New Address Collapsible Form
                AnimatedVisibility(visible = showAddAddressForm) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ServoraBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Enter Doorstep Address in Agra",
                                style = MaterialTheme.typography.titleMedium,
                                color = ServoraCharcoal
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newAddressTitle,
                                onValueChange = { newAddressTitle = it },
                                label = { Text("Label (e.g. Home, Office, Studio)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = newAddressText,
                                onValueChange = { newAddressText = it },
                                label = { Text("Flat / House No / Street Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = newAddressLocality,
                                onValueChange = { newAddressLocality = it },
                                label = { Text("Locality (e.g. Taj Nagri, Fatehabad Rd, Sanjay Place)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = newAddressLandmark,
                                onValueChange = { newAddressLandmark = it },
                                label = { Text("Nearby Landmark") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    if (newAddressText.isNotBlank()) {
                                        onAddNewAddress(
                                            newAddressTitle,
                                            newAddressText,
                                            newAddressLocality,
                                            newAddressLandmark
                                        )
                                        showAddAddressForm = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ServoraCoral),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Save & Use This Address", color = Color.White)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // STEP 4: OFFERS & COUPONS
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(ServoraCoral),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "4",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "COUPONS & OFFERS",
                        style = MaterialTheme.typography.labelSmall,
                        color = ServoraCharcoal,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (appliedOffer != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ServoraGreen)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = ServoraGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "${appliedOffer.code} Applied! Saved ₹$discount",
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                        color = ServoraCharcoal
                                    )
                                    Text(
                                        text = appliedOffer.discountDescription,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                                        color = ServoraSubtext
                                    )
                                }
                            }

                            Text(
                                text = "Remove",
                                color = Color.Red,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.clickable { onRemovePromo() }
                            )
                        }
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = couponInput,
                            onValueChange = {
                                couponInput = it
                                couponError = null
                            },
                            placeholder = { Text("Enter coupon (e.g. FIRST20)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val success = onApplyPromo(couponInput)
                                if (!success) couponError = "Invalid code. Try FIRST20 or CLEAN100"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ServoraCoral),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Apply", color = Color.White)
                        }
                    }

                    if (couponError != null) {
                        Text(
                            text = couponError!!,
                            color = Color.Red,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Available coupons chips
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(availableOffers) { offer ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ServoraPeach)
                                    .clickable {
                                        couponInput = offer.code
                                        onApplyPromo(offer.code)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalOffer,
                                        contentDescription = null,
                                        tint = ServoraCoral,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${offer.code} (${offer.title})",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ServoraCoral
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // STEP 5: PAYMENT METHOD
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(ServoraCoral),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "5",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PAYMENT METHOD",
                        style = MaterialTheme.typography.labelSmall,
                        color = ServoraCharcoal,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val paymentOptions = listOf(
                    Triple("Cash after service", "Pay verified professional upon 100% satisfaction", Icons.Default.Money),
                    Triple("UPI (Google Pay / PhonePe / Paytm)", "Instant payment with UPI app", Icons.Default.Payments),
                    Triple("Credit / Debit Card", "Visa, Mastercard, RuPay accepted", Icons.Default.CreditCard)
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    paymentOptions.forEach { (title, subtitle, icon) ->
                        val isSelected = paymentMethod == title
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPaymentMethodChange(title) }
                                .testTag("payment_$title"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) ServoraPeachLight else MaterialTheme.colorScheme.surface
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) ServoraCoral else ServoraBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onPaymentMethodChange(title) },
                                    colors = RadioButtonDefaults.colors(selectedColor = ServoraCoral)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = ServoraCoral,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                        color = ServoraCharcoal
                                    )
                                    Text(
                                        text = subtitle,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                                        color = ServoraSubtext
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BILL BREAKDOWN SUMMARY
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, ServoraBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "BILL SUMMARY",
                    style = MaterialTheme.typography.labelSmall,
                    color = ServoraSubtext,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Item Total (${selectedPackage?.name ?: service.name})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ServoraCharcoal
                    )
                    Text(
                        text = "₹$basePrice",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ServoraCharcoal
                    )
                }

                if (discount > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Promo Discount (${appliedOffer?.code})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ServoraGreen
                        )
                        Text(
                            text = "-₹$discount",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ServoraGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Safety & Insurance Fee in Agra",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ServoraSubtext
                    )
                    Text(
                        text = "FREE",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = ServoraGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = ServoraBorder)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Amount Payable",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = ServoraCharcoal
                    )
                    Text(
                        text = "₹$finalTotal",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = ServoraCoral
                        )
                    )
                }
            }
        }

        // CONFIRM BUTTON BAR
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 14.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$selectedDate at $selectedTimeSlot",
                            style = MaterialTheme.typography.labelSmall,
                            color = ServoraSubtext
                        )
                        Text(
                            text = "₹$finalTotal Total",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = ServoraCoral
                            )
                        )
                    }

                    Button(
                        onClick = onConfirmBooking,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ServoraCoral),
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("confirm_booking_btn")
                    ) {
                        Text(
                            text = "Book Now (₹$finalTotal)",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
