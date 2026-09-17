package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ServoraBorder
import com.example.ui.theme.ServoraCoral
import com.example.ui.theme.ServoraMuted
import com.example.ui.theme.ServoraPeach
import com.example.ui.theme.ServoraSubtext

enum class ServoraNavTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    EXPLORE("Explore", Icons.Filled.Search, Icons.Outlined.Search, "nav_explore"),
    BOOKINGS("Bookings", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, "nav_bookings"),
    OFFERS("Offers", Icons.Filled.LocalOffer, Icons.Outlined.LocalOffer, "nav_offers"),
    PROFILE("Account", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile")
}

@Composable
fun ServoraBottomNav(
    currentTab: ServoraNavTab,
    onTabSelected: (ServoraNavTab) -> Unit,
    activeBookingsCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(
            thickness = 0.5.dp,
            color = ServoraBorder
        )
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            windowInsets = NavigationBarDefaults.windowInsets
        ) {
            ServoraNavTab.entries.forEach { tab ->
                val isSelected = currentTab == tab

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (tab == ServoraNavTab.BOOKINGS && activeBookingsCount > 0) {
                                    Badge(
                                        containerColor = ServoraCoral,
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = activeBookingsCount.toString(),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.label,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ServoraCoral,
                        selectedTextColor = ServoraCoral,
                        indicatorColor = ServoraPeach,
                        unselectedIconColor = ServoraMuted,
                        unselectedTextColor = ServoraSubtext
                    ),
                    modifier = Modifier.testTag(tab.tag)
                )
            }
        }
    }
}

