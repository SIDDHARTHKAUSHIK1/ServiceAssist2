package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.theme.ServoraCharcoal
import com.example.ui.theme.ServoraCoral
import com.example.ui.theme.ServoraPeach

@Composable
fun ServoraTopBar(
    selectedCity: String,
    selectedLocality: String,
    userRole: UserRole,
    onLocationClick: () -> Unit,
    onSearchClick: () -> Unit,
    onRoleToggleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Logo & City
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Service Assist",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            ),
                            color = ServoraCharcoal
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(ServoraCoral)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Location Selector Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(ServoraPeach)
                            .clickable { onLocationClick() }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                            .testTag("location_pill"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = ServoraCoral,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$selectedCity • $selectedLocality",
                            style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                            color = ServoraCharcoal,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select City",
                            tint = ServoraCharcoal,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Action Icons: Quick Search + Mode Switcher
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onSearchClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("top_search_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Services",
                            tint = ServoraCharcoal,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Mode Toggle (Customer / Pro / Admin view)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                when (userRole) {
                                    UserRole.CUSTOMER -> MaterialTheme.colorScheme.surfaceVariant
                                    UserRole.PROFESSIONAL -> Color(0xFFE0F2FE)
                                    UserRole.ADMIN -> Color(0xFFFEF3C7)
                                }
                            )
                            .clickable { onRoleToggleClick() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("role_switch_btn"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (userRole) {
                                UserRole.CUSTOMER -> Icons.Default.Person
                                UserRole.PROFESSIONAL -> Icons.Default.Handyman
                                UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                            },
                            contentDescription = "User Mode",
                            tint = when (userRole) {
                                UserRole.CUSTOMER -> ServoraCharcoal
                                UserRole.PROFESSIONAL -> Color(0xFF0369A1)
                                UserRole.ADMIN -> Color(0xFFB45309)
                            },
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (userRole) {
                                UserRole.CUSTOMER -> "Customer"
                                UserRole.PROFESSIONAL -> "Partner"
                                UserRole.ADMIN -> "Admin"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = ServoraCharcoal
                        )
                    }
                }
            }
        }
    }
}
