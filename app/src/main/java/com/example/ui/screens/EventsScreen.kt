package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChurchEvent
import com.example.data.local.RegistrationWithDetails
import com.example.ui.components.EventCard
import com.example.ui.theme.ChurchBackgroundLight
import com.example.ui.theme.ChurchBorder
import com.example.ui.theme.ChurchTextMuted
import com.example.ui.theme.ChurchTextPrimary
import com.example.ui.theme.ChurchTextSecondary
import com.example.ui.theme.ChurchWhite
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RadiantGoldDark
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.RoyalNavyLight
import com.example.ui.theme.WarmOrganicSurface

@Composable
fun EventsScreen(
    events: List<ChurchEvent>,
    userRegistrations: List<RegistrationWithDetails>,
    onRegisterClick: (ChurchEvent) -> Unit,
    onViewRegistrationClick: (RegistrationWithDetails) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    val categories = listOf(
        "ALL" to "All Events",
        "CHILDREN" to "🎈 Children's Day",
        "ADULT" to "🎊 Adults' Day",
        "WOMEN" to "👩 Women's Day",
        "HARVEST" to "🌾 General Harvest"
    )

    val registeredEventIds = remember(userRegistrations) {
        userRegistrations
            .filter { it.registration.status != "CANCELLED" }
            .map { it.event.eventId }
            .toSet()
    }

    val filteredEvents = events.filter { event ->
        val matchesCategory = if (selectedCategoryFilter == "ALL") true else event.category.equals(selectedCategoryFilter, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                event.eventName.contains(searchQuery, ignoreCase = true) ||
                event.description.contains(searchQuery, ignoreCase = true) ||
                event.venue.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChurchBackgroundLight)
            .testTag("events_screen")
    ) {
        // Search & Category Filter Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ChurchWhite)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search celebration, venue, date...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = ChurchTextMuted
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RoyalNavy,
                    unfocusedBorderColor = ChurchBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("events_search_bar")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Horizontal Scrollable Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { (key, label) ->
                    val isSelected = selectedCategoryFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryFilter = key },
                        label = {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RoyalNavy,
                            selectedLabelColor = RadiantGold,
                            containerColor = WarmOrganicSurface,
                            labelColor = ChurchTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) RadiantGold else ChurchBorder,
                            enabled = true,
                            selected = isSelected
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("filter_chip_$key")
                    )
                }
            }
        }

        // Events List
        if (filteredEvents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No events match your criteria",
                        style = MaterialTheme.typography.titleMedium,
                        color = ChurchTextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try clearing the search or choosing 'All Events'",
                        style = MaterialTheme.typography.bodySmall,
                        color = ChurchTextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredEvents) { event ->
                    val isRegistered = registeredEventIds.contains(event.eventId)
                    val regDetails = if (isRegistered) {
                        userRegistrations.firstOrNull { it.event.eventId == event.eventId && it.registration.status != "CANCELLED" }
                    } else null

                    EventCard(
                        event = event,
                        onRegisterClick = onRegisterClick,
                        isAlreadyRegistered = isRegistered,
                        onViewRegistrationClick = {
                            regDetails?.let { onViewRegistrationClick(it) }
                        }
                    )
                }
            }
        }
    }
}
