package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Countertops
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.PestControl
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shower
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Booking
import com.example.data.model.CustomerReview
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.ui.theme.ServoraBorder
import com.example.ui.theme.ServoraCharcoal
import com.example.ui.theme.ServoraCoral
import com.example.ui.theme.ServoraCoralDark
import com.example.ui.theme.ServoraGreen
import com.example.ui.theme.ServoraHoney
import com.example.ui.theme.ServoraPeach
import com.example.ui.theme.ServoraPeachLight
import com.example.ui.theme.ServoraStarGold
import com.example.ui.theme.ServoraSubtext

@Composable
fun HomeScreen(
    categories: List<ServiceCategory>,
    popularServices: List<ServiceItem>,
    reviews: List<CustomerReview>,
    activeBooking: Booking?,
    selectedCity: String,
    onCategoryClick: (ServiceCategory) -> Unit,
    onServiceClick: (ServiceItem) -> Unit,
    onBookService: (ServiceItem) -> Unit,
    onTrackBookingClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
    onBecomePartnerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 1. ACTIVE BOOKING BANNER (If user has an ongoing service)
        AnimatedVisibility(visible = activeBooking != null) {
            activeBooking?.let { booking ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onTrackBookingClick(booking.id) }
                        .testTag("active_booking_banner"),
                    colors = CardDefaults.cardColors(containerColor = ServoraPeachLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ServoraCoral)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ServoraCoral),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = booking.status.label,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = ServoraCoral
                                    )
                                    Text(
                                        text = " • OTP: ${booking.startOtp}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ServoraCharcoal
                                    )
                                }
                                Text(
                                    text = booking.serviceName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                                    color = ServoraCharcoal,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${booking.scheduledDate} at ${booking.scheduledTime}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                    color = ServoraSubtext
                                )
                            }
                        }

                        Button(
                            onClick = { onTrackBookingClick(booking.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = ServoraCoral),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = "Track",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 2. SEARCH BAR (Prominent, clean, tap to search anything)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, ServoraBorder, RoundedCornerShape(14.dp))
                    .clickable { onSearchClick() }
                    .padding(horizontal = 14.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = ServoraCoral,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Search for AC, cleaning, salon, plumber...",
                    style = MaterialTheme.typography.bodyLarge.copy(color = ServoraSubtext),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. PROMO HERO BANNER (Simple, clean, friendly, no confusing clutter)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(ServoraPeach)
                .clickable {
                    val acCat = categories.find { it.id == "cat_ac" } ?: categories.firstOrNull()
                    if (acCat != null) onCategoryClick(acCat)
                }
                .testTag("featured_category_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ServoraCoral)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "SPECIAL OFFER",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Summer AC & Home Care",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = ServoraCharcoal
                    )

                    Text(
                        text = "Get 20% OFF on your first service with code FIRST20",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = ServoraSubtext
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Book Today",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ServoraCoral
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = ServoraCoral,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AcUnit,
                        contentDescription = "AC Repair",
                        tint = ServoraCoral,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4. CATEGORIES (Clear 4-column grid, instantly understandable)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Services for Your Home",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = ServoraCharcoal
                )
                Text(
                    text = "In $selectedCity",
                    style = MaterialTheme.typography.labelMedium,
                    color = ServoraSubtext
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Display categories in clean 4-column rows
            val chunkedCategories = categories.chunked(4)
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                chunkedCategories.forEach { rowCats ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        rowCats.forEach { category ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onCategoryClick(category) }
                                    .testTag("cat_${category.id}"),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(ServoraPeach)
                                        .border(1.dp, ServoraBorder, RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getCategoryIcon(category.iconName),
                                        contentDescription = category.name,
                                        tint = ServoraCoral,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    ),
                                    color = ServoraCharcoal,
                                    maxLines = 1,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Text(
                                    text = "From ₹${category.startingPrice}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Normal
                                    ),
                                    color = ServoraSubtext
                                )
                            }
                        }

                        // Fill remainder if row is not full
                        if (rowCats.size < 4) {
                            repeat(4 - rowCats.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. POPULAR SERVICES (Horizontal scroller with clear cards)
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Most Booked Services",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = ServoraCharcoal
                    )
                    Text(
                        text = "Popular choices in Agra with 30-day warranty",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = ServoraSubtext
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(popularServices) { service ->
                    Card(
                        modifier = Modifier
                            .width(220.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onServiceClick(service) }
                            .testTag("popular_service_${service.id}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ServoraBorder)
                    ) {
                        Column {
                            Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                                Image(
                                    painter = painterResource(id = service.imageDrawableRes),
                                    contentDescription = service.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = ServoraStarGold,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${service.rating}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = ServoraCharcoal
                                        )
                                    }
                                }
                            }

                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = service.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                    color = ServoraCharcoal,
                                    maxLines = 1
                                )

                                Text(
                                    text = "⏱ ${service.duration}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ServoraSubtext
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "₹${service.startingPrice}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = ServoraCoral,
                                            fontWeight = FontWeight.Black
                                        )
                                    )

                                    Button(
                                        onClick = { onBookService(service) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = ServoraCoral),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text(
                                            text = "Book",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 6. HOW SERVORA WORKS (Simple 3 Steps, zero confusion)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, ServoraBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "How Service Assist Works",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ServoraCharcoal
                )
                Text(
                    text = "Simple, hassle-free booking in 3 easy steps",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = ServoraSubtext
                )

                Spacer(modifier = Modifier.height(14.dp))

                val simpleSteps = listOf(
                    Triple("1", "Select a Service", "Choose from AC repair, cleaning, salon, plumbing or electrical."),
                    Triple("2", "Pick Date & Time", "Select a convenient slot today or for the weekend."),
                    Triple("3", "Expert Arrives & Fixes", "Trained, verified pro arrives on time with warranty.")
                )

                simpleSteps.forEachIndexed { idx, (stepNum, title, desc) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(ServoraPeach),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stepNum,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ServoraCoral
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                color = ServoraCharcoal
                            )
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                color = ServoraSubtext
                            )
                        }
                    }

                    if (idx < simpleSteps.lastIndex) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 7. SERVORA PROMISE (3 TRUST BADGES)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Triple(Icons.Default.Verified, "Police-Verified", "Background checked"),
                Triple(Icons.Default.Security, "30-Day Warranty", "Free rework promise"),
                Triple(Icons.Default.Check, "Fixed Pricing", "No hidden costs")
            ).forEach { (icon, title, subtitle) ->
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = ServoraPeachLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ServoraPeach),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = ServoraCoral,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ServoraCharcoal,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.sp),
                            color = ServoraSubtext,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 8. VERIFIED CUSTOMER REVIEWS
        if (reviews.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Customer Reviews from Agra",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ServoraCharcoal,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reviews.take(4)) { rev ->
                        Card(
                            modifier = Modifier
                                .width(260.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ServoraBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = rev.customerName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                                        color = ServoraCharcoal
                                    )
                                    Row {
                                        repeat(5) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = ServoraStarGold,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = rev.serviceName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ServoraCoral
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "\"${rev.comment}\"",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                    color = ServoraSubtext,
                                    maxLines = 3
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 9. HELPLINE & PARTNER ROW
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, ServoraBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.HeadsetMic,
                        contentDescription = "Help",
                        tint = ServoraCoral,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "24x7 Help",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = ServoraCharcoal
                        )
                        Text(
                            text = "1800-ASSIST-PRO",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                            color = ServoraSubtext
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onBecomePartnerClick() }
                    .testTag("partner_join_btn"),
                colors = CardDefaults.cardColors(containerColor = ServoraPeachLight),
                border = androidx.compose.foundation.BorderStroke(1.dp, ServoraPeach)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Handyman,
                        contentDescription = "Partner",
                        tint = ServoraCoral,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Join as Pro",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = ServoraCharcoal
                        )
                        Text(
                            text = "Earn in Agra",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                            color = ServoraCoral
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

fun getCategoryIcon(iconName: String): ImageVector {
    return when (iconName) {
        "ac_unit" -> Icons.Default.AcUnit
        "cleaning_services" -> Icons.Default.CleaningServices
        "spa" -> Icons.Default.Spa
        "face" -> Icons.Default.Face
        "bolt" -> Icons.Default.Bolt
        "plumbing" -> Icons.Default.Plumbing
        "handyman" -> Icons.Default.Handyman
        "pest_control" -> Icons.Default.PestControl
        "format_paint" -> Icons.Default.FormatPaint
        "water_drop" -> Icons.Default.WaterDrop
        "kitchen" -> Icons.Default.Kitchen
        "shower" -> Icons.Default.Shower
        "countertops" -> Icons.Default.Countertops
        "chair" -> Icons.Default.Chair
        else -> Icons.Default.CleaningServices
    }
}
