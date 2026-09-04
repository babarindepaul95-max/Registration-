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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.RegistrationWithDetails
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

@Composable
fun MyRegistrationsScreen(
    registrations: List<RegistrationWithDetails>,
    onBrowseEventsClick: () -> Unit,
    onCancelRegistration: (Long) -> Unit
) {
    var selectedRegistrationForDetail by remember { mutableStateOf<RegistrationWithDetails?>(null) }
    var registrationToCancel by remember { mutableStateOf<RegistrationWithDetails?>(null) }

    val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChurchBackgroundLight)
            .testTag("my_registrations_screen")
    ) {
        if (registrations.isEmpty()) {
            // Empty State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(GoldContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EventBusy,
                        contentDescription = null,
                        tint = RadiantGoldDark,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "No Registrations Found",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ChurchTextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "You haven't registered for any upcoming church celebrations yet. Browse available events to reserve your seat!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ChurchTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onBrowseEventsClick,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoyalNavy,
                        contentColor = ChurchWhite
                    ),
                    modifier = Modifier.testTag("browse_events_empty_button")
                ) {
                    Text("Browse Available Events", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "My Event Registrations (${registrations.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChurchTextPrimary
                    )
                    Text(
                        text = "Tap on any card to view your full registration ticket details",
                        style = MaterialTheme.typography.bodySmall,
                        color = ChurchTextMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                items(registrations) { item ->
                    val reg = item.registration
                    val event = item.event
                    val (categoryLabel, categoryIcon, categoryColor) = getCategoryMeta(event.category)
                    val isCancelled = reg.status == "CANCELLED"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRegistrationForDetail = item }
                            .testTag("reg_card_${reg.registrationId}"),
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
                            // Top Row: Reference and Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = GoldContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = reg.referenceNumber,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalNavyDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Surface(
                                    color = if (isCancelled) ClosedGrayLight else SuccessGreenLight,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isCancelled) Icons.Default.Cancel else Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = if (isCancelled) ClosedGray else SuccessGreen,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isCancelled) "CANCELLED" else "CONFIRMED",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCancelled) ClosedGray else SuccessGreen
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Event Title
                            Text(
                                text = event.eventName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ChurchTextPrimary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Date
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = RoyalNavyLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = event.eventDate,
                                    fontSize = 13.sp,
                                    color = ChurchTextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Attendees
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.People,
                                    contentDescription = null,
                                    tint = RadiantGoldDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${reg.attendeesCount} Attendee(s) Reserved",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ChurchTextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            HorizontalDivider(color = ChurchBorder.copy(alpha = 0.6f))

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Registered: ${dateFormat.format(Date(reg.registrationDate))}",
                                    fontSize = 11.sp,
                                    color = ChurchTextMuted
                                )

                                Text(
                                    text = "View Details →",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalNavyLight
                                )
                            }
                        }
                    }
                }
            }
        }

        // Full Registration Detail Modal
        selectedRegistrationForDetail?.let { item ->
            val reg = item.registration
            val event = item.event
            val user = item.user
            val isCancelled = reg.status == "CANCELLED"

            AlertDialog(
                onDismissRequest = { selectedRegistrationForDetail = null },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Registration Ticket",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = ChurchTextPrimary
                        )
                        Surface(
                            color = if (isCancelled) ClosedGrayLight else SuccessGreenLight,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isCancelled) "CANCELLED" else "CONFIRMED",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCancelled) ClosedGray else SuccessGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Ticket Reference Header
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = GoldContainer
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "OFFICIAL REFERENCE CODE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RadiantGoldDark
                                )
                                Text(
                                    text = reg.referenceNumber,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RoyalNavyDark
                                )
                            }
                        }

                        // Ticket Information Rows
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = ChurchBackgroundLight,
                            border = BorderStroke(1.dp, ChurchBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Column {
                                    Text("Event Name:", fontSize = 11.sp, color = ChurchTextMuted)
                                    Text(event.eventName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                                }

                                Column {
                                    Text("Date & Time:", fontSize = 11.sp, color = ChurchTextMuted)
                                    Text(event.eventDate, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ChurchTextPrimary)
                                }

                                Column {
                                    Text("Venue:", fontSize = 11.sp, color = ChurchTextMuted)
                                    Text(event.venue, fontSize = 13.sp, color = ChurchTextSecondary)
                                }

                                Column {
                                    Text("Registrant Contact:", fontSize = 11.sp, color = ChurchTextMuted)
                                    Text("${user.fullName} • ${user.phoneNumber}", fontSize = 12.sp, color = ChurchTextPrimary)
                                    Text(user.email, fontSize = 12.sp, color = ChurchTextSecondary)
                                }

                                Column {
                                    Text("Total Attendees:", fontSize = 11.sp, color = ChurchTextMuted)
                                    Text("${reg.attendeesCount} person(s)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RadiantGoldDark)
                                }

                                if (reg.comments.isNotBlank()) {
                                    Column {
                                        Text("Special Comments:", fontSize = 11.sp, color = ChurchTextMuted)
                                        Text(reg.comments, fontSize = 12.sp, color = ChurchTextSecondary)
                                    }
                                }

                                Column {
                                    Text("Registration Date:", fontSize = 11.sp, color = ChurchTextMuted)
                                    Text(dateFormat.format(Date(reg.registrationDate)), fontSize = 12.sp, color = ChurchTextMuted)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { selectedRegistrationForDetail = null },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalNavy)
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    if (!isCancelled) {
                        TextButton(
                            onClick = {
                                registrationToCancel = item
                                selectedRegistrationForDetail = null
                            }
                        ) {
                            Text("Cancel Registration", color = ClosedGray)
                        }
                    }
                }
            )
        }

        // Cancel Confirmation Dialog
        registrationToCancel?.let { item ->
            AlertDialog(
                onDismissRequest = { registrationToCancel = null },
                title = { Text("Cancel Registration?", fontWeight = FontWeight.Bold) },
                text = {
                    Text("Are you sure you want to cancel your reservation for '${item.event.eventName}'? Your seat will be released.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onCancelRegistration(item.registration.registrationId)
                            registrationToCancel = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text("Yes, Cancel")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { registrationToCancel = null }) {
                        Text("Keep Registration")
                    }
                }
            )
        }
    }
}
