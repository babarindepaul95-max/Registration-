package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChurchEvent
import com.example.data.local.User
import com.example.ui.components.ChurchTopBar
import com.example.ui.components.getCategoryMeta
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
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventRegistrationScreen(
    event: ChurchEvent,
    currentUser: User,
    isLoading: Boolean = false,
    onSubmitRegistration: (event: ChurchEvent, attendeesCount: Int, comments: String) -> Unit,
    onBackClick: () -> Unit
) {
    var attendeesCount by remember { mutableIntStateOf(1) }
    var comments by remember { mutableStateOf("") }
    var showReviewDialog by remember { mutableStateOf(false) }

    val (categoryLabel, categoryIcon, categoryColor) = getCategoryMeta(event.category)

    Scaffold(
        topBar = {
            ChurchTopBar(
                title = "Event Registration",
                subtitle = event.eventName,
                onBackClick = onBackClick
            )
        },
        containerColor = ChurchBackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Event Details Banner Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ChurchWhite),
                border = BorderStroke(1.dp, ChurchBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Surface(
                        color = categoryColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = categoryIcon,
                                contentDescription = null,
                                tint = categoryColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = categoryLabel,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = categoryColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = event.eventName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ChurchTextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = RoyalNavyLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = event.eventDate,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = ChurchTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = RadiantGoldDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = event.venue,
                            style = MaterialTheme.typography.bodySmall,
                            color = ChurchTextSecondary
                        )
                    }
                }
            }

            // Section 1: Auto-displayed User Contact Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ChurchWhite),
                border = BorderStroke(1.dp, ChurchBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Registrant Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavy
                    )
                    Text(
                        text = "Auto-filled from your member account",
                        style = MaterialTheme.typography.bodySmall,
                        color = ChurchTextMuted
                    )

                    HorizontalDivider(color = ChurchBorder.copy(alpha = 0.5f))

                    // Full Name (auto-displayed)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = RoyalNavyLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Full Name", fontSize = 11.sp, color = ChurchTextMuted)
                            Text(
                                text = currentUser.fullName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ChurchTextPrimary
                            )
                        }
                    }

                    // Phone Number (auto-displayed)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = RoyalNavyLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Phone Number", fontSize = 11.sp, color = ChurchTextMuted)
                            Text(
                                text = currentUser.phoneNumber,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ChurchTextPrimary
                            )
                        }
                    }

                    // Email Address (auto-displayed)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = RoyalNavyLight,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Email Address", fontSize = 11.sp, color = ChurchTextMuted)
                            Text(
                                text = currentUser.email,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ChurchTextPrimary
                            )
                        }
                    }
                }
            }

            // Section 2: Attendance & Additional Details
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ChurchWhite),
                border = BorderStroke(1.dp, ChurchBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Attendance Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavy
                    )

                    // Attendees Counter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Number of People Attending",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = ChurchTextPrimary
                            )
                            Text(
                                text = "Include yourself and family/guests",
                                style = MaterialTheme.typography.bodySmall,
                                color = ChurchTextMuted
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledIconButton(
                                onClick = { if (attendeesCount > 1) attendeesCount-- },
                                enabled = attendeesCount > 1,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("decrement_attendees_button"),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = RoyalNavy.copy(alpha = 0.1f),
                                    contentColor = RoyalNavy
                                )
                            ) {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease")
                            }

                            Surface(
                                modifier = Modifier
                                    .width(42.dp)
                                    .height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = GoldContainer,
                                border = BorderStroke(1.dp, RadiantGoldDark)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "$attendeesCount",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = RoyalNavyDark
                                    )
                                }
                            }

                            FilledIconButton(
                                onClick = { if (attendeesCount < 20) attendeesCount++ },
                                enabled = attendeesCount < 20,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("increment_attendees_button"),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = RoyalNavy,
                                    contentColor = ChurchWhite
                                )
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Increase")
                            }
                        }
                    }

                    HorizontalDivider(color = ChurchBorder.copy(alpha = 0.5f))

                    // Special Information / Comments (Optional)
                    OutlinedTextField(
                        value = comments,
                        onValueChange = { comments = it },
                        label = { Text("Special Information / Comment (optional)") },
                        placeholder = { Text("e.g. Dietary requirements, wheelchair seating, prayer intentions, children's ages...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("registration_comments_input"),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalNavy
                        )
                    )
                }
            }

            // Summary Review Prompt & Submit Button
            Button(
                onClick = { showReviewDialog = true },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_registration_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoyalNavy,
                    contentColor = ChurchWhite
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = ChurchWhite, modifier = Modifier.size(24.dp))
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SUBMIT REGISTRATION",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Summary Review Dialog Before Submitting
        if (showReviewDialog) {
            AlertDialog(
                onDismissRequest = { showReviewDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = RadiantGoldDark,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Review Registration Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = ChurchTextPrimary
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Please verify your registration summary before confirming:",
                            fontSize = 13.sp,
                            color = ChurchTextSecondary
                        )

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = ChurchBackgroundLight,
                            border = BorderStroke(1.dp, ChurchBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Event:", fontSize = 13.sp, color = ChurchTextMuted)
                                    Text(
                                        event.eventName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ChurchTextPrimary
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Date:", fontSize = 13.sp, color = ChurchTextMuted)
                                    Text(
                                        event.eventDate,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ChurchTextPrimary
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Registrant:", fontSize = 13.sp, color = ChurchTextMuted)
                                    Text(
                                        currentUser.fullName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ChurchTextPrimary
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Email:", fontSize = 13.sp, color = ChurchTextMuted)
                                    Text(currentUser.email, fontSize = 12.sp, color = ChurchTextPrimary)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Attendees:", fontSize = 13.sp, color = ChurchTextMuted)
                                    Text(
                                        "$attendeesCount person(s)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalNavy
                                    )
                                }

                                if (comments.isNotBlank()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Comments:", fontSize = 12.sp, color = ChurchTextMuted)
                                        Text(
                                            comments,
                                            fontSize = 12.sp,
                                            color = ChurchTextSecondary,
                                            modifier = Modifier.width(160.dp),
                                            textAlign = TextAlign.End
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showReviewDialog = false
                            onSubmitRegistration(event, attendeesCount, comments)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoyalNavy,
                            contentColor = ChurchWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("confirm_review_submit_button")
                    ) {
                        Text("Confirm & Submit", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showReviewDialog = false },
                        modifier = Modifier.testTag("dismiss_review_button")
                    ) {
                        Text("Edit Details", color = ChurchTextSecondary)
                    }
                }
            )
        }
    }
}
