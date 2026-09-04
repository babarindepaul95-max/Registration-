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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
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
import com.example.ui.theme.RoyalNavyDark
import com.example.ui.theme.RoyalNavyLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RegistrationConfirmationScreen(
    registrationDetails: RegistrationWithDetails?,
    onViewRegistration: () -> Unit,
    onBackToDashboard: () -> Unit,
    onSendEmailConfirmation: (String) -> Unit
) {
    val reg = registrationDetails?.registration
    val event = registrationDetails?.event
    val user = registrationDetails?.user

    var emailNotificationSent by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("MMMM dd, yyyy • hh:mm a", Locale.getDefault())
    val formattedRegDate = if (reg != null) {
        dateFormat.format(Date(reg.registrationDate))
    } else {
        dateFormat.format(Date())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChurchBackgroundLight)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("registration_confirmation_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Success Glowing Icon
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(SuccessGreenLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = SuccessGreen,
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "REGISTRATION SUCCESSFUL!",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = RoyalNavyDark,
            textAlign = TextAlign.Center,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your seat has been reserved. An official registration record has been generated.",
            style = MaterialTheme.typography.bodyMedium,
            color = ChurchTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Official Registration Ticket Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ChurchWhite),
            border = BorderStroke(1.5.dp, RadiantGold.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Reference Banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = GoldContainer,
                    border = BorderStroke(1.dp, RadiantGoldDark.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "REGISTRATION REFERENCE NUMBER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RadiantGoldDark,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = reg?.referenceNumber ?: "CHR-2026-CONFIRMED",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = RoyalNavyDark,
                            letterSpacing = 1.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Detail Rows
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Registrant's Name
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Registrant's Name:", fontSize = 13.sp, color = ChurchTextMuted)
                        Text(
                            text = user?.fullName ?: "Valued Member / Guest",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChurchTextPrimary
                        )
                    }

                    // Event Name
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Event Name:", fontSize = 13.sp, color = ChurchTextMuted)
                        Text(
                            text = event?.eventName ?: "Church Celebration",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalNavy
                        )
                    }

                    // Event Date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Event Date:", fontSize = 13.sp, color = ChurchTextMuted)
                        Text(
                            text = event?.eventDate ?: "Upcoming",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ChurchTextPrimary,
                            textAlign = TextAlign.End,
                            modifier = Modifier.width(180.dp)
                        )
                    }

                    // Venue
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Venue:", fontSize = 13.sp, color = ChurchTextMuted)
                        Text(
                            text = event?.venue ?: "Church Sanctuary",
                            fontSize = 13.sp,
                            color = ChurchTextSecondary,
                            textAlign = TextAlign.End,
                            modifier = Modifier.width(180.dp)
                        )
                    }

                    // Attendees Count
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Attendees Reserved:", fontSize = 13.sp, color = ChurchTextMuted)
                        Text(
                            text = "${reg?.attendeesCount ?: 1} person(s)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = RadiantGoldDark
                        )
                    }

                    HorizontalDivider(color = ChurchBorder)

                    // Date Registered
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Date Registered:", fontSize = 12.sp, color = ChurchTextMuted)
                        Text(
                            text = formattedRegDate,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ChurchTextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Email Confirmation Notification banner & toggle
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = if (emailNotificationSent) SuccessGreenLight else ChurchWhite,
            border = BorderStroke(1.dp, if (emailNotificationSent) SuccessGreen else ChurchBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (emailNotificationSent) Icons.Default.MarkEmailRead else Icons.Default.Email,
                        contentDescription = null,
                        tint = if (emailNotificationSent) SuccessGreen else RoyalNavyLight,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (emailNotificationSent) "Confirmation Email Sent!" else "Receive Email Confirmation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (emailNotificationSent) SuccessGreen else ChurchTextPrimary
                        )
                        Text(
                            text = user?.email ?: "To your registered email",
                            fontSize = 11.sp,
                            color = ChurchTextMuted
                        )
                    }
                }

                if (!emailNotificationSent) {
                    OutlinedButton(
                        onClick = {
                            emailNotificationSent = true
                            onSendEmailConfirmation(user?.email ?: "")
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("send_email_confirmation_button")
                    ) {
                        Text("Send Receipt", fontSize = 12.sp)
                    }
                } else {
                    Text(
                        text = "Delivered ✓",
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons: View Registration & Back to Dashboard
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onViewRegistration,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("confirmation_view_registration_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoyalNavy,
                    contentColor = ChurchWhite
                )
            ) {
                Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "View Registration",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            OutlinedButton(
                onClick = onBackToDashboard,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("confirmation_back_to_dashboard_button"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, RoyalNavyLight)
            ) {
                Icon(imageVector = Icons.Default.Dashboard, contentDescription = null, tint = RoyalNavyLight, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Back to Dashboard",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = RoyalNavyLight
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
