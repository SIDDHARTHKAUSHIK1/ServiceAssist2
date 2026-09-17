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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.model.SavedAddress
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.example.data.remote.supabase.SupabaseConfig
import com.example.data.remote.supabase.SupabaseSyncState
import com.example.ui.theme.ServoraBorder
import com.example.ui.theme.ServoraCharcoal
import com.example.ui.theme.ServoraCoral
import com.example.ui.theme.ServoraGreen
import com.example.ui.theme.ServoraHoney
import com.example.ui.theme.ServoraPeach
import com.example.ui.theme.ServoraPeachLight
import com.example.ui.theme.ServoraStarGold
import com.example.ui.theme.ServoraSubtext

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    savedAddresses: List<SavedAddress>,
    onSwitchRole: (UserRole) -> Unit,
    onLocationClick: () -> Unit,
    onDeleteAddress: (Long) -> Unit,
    onAddNewAddress: (String, String, String, String) -> Unit,
    supabaseSyncState: SupabaseSyncState = SupabaseSyncState(),
    onSyncWithSupabase: () -> Unit = {},
    onSeedSupabaseDemoData: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isPartnerOnline by remember { mutableStateOf(true) }
    var showAddAddress by remember { mutableStateOf(false) }

    var addrTitle by remember { mutableStateOf("Home") }
    var addrText by remember { mutableStateOf("") }
    var addrLocality by remember { mutableStateOf("Taj Nagri") }
    var addrLandmark by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .padding(bottom = 16.dp)
    ) {
        // User Profile Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, ServoraBorder)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(ServoraPeach),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userProfile.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = ServoraCoral
                        )
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userProfile.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = ServoraCharcoal
                    )
                    Text(
                        text = "${userProfile.phone} • ${userProfile.email}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = ServoraSubtext
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ServoraPeach)
                            .clickable { onLocationClick() }
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = ServoraCoral,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${userProfile.city} (${userProfile.locality})",
                            style = MaterialTheme.typography.labelSmall,
                            color = ServoraCoral
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ROLE SWITCHER (Customer / Professional Partner / Admin view)
        Text(
            text = "PERSPECTIVE MODE SWITCH",
            style = MaterialTheme.typography.labelSmall,
            color = ServoraSubtext,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, ServoraBorder, RoundedCornerShape(14.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            UserRole.entries.forEach { role ->
                val isSelected = userProfile.role == role
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) ServoraCoral else Color.Transparent)
                        .clickable { onSwitchRole(role) }
                        .padding(vertical = 10.dp)
                        .testTag("role_tab_${role.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (role) {
                                UserRole.CUSTOMER -> Icons.Default.Person
                                UserRole.PROFESSIONAL -> Icons.Default.Handyman
                                UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                            },
                            contentDescription = null,
                            tint = if (isSelected) Color.White else ServoraSubtext,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (role) {
                                UserRole.CUSTOMER -> "Customer"
                                UserRole.PROFESSIONAL -> "Partner Pro"
                                UserRole.ADMIN -> "Admin"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) Color.White else ServoraCharcoal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CONDITIONAL DASHBOARD ACCORDING TO ROLE
        if (userProfile.role == UserRole.PROFESSIONAL) {
            // PRO DASHBOARD VIEW
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = androidx.compose.foundation.BorderStroke(1.dp, ServoraGreen)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PARTNER DASHBOARD",
                                style = MaterialTheme.typography.labelSmall,
                                color = ServoraGreen
                            )
                            Text(
                                text = if (isPartnerOnline) "Status: Online & Receiving Jobs" else "Status: Offline",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = ServoraCharcoal
                            )
                        }

                        Switch(
                            checked = isPartnerOnline,
                            onCheckedChange = { isPartnerOnline = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = ServoraGreen)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("TODAY'S EARNINGS", style = MaterialTheme.typography.labelSmall, color = ServoraSubtext)
                            Text("₹3,850", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, color = ServoraGreen))
                        }
                        Column {
                            Text("JOBS COMPLETED", style = MaterialTheme.typography.labelSmall, color = ServoraSubtext)
                            Text("142", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, color = ServoraCharcoal))
                        }
                        Column {
                            Text("RATING", style = MaterialTheme.typography.labelSmall, color = ServoraSubtext)
                            Text("4.9 ★", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, color = ServoraHoney))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Next Scheduled Job: Taj Nagri Phase 2 • 02:00 PM • AC Jet Service",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = ServoraCharcoal
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        } else if (userProfile.role == UserRole.ADMIN) {
            // ADMIN DASHBOARD VIEW
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                border = androidx.compose.foundation.BorderStroke(1.dp, ServoraHoney)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "AGRA OPERATIONS CONSOLE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFB45309)
                    )
                    Text(
                        text = "Service Assist Live City Metrics",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = ServoraCharcoal
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("ACTIVE BOOKINGS", style = MaterialTheme.typography.labelSmall, color = ServoraSubtext)
                            Text("28 Live", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = ServoraCoral))
                        }
                        Column {
                            Text("ONLINE PROS", style = MaterialTheme.typography.labelSmall, color = ServoraSubtext)
                            Text("74 Active", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = ServoraGreen))
                        }
                        Column {
                            Text("AVG ARRIVAL", style = MaterialTheme.typography.labelSmall, color = ServoraSubtext)
                            Text("22 Mins", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = ServoraCharcoal))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // SAVED ADDRESSES MANAGEMENT
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SAVED ADDRESSES (${savedAddresses.size})",
                style = MaterialTheme.typography.labelSmall,
                color = ServoraSubtext,
                letterSpacing = 1.sp
            )

            OutlinedButton(
                onClick = { showAddAddress = !showAddAddress },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = ServoraCoral,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add", color = ServoraCoral, style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            savedAddresses.forEach { addr ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ServoraBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = addr.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                    color = ServoraCharcoal
                                )
                                if (addr.isDefault) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(ServoraPeach)
                                            .padding(horizontal = 6.dp, vertical = 1.dp)
                                    ) {
                                        Text("DEFAULT", color = ServoraCoral, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Text(
                                text = "${addr.fullAddress}, ${addr.locality}, ${addr.city}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                color = ServoraSubtext
                            )
                        }

                        IconButton(onClick = { onDeleteAddress(addr.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        if (showAddAddress) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, ServoraBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "New Address Details",
                        style = MaterialTheme.typography.titleMedium,
                        color = ServoraCharcoal
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = addrTitle,
                        onValueChange = { addrTitle = it },
                        label = { Text("Label (Home/Office)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = addrText,
                        onValueChange = { addrText = it },
                        label = { Text("House/Flat/Road") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = addrLocality,
                        onValueChange = { addrLocality = it },
                        label = { Text("Locality") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (addrText.isNotBlank()) {
                                onAddNewAddress(addrTitle, addrText, addrLocality, addrLandmark)
                                showAddAddress = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ServoraCoral),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Address", color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SUPABASE CLOUD BACKEND DATABASE STATUS CARD
        Text(
            text = "CLOUD BACKEND INTEGRATION",
            style = MaterialTheme.typography.labelSmall,
            color = ServoraSubtext,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("supabase_database_card"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, ServoraBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF3ECF8E).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Supabase PostgreSQL Database",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = ServoraCharcoal
                            )
                            Text(
                                text = if (supabaseSyncState.isConfigured) "Live Cloud Synchronization" else "Room DB Active (Supabase Ready)",
                                style = MaterialTheme.typography.bodySmall,
                                color = ServoraSubtext
                            )
                        }
                    }

                    // Status pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (supabaseSyncState.isConnected) Color(0xFFE8F5E9)
                                else if (supabaseSyncState.isConfigured) Color(0xFFFFF3E0)
                                else Color(0xFFF1F5F9)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (supabaseSyncState.isConnected) "CONNECTED"
                            else if (supabaseSyncState.isConfigured) "READY"
                            else "OFFLINE ROOM",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (supabaseSyncState.isConnected) Color(0xFF2E7D32)
                            else if (supabaseSyncState.isConfigured) Color(0xFFE65100)
                            else ServoraSubtext
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = supabaseSyncState.lastMessage,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = if (supabaseSyncState.error != null) Color(0xFFC62828) else ServoraSubtext
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "URL: ${SupabaseConfig.maskedUrl}",
                        style = MaterialTheme.typography.labelSmall,
                        color = ServoraSubtext
                    )
                    Text(
                        text = "Tables: bookings, addresses, reviews, users",
                        style = MaterialTheme.typography.labelSmall,
                        color = ServoraSubtext
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onSeedSupabaseDemoData,
                        enabled = !supabaseSyncState.isSyncing,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("seed_demo_data_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = ServoraCoral
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Seed Demo Data", fontSize = 11.sp, color = ServoraCoral, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onSyncWithSupabase,
                        enabled = !supabaseSyncState.isSyncing,
                        colors = ButtonDefaults.buttonColors(containerColor = ServoraCoral),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("sync_supabase_button")
                    ) {
                        if (supabaseSyncState.isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Syncing...", fontSize = 11.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sync Now", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 24x7 AGRA CUSTOMER CARE CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ServoraPeachLight),
            border = androidx.compose.foundation.BorderStroke(1.dp, ServoraPeach)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(ServoraCoral),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "24x7 Service Assist Helpline Agra",
                        style = MaterialTheme.typography.titleMedium,
                        color = ServoraCharcoal
                    )
                    Text(
                        text = "1800-ASSIST-PRO (Toll-Free) • Dedicated city support",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = ServoraSubtext
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SAFETY & INSURANCE BADGE
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF1F5F9))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.VerifiedUser,
                contentDescription = null,
                tint = ServoraGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "₹10,000 Property Damage Cover on every service booking in Agra",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = ServoraCharcoal
            )
        }
    }
}
