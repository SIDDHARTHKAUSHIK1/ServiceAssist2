package com.example.ui.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.ui.theme.ServoraBorder
import com.example.ui.theme.ServoraCharcoal
import com.example.ui.theme.ServoraCoral
import com.example.ui.theme.ServoraGreen
import com.example.ui.theme.ServoraPeach
import com.example.ui.theme.ServoraSubtext

@Composable
fun BookingsListScreen(
    bookings: List<Booking>,
    onSelectBooking: (Booking) -> Unit,
    onBookAgain: (String) -> Unit,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Active / Upcoming, 1: Past / Completed

    val activeBookings = bookings.filter {
        it.status != BookingStatus.COMPLETED && it.status != BookingStatus.CANCELLED
    }
    val pastBookings = bookings.filter {
        it.status == BookingStatus.COMPLETED || it.status == BookingStatus.CANCELLED
    }

    val displayList = if (selectedTab == 0) activeBookings else pastBookings

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "My Bookings",
                style = MaterialTheme.typography.headlineLarge,
                color = ServoraCharcoal
            )
            Text(
                text = "Manage your appointments & service history",
                style = MaterialTheme.typography.bodyMedium,
                color = ServoraSubtext
            )

            Spacer(modifier = Modifier.height(14.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = ServoraCoral,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ServoraCoral
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Active & Upcoming (${activeBookings.size})",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (selectedTab == 0) ServoraCoral else ServoraSubtext
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Past & Completed (${pastBookings.size})",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (selectedTab == 1) ServoraCoral else ServoraSubtext
                        )
                    }
                )
            }
        }

        if (displayList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = ServoraCoral,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (selectedTab == 0) "No active bookings right now" else "No past bookings yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = ServoraCharcoal
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Book verified AC repair, home cleaning, salon or electrical services in Agra.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ServoraSubtext,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onExploreClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ServoraCoral)
                ) {
                    Text("Explore Services", color = Color.White)
                }
            }
        } else {
            LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(displayList) { booking ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onSelectBooking(booking) }
                            .testTag("booking_card_${booking.bookingCode}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ServoraBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = booking.bookingCode,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ServoraSubtext
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when (booking.status) {
                                                BookingStatus.COMPLETED -> Color(0xFFECFDF5)
                                                BookingStatus.CANCELLED -> Color(0xFFFEF2F2)
                                                else -> ServoraPeach
                                            }
                                        )
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = booking.status.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = when (booking.status) {
                                                BookingStatus.COMPLETED -> ServoraGreen
                                                BookingStatus.CANCELLED -> Color(0xFFEF4444)
                                                else -> ServoraCoral
                                            }
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = booking.serviceName,
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 17.sp),
                                color = ServoraCharcoal
                            )
                            Text(
                                text = booking.packageName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = ServoraSubtext
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = ServoraCoral,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${booking.scheduledDate} • ${booking.scheduledTime}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = ServoraCharcoal
                                    )
                                }

                                if (booking.status != BookingStatus.COMPLETED && booking.status != BookingStatus.CANCELLED) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Key,
                                            contentDescription = null,
                                            tint = ServoraCoral,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "OTP: ${booking.startOtp}",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                color = ServoraCoral
                                            )
                                        )
                                    }
                                }
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
                                    text = "₹${booking.totalAmount}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ServoraCoral
                                    )
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (booking.status != BookingStatus.COMPLETED && booking.status != BookingStatus.CANCELLED) {
                                        Button(
                                            onClick = { onSelectBooking(booking) },
                                            colors = ButtonDefaults.buttonColors(containerColor = ServoraCoral),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Track Status", color = Color.White)
                                        }
                                    } else {
                                        OutlinedButton(
                                            onClick = { onSelectBooking(booking) },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("View Details", color = ServoraCharcoal)
                                        }
                                        Button(
                                            onClick = { onBookAgain(booking.serviceId) },
                                            colors = ButtonDefaults.buttonColors(containerColor = ServoraCoral),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Book Again", color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
