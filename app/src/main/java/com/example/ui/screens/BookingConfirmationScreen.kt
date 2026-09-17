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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.Professional
import com.example.ui.theme.ServoraBorder
import com.example.ui.theme.ServoraCharcoal
import com.example.ui.theme.ServoraCoral
import com.example.ui.theme.ServoraCoralDark
import com.example.ui.theme.ServoraGreen
import com.example.ui.theme.ServoraPeach
import com.example.ui.theme.ServoraPeachLight
import com.example.ui.theme.ServoraStarGold
import com.example.ui.theme.ServoraSubtext

@Composable
fun BookingConfirmationScreen(
    booking: Booking,
    professional: Professional?,
    onAdvanceStatus: (Long, BookingStatus) -> Unit,
    onCancelBooking: (Long) -> Unit,
    onSubmitReview: (String, String, String, Float, String, String) -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var reviewRating by remember { mutableStateOf(5f) }
    var reviewComment by remember { mutableStateOf("") }
    var reviewSubmitted by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(bottom = 16.dp)
    ) {
        // TOP CELEBRATION HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFECFDF5), Color(0xFFD1FAE5))
                    )
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(ServoraGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Booking Confirmed!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = ServoraCharcoal
                )

                Text(
                    text = "Booking Reference: ${booking.bookingCode}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = ServoraGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // START OTP CARD (Anti-fraud safety PIN)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ServoraPeachLight),
            border = androidx.compose.foundation.BorderStroke(1.dp, ServoraCoral)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ServoraCoral),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "OTP",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "START SERVICE OTP",
                            style = MaterialTheme.typography.labelSmall,
                            color = ServoraCoralDark,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Share with pro only when at door",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                            color = ServoraSubtext
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(1.dp, ServoraCoral, RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = booking.startOtp,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            letterSpacing = 4.sp,
                            fontWeight = FontWeight.Black,
                            color = ServoraCoral
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SIMULATED TRACKING STATUS TIMELINE
        Text(
            text = "LIVE STATUS TRACKER",
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
            Column(modifier = Modifier.padding(16.dp)) {
                val statuses = listOf(
                    BookingStatus.CONFIRMED to "Booking Placed",
                    BookingStatus.ASSIGNED to "Professional Assigned",
                    BookingStatus.ON_THE_WAY to "On the Way to Your Home",
                    BookingStatus.ARRIVED to "Arrived at Doorstep",
                    BookingStatus.STARTED to "Service in Progress",
                    BookingStatus.COMPLETED to "Job Finished & Verified"
                )

                val currentIdx = statuses.indexOfFirst { it.first == booking.status }

                statuses.forEachIndexed { index, (st, desc) ->
                    val isDone = currentIdx >= index
                    val isCurrent = currentIdx == index

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isCurrent) ServoraCoral
                                    else if (isDone) ServoraGreen
                                    else Color(0xFFE2E8F0)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 14.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isCurrent) ServoraCoral else if (isDone) ServoraCharcoal else ServoraSubtext
                            )
                            if (isCurrent) {
                                Text(
                                    text = "Current State",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ServoraCoral
                                )
                            }
                        }
                    }

                    if (index < statuses.lastIndex) {
                        Box(
                            modifier = Modifier
                                .padding(start = 11.dp)
                                .width(2.dp)
                                .height(16.dp)
                                .background(if (currentIdx > index) ServoraGreen else Color(0xFFE2E8F0))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status simulation advance button
                if (booking.status != BookingStatus.COMPLETED && booking.status != BookingStatus.CANCELLED) {
                    Button(
                        onClick = { onAdvanceStatus(booking.id, booking.status) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("advance_status_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = ServoraCoral),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Simulate Next Stage (${booking.status.name} ➔ Next)",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ASSIGNED PROFESSIONAL CARD
        professional?.let { pro ->
            Text(
                text = "YOUR ASSIGNED PROFESSIONAL",
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
                            .size(48.dp)
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
                            text = "★ ${pro.rating} • ${pro.completedJobs}+ jobs completed",
                            style = MaterialTheme.typography.labelSmall,
                            color = ServoraCharcoal
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(ServoraGreen)
                            .padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BOOKING DETAILS SUMMARY CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, ServoraBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "SCHEDULE & ADDRESS",
                    style = MaterialTheme.typography.labelSmall,
                    color = ServoraSubtext,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = ServoraCoral,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${booking.scheduledDate} at ${booking.scheduledTime}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = ServoraCharcoal
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = ServoraCoral,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = booking.addressText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ServoraSubtext
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
                        text = "Total Paid / Due",
                        style = MaterialTheme.typography.titleMedium,
                        color = ServoraCharcoal
                    )
                    Text(
                        text = "₹${booking.totalAmount} (${booking.paymentMethod})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ServoraCoral
                        )
                    )
                }
            }
        }

        // COMPLETED REVIEW SUBMISSION SECTION
        if (booking.status == BookingStatus.COMPLETED) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ServoraPeachLight),
                border = androidx.compose.foundation.BorderStroke(1.dp, ServoraCoral)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Rate Your Experience with ${professional?.name ?: "Professional"}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = ServoraCharcoal
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (reviewSubmitted) {
                        Text(
                            text = "Thank you for your rating! Your review is now published.",
                            color = ServoraGreen,
                            style = MaterialTheme.typography.labelMedium
                        )
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            (1..5).forEach { star ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "$star stars",
                                    tint = if (reviewRating >= star) ServoraStarGold else Color.LightGray,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable { reviewRating = star.toFloat() }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = reviewComment,
                            onValueChange = { reviewComment = it },
                            placeholder = { Text("Write a quick review of the service...") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                onSubmitReview(
                                    booking.serviceId,
                                    booking.serviceName,
                                    professional?.name ?: "Rajesh Sharma",
                                    reviewRating,
                                    if (reviewComment.isBlank()) "Excellent service, very courteous and punctual pro!" else reviewComment,
                                    "Verified Customer • Agra"
                                )
                                reviewSubmitted = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ServoraCoral)
                        ) {
                            Text("Submit Review", color = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // CANCEL / HOME ACTIONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onBackToHome,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Back to Home", color = ServoraCharcoal)
            }

            if (booking.status != BookingStatus.COMPLETED && booking.status != BookingStatus.CANCELLED) {
                Button(
                    onClick = {
                        onCancelBooking(booking.id)
                        onBackToHome()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Cancel Job", color = Color.White)
                }
            }
        }
    }
}
