package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChurchEvent
import com.example.data.local.RegistrationWithDetails
import com.example.data.local.User
import com.example.ui.DashboardTab
import com.example.ui.components.ChurchTopBar
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
import com.example.ui.theme.RadiantGoldLight
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.RoyalNavyLight
import com.example.ui.theme.WarmOrganicSurface

@Composable
fun UserDashboardScreen(
    user: User,
    currentTab: DashboardTab,
    events: List<ChurchEvent>,
    userRegistrations: List<RegistrationWithDetails>,
    onTabSelected: (DashboardTab) -> Unit,
    onRegisterEventClick: (ChurchEvent) -> Unit,
    onViewRegistrationClick: (RegistrationWithDetails) -> Unit,
    onCancelRegistration: (Long) -> Unit,
    onUpdateProfile: (String, String) -> Unit,
    emailNotifications: Boolean,
    onToggleEmailNotifications: (Boolean) -> Unit,
    smsReminders: Boolean,
    onToggleSmsReminders: (Boolean) -> Unit,
    onResetPassword: (String, String, String, () -> Unit) -> Unit,
    onAdminPortalClick: () -> Unit,
    onLogout: () -> Unit
) {
    val registeredEventIds = remember(userRegistrations) {
        userRegistrations
            .filter { it.registration.status != "CANCELLED" }
            .map { it.event.eventId }
            .toSet()
    }

    Scaffold(
        topBar = {
            ChurchTopBar(
                title = when (currentTab) {
                    DashboardTab.HOME -> "Grace Portal"
                    DashboardTab.AVAILABLE_EVENTS -> "Events Catalog"
                    DashboardTab.MY_REGISTRATIONS -> "My Tickets"
                    DashboardTab.MY_PROFILE -> "Member Profile"
                    DashboardTab.SETTINGS -> "Portal Settings"
                },
                subtitle = when (currentTab) {
                    DashboardTab.HOME -> "Community & Sanctuary"
                    DashboardTab.AVAILABLE_EVENTS -> "Upcoming Celebrations"
                    DashboardTab.MY_REGISTRATIONS -> "Active Registrations"
                    DashboardTab.MY_PROFILE -> user.fullName
                    DashboardTab.SETTINGS -> "Preferences & Account"
                },
                currentUser = user,
                onAdminClick = if (user.role == "ADMIN") onAdminPortalClick else null,
                onLogoutClick = onLogout
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(color = ChurchBorder, thickness = 1.dp)
                NavigationBar(
                    containerColor = ChurchWhite,
                    contentColor = RoyalNavy,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        selected = currentTab == DashboardTab.HOME,
                        onClick = { onTabSelected(DashboardTab.HOME) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == DashboardTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = {
                            Text(
                                "Home",
                                fontSize = 10.sp,
                                fontWeight = if (currentTab == DashboardTab.HOME) FontWeight.Bold else FontWeight.Normal,
                                letterSpacing = 0.5.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RadiantGold,
                            selectedTextColor = RadiantGold,
                            indicatorColor = WarmOrganicSurface,
                            unselectedIconColor = ChurchTextMuted,
                            unselectedTextColor = ChurchTextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_home")
                    )

                    NavigationBarItem(
                        selected = currentTab == DashboardTab.AVAILABLE_EVENTS,
                        onClick = { onTabSelected(DashboardTab.AVAILABLE_EVENTS) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == DashboardTab.AVAILABLE_EVENTS) Icons.Filled.Celebration else Icons.Outlined.Celebration,
                                contentDescription = "Available Events"
                            )
                        },
                        label = {
                            Text(
                                "Events",
                                fontSize = 10.sp,
                                fontWeight = if (currentTab == DashboardTab.AVAILABLE_EVENTS) FontWeight.Bold else FontWeight.Normal,
                                letterSpacing = 0.5.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RadiantGold,
                            selectedTextColor = RadiantGold,
                            indicatorColor = WarmOrganicSurface,
                            unselectedIconColor = ChurchTextMuted,
                            unselectedTextColor = ChurchTextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_events")
                    )

                    NavigationBarItem(
                        selected = currentTab == DashboardTab.MY_REGISTRATIONS,
                        onClick = { onTabSelected(DashboardTab.MY_REGISTRATIONS) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == DashboardTab.MY_REGISTRATIONS) Icons.Filled.ConfirmationNumber else Icons.Outlined.ConfirmationNumber,
                                contentDescription = "My Registrations"
                            )
                        },
                        label = {
                            Text(
                                "Tickets",
                                fontSize = 10.sp,
                                fontWeight = if (currentTab == DashboardTab.MY_REGISTRATIONS) FontWeight.Bold else FontWeight.Normal,
                                letterSpacing = 0.5.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RadiantGold,
                            selectedTextColor = RadiantGold,
                            indicatorColor = WarmOrganicSurface,
                            unselectedIconColor = ChurchTextMuted,
                            unselectedTextColor = ChurchTextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_registrations")
                    )

                    NavigationBarItem(
                        selected = currentTab == DashboardTab.MY_PROFILE,
                        onClick = { onTabSelected(DashboardTab.MY_PROFILE) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == DashboardTab.MY_PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "My Profile"
                            )
                        },
                        label = {
                            Text(
                                "Profile",
                                fontSize = 10.sp,
                                fontWeight = if (currentTab == DashboardTab.MY_PROFILE) FontWeight.Bold else FontWeight.Normal,
                                letterSpacing = 0.5.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RadiantGold,
                            selectedTextColor = RadiantGold,
                            indicatorColor = WarmOrganicSurface,
                            unselectedIconColor = ChurchTextMuted,
                            unselectedTextColor = ChurchTextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_profile")
                    )

                    NavigationBarItem(
                        selected = currentTab == DashboardTab.SETTINGS,
                        onClick = { onTabSelected(DashboardTab.SETTINGS) },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == DashboardTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = {
                            Text(
                                "Settings",
                                fontSize = 10.sp,
                                fontWeight = if (currentTab == DashboardTab.SETTINGS) FontWeight.Bold else FontWeight.Normal,
                                letterSpacing = 0.5.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RadiantGold,
                            selectedTextColor = RadiantGold,
                            indicatorColor = WarmOrganicSurface,
                            unselectedIconColor = ChurchTextMuted,
                            unselectedTextColor = ChurchTextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_settings")
                    )
                }
            }
        },
        containerColor = ChurchBackgroundLight
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                DashboardTab.HOME -> {
                    HomeTabContent(
                        user = user,
                        events = events,
                        userRegistrations = userRegistrations,
                        registeredEventIds = registeredEventIds,
                        onTabSelected = onTabSelected,
                        onRegisterClick = onRegisterEventClick,
                        onViewRegistrationClick = onViewRegistrationClick,
                        onLogout = onLogout
                    )
                }
                DashboardTab.AVAILABLE_EVENTS -> {
                    EventsScreen(
                        events = events,
                        userRegistrations = userRegistrations,
                        onRegisterClick = onRegisterEventClick,
                        onViewRegistrationClick = onViewRegistrationClick
                    )
                }
                DashboardTab.MY_REGISTRATIONS -> {
                    MyRegistrationsScreen(
                        registrations = userRegistrations,
                        onBrowseEventsClick = { onTabSelected(DashboardTab.AVAILABLE_EVENTS) },
                        onCancelRegistration = onCancelRegistration
                    )
                }
                DashboardTab.MY_PROFILE -> {
                    UserProfileScreen(
                        user = user,
                        totalRegistrations = userRegistrations.count { it.registration.status != "CANCELLED" },
                        onUpdateProfile = onUpdateProfile
                    )
                }
                DashboardTab.SETTINGS -> {
                    SettingsScreen(
                        user = user,
                        emailNotifications = emailNotifications,
                        onToggleEmailNotifications = onToggleEmailNotifications,
                        smsReminders = smsReminders,
                        onToggleSmsReminders = onToggleSmsReminders,
                        onResetPassword = onResetPassword,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryTile(
    emoji: String,
    tag: String,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ChurchWhite),
        border = BorderStroke(1.dp, ChurchBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(WarmOrganicSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column {
                Text(
                    text = tag,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChurchTextMuted,
                    letterSpacing = 1.sp
                )
                Text(
                    text = title,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = ChurchTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun HomeTabContent(
    user: User,
    events: List<ChurchEvent>,
    userRegistrations: List<RegistrationWithDetails>,
    registeredEventIds: Set<Long>,
    onTabSelected: (DashboardTab) -> Unit,
    onRegisterClick: (ChurchEvent) -> Unit,
    onViewRegistrationClick: (RegistrationWithDetails) -> Unit,
    onLogout: () -> Unit
) {
    val activeRegistrationsCount = userRegistrations.count { it.registration.status != "CANCELLED" }
    val highlightEvent = events.firstOrNull { it.category.equals("HARVEST", true) } ?: events.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_home_content"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Greeting Header (Warm & Organic Cultural)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Peace be with you,",
                    fontSize = 13.sp,
                    color = ChurchTextSecondary
                )
                Text(
                    text = user.fullName,
                    fontFamily = FontFamily.Serif,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ChurchTextPrimary
                )
            }
        }

        // Upcoming Highlight Card (Design HTML Style: Deep obsidian with warm golden radial blur & gold button)
        if (highlightEvent != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upcoming_highlight_card"),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = RoyalNavy),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Radiant gold subtle corner aura
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .align(Alignment.TopEnd)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(RadiantGold.copy(alpha = 0.20f), Color.Transparent)
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "UPCOMING HIGHLIGHT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = RadiantGold,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = highlightEvent.eventName,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 22.sp,
                                color = ChurchWhite,
                                lineHeight = 28.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("📅", fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = highlightEvent.eventDate.split("•").firstOrNull()?.trim() ?: highlightEvent.eventDate,
                                        fontSize = 12.sp,
                                        color = ChurchWhite.copy(alpha = 0.85f)
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("📍", fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = highlightEvent.venue.split("&").firstOrNull()?.trim() ?: highlightEvent.venue,
                                        fontSize = 12.sp,
                                        color = ChurchWhite.copy(alpha = 0.85f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(18.dp))
                            Button(
                                onClick = { onRegisterClick(highlightEvent) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("highlight_register_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RadiantGold,
                                    contentColor = RoyalNavy
                                )
                            ) {
                                Text(
                                    text = "REGISTER NOW",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = RoyalNavy,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Event Categories Section (Warm Cultural Theme Grid)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EVENT CATEGORIES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ChurchTextMuted,
                        letterSpacing = 1.8.sp
                    )
                    TextButton(
                        onClick = { onTabSelected(DashboardTab.AVAILABLE_EVENTS) },
                        modifier = Modifier.testTag("see_all_categories_button")
                    ) {
                        Text(
                            text = "View All",
                            color = RadiantGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoryTile(
                        emoji = "🎈",
                        tag = "FAMILIES",
                        title = "Children's Day",
                        modifier = Modifier.weight(1f),
                        onClick = { onTabSelected(DashboardTab.AVAILABLE_EVENTS) }
                    )
                    CategoryTile(
                        emoji = "👩",
                        tag = "FELLOWSHIP",
                        title = "Women's Day",
                        modifier = Modifier.weight(1f),
                        onClick = { onTabSelected(DashboardTab.AVAILABLE_EVENTS) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoryTile(
                        emoji = "🎊",
                        tag = "SOCIAL",
                        title = "Adults' Party",
                        modifier = Modifier.weight(1f),
                        onClick = { onTabSelected(DashboardTab.AVAILABLE_EVENTS) }
                    )
                    CategoryTile(
                        emoji = "🌾",
                        tag = "THANKSGIVING",
                        title = "Harvest Festival",
                        modifier = Modifier.weight(1f),
                        onClick = { onTabSelected(DashboardTab.AVAILABLE_EVENTS) }
                    )
                }
            }
        }

        // Section Shortcuts: Available Events & My Registrations
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Browse Events Shortcut Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTabSelected(DashboardTab.AVAILABLE_EVENTS) }
                        .testTag("quick_browse_events_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ChurchWhite),
                    border = BorderStroke(1.dp, ChurchBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(WarmOrganicSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Celebration,
                                contentDescription = null,
                                tint = RadiantGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Browse Events",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = ChurchTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${events.size} available",
                            fontSize = 11.sp,
                            color = ChurchTextMuted
                        )
                    }
                }

                // My Registrations Shortcut Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTabSelected(DashboardTab.MY_REGISTRATIONS) }
                        .testTag("quick_my_registrations_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ChurchWhite),
                    border = BorderStroke(1.dp, ChurchBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(WarmOrganicSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ConfirmationNumber,
                                contentDescription = null,
                                tint = RadiantGoldDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "My Tickets",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = ChurchTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$activeRegistrationsCount ticket(s) saved",
                            fontSize = 11.sp,
                            color = ChurchTextMuted
                        )
                    }
                }
            }
        }

        // Heading: Available Upcoming Church Events
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Church Events",
                    fontFamily = FontFamily.Serif,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = ChurchTextPrimary
                )
                TextButton(
                    onClick = { onTabSelected(DashboardTab.AVAILABLE_EVENTS) },
                    modifier = Modifier.testTag("see_all_events_button")
                ) {
                    Text(
                        text = "See All (${events.size})",
                        color = RadiantGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // List of Events
        items(events) { event ->
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
