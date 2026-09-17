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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.ui.theme.ServoraBorder
import com.example.ui.theme.ServoraCharcoal
import com.example.ui.theme.ServoraCoral
import com.example.ui.theme.ServoraPeach
import com.example.ui.theme.ServoraStarGold
import com.example.ui.theme.ServoraSubtext

@Composable
fun ExploreScreen(
    categories: List<ServiceCategory>,
    services: List<ServiceItem>,
    selectedCategoryId: String?,
    searchQuery: String,
    onCategorySelect: (String?) -> Unit,
    onSearchChange: (String) -> Unit,
    onServiceClick: (ServiceItem) -> Unit,
    onBookService: (ServiceItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Search & Filter Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Discover Services",
                style = MaterialTheme.typography.headlineLarge,
                color = ServoraCharcoal
            )
            Text(
                text = "Compare verified packages & pricing in Agra",
                style = MaterialTheme.typography.bodyMedium,
                color = ServoraSubtext
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text(
                        text = "Search by service or issue (e.g. AC, tap, facial)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ServoraSubtext
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = ServoraCoral
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = ServoraSubtext
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("explore_search_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ServoraCoral,
                    unfocusedBorderColor = ServoraBorder,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true
            )
        }

        // Horizontal Category Chips
        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                val isAllSelected = selectedCategoryId == null
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isAllSelected) ServoraCoral else MaterialTheme.colorScheme.surface)
                        .border(
                            1.dp,
                            if (isAllSelected) ServoraCoral else ServoraBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onCategorySelect(null) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("cat_chip_all")
                ) {
                    Text(
                        text = "All Services",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isAllSelected) Color.White else ServoraCharcoal
                    )
                }
            }

            items(categories) { category ->
                val isSelected = selectedCategoryId == category.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) ServoraCoral else MaterialTheme.colorScheme.surface)
                        .border(
                            1.dp,
                            if (isSelected) ServoraCoral else ServoraBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onCategorySelect(category.id) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("cat_chip_${category.id}")
                ) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) Color.White else ServoraCharcoal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Services Count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${services.size} AVAILABLE IN AGRA",
                style = MaterialTheme.typography.labelSmall,
                color = ServoraSubtext,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Services Cards List
        if (services.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No services matched your filter.",
                    style = MaterialTheme.typography.titleMedium,
                    color = ServoraCharcoal
                )
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = {
                        onCategorySelect(null)
                        onSearchChange("")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ServoraCoral)
                ) {
                    Text("Reset Filters", color = Color.White)
                }
            }
        } else {
            LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(services) { service ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onServiceClick(service) }
                            .testTag("service_card_${service.id}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ServoraBorder)
                    ) {
                        Column {
                            Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                                Image(
                                    painter = painterResource(id = service.imageDrawableRes),
                                    contentDescription = service.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(10.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.95f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = ServoraStarGold,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${service.rating} (${service.reviewsCount})",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = ServoraCharcoal
                                        )
                                    }
                                }
                            }

                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = service.name,
                                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 17.sp),
                                        color = ServoraCharcoal,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "⏱ ${service.duration} • 🛡 ${service.warrantyText}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ServoraCoral
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = service.subtitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ServoraSubtext
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // What is included sample
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    service.whatIsIncluded.take(2).forEach { inc ->
                                        Text(
                                            text = "• $inc",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                            color = ServoraSubtext
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "STARTS AT",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ServoraSubtext
                                        )
                                        Text(
                                            text = "₹${service.startingPrice}",
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                color = ServoraCoral
                                            )
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(
                                            onClick = { onServiceClick(service) },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.height(40.dp)
                                        ) {
                                            Text("Details", color = ServoraCharcoal)
                                        }

                                        Button(
                                            onClick = { onBookService(service) },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = ServoraCoral),
                                            modifier = Modifier.height(40.dp).testTag("explore_book_${service.id}")
                                        ) {
                                            Text("Book Now", color = Color.White)
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
