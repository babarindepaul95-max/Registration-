package com.example.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diversity1
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChurchEvent
import com.example.data.local.RegistrationWithDetails
import com.example.data.local.User
import com.example.ui.AdminStats
import com.example.ui.components.ChurchTopBar
import com.example.ui.components.StatsCard
import com.example.ui.components.getCategoryMeta
import com.example.ui.theme.ChurchBackgroundLight
import com.example.ui.theme.ChurchBorder
import com.example.ui.theme.ChurchTextMuted
import com.example.ui.theme.ChurchTextPrimary
import com.example.ui.theme.ChurchTextSecondary
import com.example.ui.theme.ChurchWhite
import com.example.ui.theme.ClosedGray
import com.example.ui.theme.ClosedGrayLight
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RadiantGoldDark
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.RoyalNavyLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AdminTab {
    OVERVIEW,
    EVENTS,
    REGISTRATIONS,
    USERS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    stats: AdminStats,
    events: List<ChurchEvent>,
    registrations: List<RegistrationWithDetails>,
    users: List<User>,
    onSaveEvent: (eventId: Long, name: String, category: String, desc: String, date: String, venue: String, status: String) -> Unit,
    onDeleteEvent: (Long) -> Unit,
    onToggleEventStatus: (ChurchEvent) -> Unit,
    onDeleteUser: (Long) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(AdminTab.OVERVIEW) }

    // Dialog states
    var showEventEditorDialog by remember { mutableStateOf(false) }
    var eventToEdit by remember { mutableStateOf<ChurchEvent?>(null) }
    var eventToDelete by remember { mutableStateOf<ChurchEvent?>(null) }
    var selectedRegDetail by remember { mutableStateOf<RegistrationWithDetails?>(null) }
    var userToDelete by remember { mutableStateOf<User?>(null) }

    // Registrations search & filters
    var regSearchQuery by remember { mutableStateOf("") }
    var selectedEventFilterId by remember { mutableStateOf<Long?>(null) }

    val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())

    Scaffold(
        topBar = {
            ChurchTopBar(
                title = "Church Admin Portal",
                subtitle = "Administration & Registry Management",
                onBackClick = onBackClick
            )
        },
        containerColor = ChurchBackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("admin_dashboard_screen")
        ) {
            // Admin Tabs Header
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = RoyalNavyDark,
                contentColor = ChurchWhite,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                        color = RadiantGold,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == AdminTab.OVERVIEW,
                    onClick = { selectedTab = AdminTab.OVERVIEW },
                    text = { Text("Overview & Stats", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("admin_tab_overview")
                )
                Tab(
                    selected = selectedTab == AdminTab.EVENTS,
                    onClick = { selectedTab = AdminTab.EVENTS },
                    text = { Text("Events Manager (${events.size})", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("admin_tab_events")
                )
                Tab(
                    selected = selectedTab == AdminTab.REGISTRATIONS,
                    onClick = { selectedTab = AdminTab.REGISTRATIONS },
                    text = { Text("Registrations (${registrations.size})", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("admin_tab_registrations")
                )
                Tab(
                    selected = selectedTab == AdminTab.USERS,
                    onClick = { selectedTab = AdminTab.USERS },
                    text = { Text("Users (${users.size})", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("admin_tab_users")
                )
            }

            // Tab Body Content
            when (selectedTab) {
                AdminTab.OVERVIEW -> {
                    AdminOverviewContent(
                        stats = stats,
                        events = events,
                        onCreateEventClick = {
                            eventToEdit = null
                            showEventEditorDialog = true
                        },
                        onViewRegistrationsClick = { selectedTab = AdminTab.REGISTRATIONS }
                    )
                }

                AdminTab.EVENTS -> {
                    AdminEventsContent(
                        events = events,
                        onCreateEventClick = {
                            eventToEdit = null
                            showEventEditorDialog = true
                        },
                        onEditEventClick = { event ->
                            eventToEdit = event
                            showEventEditorDialog = true
                        },
                        onDeleteEventClick = { event ->
                            eventToDelete = event
                        },
                        onToggleStatusClick = onToggleEventStatus
                    )
                }

                AdminTab.REGISTRATIONS -> {
                    AdminRegistrationsContent(
                        registrations = registrations,
                        events = events,
                        searchQuery = regSearchQuery,
                        onSearchQueryChange = { regSearchQuery = it },
                        selectedEventFilterId = selectedEventFilterId,
                        onEventFilterChange = { selectedEventFilterId = it },
                        onViewDetailClick = { selectedRegDetail = it },
                        onExportCsvClick = {
                            exportRegistrationsCsv(context, registrations)
                        }
                    )
                }

                AdminTab.USERS -> {
                    AdminUsersContent(
                        users = users,
                        onDeleteUserClick = { userToDelete = it }
                    )
                }
            }
        }

        // Dialog: Create / Edit Event
        if (showEventEditorDialog) {
            EventEditorDialog(
                initialEvent = eventToEdit,
                onDismiss = { showEventEditorDialog = false },
                onSave = { id, name, category, desc, date, venue, status ->
                    onSaveEvent(id, name, category, desc, date, venue, status)
                    showEventEditorDialog = false
                }
            )
        }

        // Dialog: Confirm Delete Event
        eventToDelete?.let { event ->
            AlertDialog(
                onDismissRequest = { eventToDelete = null },
                title = { Text("Delete Event?", fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to delete '${event.eventName}'? This will permanently remove the event and its associated records.") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteEvent(event.eventId)
                            eventToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text("Delete Event")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { eventToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Dialog: Confirm Delete User
        userToDelete?.let { user ->
            AlertDialog(
                onDismissRequest = { userToDelete = null },
                title = { Text("Delete Member Account?", fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to delete account for '${user.fullName}' (${user.email})?") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteUser(user.userId)
                            userToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text("Delete User")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { userToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Dialog: View Full Registration Details
        selectedRegDetail?.let { item ->
            val reg = item.registration
            val event = item.event
            val user = item.user

            AlertDialog(
                onDismissRequest = { selectedRegDetail = null },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Registration Record", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Surface(
                            color = if (reg.status == "CANCELLED") ClosedGrayLight else SuccessGreenLight,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = reg.status,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (reg.status == "CANCELLED") ClosedGray else SuccessGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = GoldContainer
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("REFERENCE NUMBER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RadiantGoldDark)
                                Text(reg.referenceNumber, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = RoyalNavyDark)
                            }
                        }

                        Text("Registrant: ${user.fullName}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Phone: ${user.phoneNumber}", fontSize = 13.sp)
                        Text("Email: ${user.email}", fontSize = 13.sp)
                        HorizontalDivider(color = ChurchBorder)
                        Text("Event: ${event.eventName}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = RoyalNavy)
                        Text("Date: ${event.eventDate}", fontSize = 13.sp)
                        Text("Venue: ${event.venue}", fontSize = 13.sp)
                        Text("Total Attendees: ${reg.attendeesCount} person(s)", fontWeight = FontWeight.Bold, color = RadiantGoldDark)
                        if (reg.comments.isNotBlank()) {
                            Text("Comments: ${reg.comments}", fontSize = 12.sp, color = ChurchTextSecondary)
                        }
                        Text("Registered: ${dateFormat.format(Date(reg.registrationDate))}", fontSize = 11.sp, color = ChurchTextMuted)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { selectedRegDetail = null },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
                    ) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

// ==================== TAB 1: OVERVIEW & STATS ====================
@Composable
private fun AdminOverviewContent(
    stats: AdminStats,
    events: List<ChurchEvent>,
    onCreateEventClick: () -> Unit,
    onViewRegistrationsClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_overview_content"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Registration Statistics",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ChurchTextPrimary
                    )
                    Text(
                        text = "Real-time registration counts across all church events",
                        style = MaterialTheme.typography.bodySmall,
                        color = ChurchTextMuted
                    )
                }

                Button(
                    onClick = onCreateEventClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RadiantGold, contentColor = RoyalNavyDark),
                    modifier = Modifier.testTag("admin_create_event_btn_overview")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Event", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Primary Stats Cards: Total Users & Total Registrations
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCard(
                    title = "Total Users",
                    value = "${stats.totalUsers}",
                    icon = Icons.Default.Group,
                    accentColor = RoyalNavy,
                    modifier = Modifier.weight(1f),
                    subtitle = "Registered Members"
                )

                StatsCard(
                    title = "Total Registrations",
                    value = "${stats.totalRegistrations}",
                    icon = Icons.Default.HowToReg,
                    accentColor = RadiantGoldDark,
                    modifier = Modifier.weight(1f),
                    subtitle = "Confirmed Seats"
                )
            }
        }

        // Category Breakdown Header
        item {
            Text(
                text = "Registrations by Celebration Category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ChurchTextPrimary
            )
        }

        // Breakdown Stats Cards for each required category:
        // - Total registrations for Children's Day Party
        // - Total registrations for Adults' Day Party
        // - Total registrations for Women's Day Party
        // - Total registrations for General Harvest Party
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                StatsCard(
                    title = "🎈 Children's Day Party",
                    value = "${stats.childrenRegistrations} Registrations",
                    icon = Icons.Default.ChildCare,
                    accentColor = Color(0xFFEA580C),
                    subtitle = "Family & Kids Celebrations"
                )

                StatsCard(
                    title = "🎊 Adults' Day Party",
                    value = "${stats.adultRegistrations} Registrations",
                    icon = Icons.Default.People,
                    accentColor = Color(0xFF4F46E5),
                    subtitle = "Adult Church Members & Guests"
                )

                StatsCard(
                    title = "👩 Women's Day Party",
                    value = "${stats.womenRegistrations} Registrations",
                    icon = Icons.Default.Diversity1,
                    accentColor = Color(0xFFDB2777),
                    subtitle = "Women's Fellowship Gathering"
                )

                StatsCard(
                    title = "🌾 General Harvest Party",
                    value = "${stats.harvestRegistrations} Registrations",
                    icon = Icons.Default.Grass,
                    accentColor = RadiantGoldDark,
                    subtitle = "Annual Church Thanksgiving Celebration"
                )
            }
        }
    }
}

// ==================== TAB 2: EVENTS MANAGER ====================
@Composable
private fun AdminEventsContent(
    events: List<ChurchEvent>,
    onCreateEventClick: () -> Unit,
    onEditEventClick: (ChurchEvent) -> Unit,
    onDeleteEventClick: (ChurchEvent) -> Unit,
    onToggleStatusClick: (ChurchEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_events_content"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Event Registry Manager",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ChurchTextPrimary
                    )
                    Text(
                        text = "Create, edit, open/close registrations, or remove events",
                        style = MaterialTheme.typography.bodySmall,
                        color = ChurchTextMuted
                    )
                }

                Button(
                    onClick = onCreateEventClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                    modifier = Modifier.testTag("admin_create_event_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Event", fontWeight = FontWeight.Bold)
                }
            }
        }

        items(events) { event ->
            val isOpen = event.status.equals("OPEN", ignoreCase = true)
            val (categoryLabel, categoryIcon, categoryColor) = getCategoryMeta(event.category)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ChurchWhite),
                border = BorderStroke(1.dp, ChurchBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = categoryColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = categoryIcon, contentDescription = null, tint = categoryColor, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(categoryLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = categoryColor)
                            }
                        }

                        // Open / Close status toggle badge
                        Surface(
                            color = if (isOpen) SuccessGreenLight else ClosedGrayLight,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { onToggleStatusClick(event) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isOpen) Icons.Default.LockOpen else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isOpen) SuccessGreen else ClosedGray,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isOpen) "OPEN (Tap to Close)" else "CLOSED (Tap to Open)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOpen) SuccessGreen else ClosedGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = event.eventName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChurchTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = ChurchTextSecondary,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = RoyalNavyLight, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(event.eventDate, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ChurchTextPrimary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider(color = ChurchBorder.copy(alpha = 0.5f))

                    Spacer(modifier = Modifier.height(8.dp))

                    // Actions Row: Edit, Delete, Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { onEditEventClick(event) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("admin_edit_event_${event.eventId}")
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit", fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedButton(
                            onClick = { onDeleteEventClick(event) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                            border = BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.5f)),
                            modifier = Modifier.testTag("admin_delete_event_${event.eventId}")
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// ==================== TAB 3: REGISTRATIONS ====================
@Composable
private fun AdminRegistrationsContent(
    registrations: List<RegistrationWithDetails>,
    events: List<ChurchEvent>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedEventFilterId: Long?,
    onEventFilterChange: (Long?) -> Unit,
    onViewDetailClick: (RegistrationWithDetails) -> Unit,
    onExportCsvClick: () -> Unit
) {
    val filteredRegistrations = registrations.filter { item ->
        val matchesEvent = selectedEventFilterId == null || item.event.eventId == selectedEventFilterId
        val q = searchQuery.trim()
        val matchesSearch = q.isBlank() ||
                item.user.fullName.contains(q, ignoreCase = true) ||
                item.user.phoneNumber.contains(q, ignoreCase = true) ||
                item.user.email.contains(q, ignoreCase = true) ||
                item.registration.referenceNumber.contains(q, ignoreCase = true) ||
                item.event.eventName.contains(q, ignoreCase = true)
        matchesEvent && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_registrations_content")
    ) {
        // Search & Export Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ChurchWhite)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Registrations Registry (${filteredRegistrations.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ChurchTextPrimary
                )

                Button(
                    onClick = onExportCsvClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RadiantGold,
                        contentColor = RoyalNavyDark
                    ),
                    modifier = Modifier.testTag("admin_export_csv_btn")
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export CSV", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            // Search by Name, Phone, Email
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search by name, phone, email, reference...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = ChurchTextMuted)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_reg_search_input"),
                shape = RoundedCornerShape(12.dp)
            )

            // Event Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedEventFilterId == null,
                        onClick = { onEventFilterChange(null) },
                        label = { Text("All Events") },
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                items(events) { event ->
                    val isSelected = selectedEventFilterId == event.eventId
                    FilterChip(
                        selected = isSelected,
                        onClick = { onEventFilterChange(if (isSelected) null else event.eventId) },
                        label = { Text(event.eventName, maxLines = 1) },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }

        // Registrations List
        if (filteredRegistrations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No registration records found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ChurchTextMuted
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredRegistrations) { item ->
                    val reg = item.registration
                    val event = item.event
                    val user = item.user
                    val isCancelled = reg.status == "CANCELLED"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onViewDetailClick(item) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = ChurchWhite),
                        border = BorderStroke(1.dp, ChurchBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = user.fullName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = ChurchTextPrimary
                                )

                                Surface(
                                    color = GoldContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = reg.referenceNumber,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalNavyDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "📞 ${user.phoneNumber} • ✉️ ${user.email}",
                                fontSize = 12.sp,
                                color = ChurchTextSecondary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Event: ${event.eventName}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = RoyalNavy
                                )

                                Text(
                                    text = "${reg.attendeesCount} Attendee(s)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RadiantGoldDark
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== TAB 4: USER MANAGEMENT ====================
@Composable
private fun AdminUsersContent(
    users: List<User>,
    onDeleteUserClick: (User) -> Unit
) {
    var userSearch by remember { mutableStateOf("") }

    val filteredUsers = users.filter { u ->
        userSearch.isBlank() ||
                u.fullName.contains(userSearch, ignoreCase = true) ||
                u.email.contains(userSearch, ignoreCase = true) ||
                u.phoneNumber.contains(userSearch, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_users_content")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ChurchWhite)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Registered Church Members (${filteredUsers.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = ChurchTextPrimary
            )

            OutlinedTextField(
                value = userSearch,
                onValueChange = { userSearch = it },
                placeholder = { Text("Search by name, email, phone...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = ChurchTextMuted) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredUsers) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ChurchWhite),
                    border = BorderStroke(1.dp, ChurchBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (user.role == "ADMIN") RadiantGold else RoyalNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.fullName.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = if (user.role == "ADMIN") RoyalNavyDark else ChurchWhite
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    if (user.role == "ADMIN") {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(color = GoldContainer, shape = RoundedCornerShape(4.dp)) {
                                            Text("ADMIN", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = RadiantGoldDark, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                                Text(user.email, fontSize = 12.sp, color = ChurchTextMuted)
                                Text(user.phoneNumber, fontSize = 11.sp, color = ChurchTextSecondary)
                            }
                        }

                        if (user.role != "ADMIN") {
                            IconButton(
                                onClick = { onDeleteUserClick(user) },
                                modifier = Modifier.testTag("delete_user_${user.userId}")
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete User", tint = ClosedGray)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Dialog: Create or Edit Church Event
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventEditorDialog(
    initialEvent: ChurchEvent?,
    onDismiss: () -> Unit,
    onSave: (id: Long, name: String, category: String, desc: String, date: String, venue: String, status: String) -> Unit
) {
    var name by remember { mutableStateOf(initialEvent?.eventName ?: "") }
    var category by remember { mutableStateOf(initialEvent?.category ?: "CHILDREN") }
    var desc by remember { mutableStateOf(initialEvent?.description ?: "") }
    var date by remember { mutableStateOf(initialEvent?.eventDate ?: "") }
    var venue by remember { mutableStateOf(initialEvent?.venue ?: "Church Main Sanctuary") }
    var status by remember { mutableStateOf(initialEvent?.status ?: "OPEN") }
    var categoryExpanded by remember { mutableStateOf(false) }

    val categoryOptions = listOf(
        "CHILDREN" to "🎈 Children's Day Party",
        "ADULT" to "🎊 Adults' Day Party",
        "WOMEN" to "👩 Women's Day Party",
        "HARVEST" to "🌾 General Harvest Party"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialEvent == null) "Create New Church Event" else "Edit Event Details",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Event Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("event_editor_name")
                )

                // Category Selector
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = categoryOptions.find { it.first == category }?.second ?: category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Event Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categoryOptions.forEach { (catKey, catLabel) ->
                            DropdownMenuItem(
                                text = { Text(catLabel) },
                                onClick = {
                                    category = catKey
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date & Time (e.g. October 15, 2026 • 10:00 AM) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("event_editor_date")
                )

                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Venue Location *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("event_editor_venue")
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Short Description") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("event_editor_desc")
                )

                // Status row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Registration Status:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = status == "OPEN",
                            onClick = { status = "OPEN" },
                            label = { Text("OPEN") }
                        )
                        FilterChip(
                            selected = status == "CLOSED",
                            onClick = { status = "CLOSED" },
                            label = { Text("CLOSED") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && date.isNotBlank() && venue.isNotBlank()) {
                        onSave(initialEvent?.eventId ?: 0L, name, category, desc, date, venue, status)
                    }
                },
                enabled = name.isNotBlank() && date.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy),
                modifier = Modifier.testTag("save_event_submit_btn")
            ) {
                Text(if (initialEvent == null) "Create Event" else "Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Export registration records to CSV and launch share chooser
private fun exportRegistrationsCsv(context: Context, list: List<RegistrationWithDetails>) {
    val header = "Registration Reference,Event Name,Category,Event Date,Registrant Name,Phone Number,Email,Attendees,Status,Comments,Date Registered\n"
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    val rows = list.joinToString("\n") { item ->
        val reg = item.registration
        val e = item.event
        val u = item.user
        val regDateStr = dateFormat.format(Date(reg.registrationDate))
        val escapedComments = reg.comments.replace("\"", "\"\"")

        "\"${reg.referenceNumber}\",\"${e.eventName}\",\"${e.category}\",\"${e.eventDate}\",\"${u.fullName}\",\"${u.phoneNumber}\",\"${u.email}\",${reg.attendeesCount},\"${reg.status}\",\"$escapedComments\",\"$regDateStr\""
    }

    val fullCsv = header + rows

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, fullCsv)
        putExtra(Intent.EXTRA_SUBJECT, "Grace Community Church - Registration Records Export")
        type = "text/csv"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Export Registration Records (CSV)")
    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(shareIntent)
}
