package com.example.ui.screens

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerReview
import com.example.data.model.Professional
import com.example.data.model.ServiceItem
import com.example.data.model.ServicePackage
import com.example.ui.theme.ServoraBorder
import com.example.ui.theme.ServoraCharcoal
import com.example.ui.theme.ServoraCoral
import com.example.ui.theme.ServoraGreen
import com.example.ui.theme.ServoraPeach
import com.example.ui.theme.ServoraPeachLight
import com.example.ui.theme.ServoraStarGold
import com.example.ui.theme.ServoraSubtext

@Composable
fun ServiceDetailScreen(
    service: ServiceItem,
    assignedPro: Professional?,
    reviews: List<CustomerReview>,
    onBackClick: () -> Unit,
    onBookPackage: (ServiceItem, ServicePackage?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPackage by remember {
        mutableStateOf(service.packages.firstOrNull())
    }
    var expandedFaqIndex by remember { mutableStateOf<Int?>(0) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            // Hero Image with Top Bar Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                Image(
                    painter = painterResource(id = service.imageDrawableRes),
                    contentDescription = service.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0x88000000), Color.Transparent, Color(0xAA000000))
                            )
                        )
                )

                // Top Back Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .testTag("service_detail_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(ServoraCoral)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Agra Verified",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                // Bottom badges in hero
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = ServoraStarGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${service.rating} (${service.reviewsCount} reviews)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = ServoraCharcoal
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "⏱ ${service.duration}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }

            // Service Content Section
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.headlineLarge,
                    color = ServoraCharcoal
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = service.subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = ServoraSubtext
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Warranty & Safety Highlights
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ServoraPeachLight)
                        .border(1.dp, ServoraPeach, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = ServoraCoral,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Service Assist Protection Plan",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                            color = ServoraCharcoal
                        )
                        Text(
                            text = "${service.warrantyText} • Trained & background checked staff",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = ServoraSubtext
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Select Package Section
                if (service.packages.isNotEmpty()) {
                    Text(
                        text = "CHOOSE A PACKAGE",
                        style = MaterialTheme.typography.labelSmall,
                        color = ServoraSubtext,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        service.packages.forEach { pkg ->
                            val isSelected = selectedPackage?.id == pkg.id

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { selectedPackage = pkg }
                                    .testTag("package_${pkg.id}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) ServoraPeachLight else MaterialTheme.colorScheme.surface
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) ServoraCoral else ServoraBorder
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedPackage = pkg },
                                        colors = RadioButtonDefaults.colors(selectedColor = ServoraCoral)
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = pkg.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = ServoraCharcoal
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "₹${pkg.originalPrice}",
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                                    ),
                                                    color = ServoraSubtext
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "₹${pkg.price}",
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        color = ServoraCoral,
                                                        fontWeight = FontWeight.Black
                                                    )
                                                )
                                            }
                                        }

                                        Text(
                                            text = pkg.description,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                            color = ServoraSubtext
                                        )

                                        if (pkg.includes.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Includes: " + pkg.includes.joinToString(", "),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = ServoraCharcoal
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Detailed Description
                Text(
                    text = "ABOUT THIS SERVICE",
                    style = MaterialTheme.typography.labelSmall,
                    color = ServoraSubtext,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = ServoraCharcoal
                )

                Spacer(modifier = Modifier.height(20.dp))

                // What is included
                Text(
                    text = "WHAT'S INCLUDED",
                    style = MaterialTheme.typography.labelSmall,
                    color = ServoraGreen,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    service.whatIsIncluded.forEach { item ->
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Included",
                                tint = ServoraGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyMedium,
                                color = ServoraCharcoal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // What is not included
                Text(
                    text = "WHAT'S NOT INCLUDED",
                    style = MaterialTheme.typography.labelSmall,
                    color = ServoraSubtext,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    service.whatIsNotIncluded.forEach { item ->
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Not included",
                                tint = ServoraSubtext,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyMedium,
                                color = ServoraSubtext
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Assigned Professional Highlight
                assignedPro?.let { pro ->
                    Text(
                        text = "VERIFIED PROFESSIONAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = ServoraSubtext,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ServoraBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(ServoraPeach),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = pro.avatarInitials,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ServoraCoral
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = pro.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = ServoraCharcoal
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified",
                                        tint = ServoraGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = pro.specialty,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                    color = ServoraSubtext
                                )
                                Text(
                                    text = "★ ${pro.rating} • ${pro.completedJobs}+ jobs completed • ${pro.experienceYears} yrs exp",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ServoraCharcoal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // FAQs
                if (service.faqs.isNotEmpty()) {
                    Text(
                        text = "FREQUENTLY ASKED QUESTIONS",
                        style = MaterialTheme.typography.labelSmall,
                        color = ServoraSubtext,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        service.faqs.forEachIndexed { index, (q, a) ->
                            val isExpanded = expandedFaqIndex == index
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedFaqIndex = if (isExpanded) null else index
                                    },
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
                                            text = q,
                                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                            color = ServoraCharcoal,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = ServoraSubtext
                                        )
                                    }
                                    if (isExpanded) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        HorizontalDivider(color = ServoraBorder)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = a,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = ServoraSubtext
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // STICKY BOTTOM BOOKING BAR
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val price = selectedPackage?.price ?: service.startingPrice
                    val pkgName = selectedPackage?.name ?: "Standard Service"
                    Text(
                        text = pkgName,
                        style = MaterialTheme.typography.labelSmall,
                        color = ServoraSubtext,
                        maxLines = 1
                    )
                    Text(
                        text = "₹$price",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = ServoraCoral
                        )
                    )
                }

                Button(
                    onClick = { onBookPackage(service, selectedPackage) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ServoraCoral),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("sticky_book_cta")
                ) {
                    Text(
                        text = "Select Date & Time",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}
