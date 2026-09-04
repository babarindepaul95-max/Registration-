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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.User
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UserProfileScreen(
    user: User,
    totalRegistrations: Int,
    onUpdateProfile: (String, String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editName by remember(user) { mutableStateOf(user.fullName) }
    var editPhone by remember(user) { mutableStateOf(user.phoneNumber) }

    val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val joinDate = dateFormat.format(Date(user.createdAt))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChurchBackgroundLight)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("user_profile_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Avatar and Name Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ChurchWhite),
            border = BorderStroke(1.dp, ChurchBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(RoyalNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.fullName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = RadiantGold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ChurchTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    color = GoldContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (user.role == "ADMIN") "CHURCH ADMINISTRATOR" else "ACTIVE MEMBER / GUEST",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RadiantGoldDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats quick counters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Registrations", fontSize = 12.sp, color = ChurchTextMuted)
                        Text("$totalRegistrations", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = RoyalNavy)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Member Since", fontSize = 12.sp, color = ChurchTextMuted)
                        Text(joinDate, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ChurchTextPrimary)
                    }
                }
            }
        }

        // Contact Information Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ChurchWhite),
            border = BorderStroke(1.dp, ChurchBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Account Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavy
                    )

                    TextButton(
                        onClick = {
                            if (isEditing) {
                                onUpdateProfile(editName, editPhone)
                                isEditing = false
                            } else {
                                isEditing = true
                            }
                        },
                        modifier = Modifier.testTag("edit_profile_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = null,
                            tint = RoyalNavyLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isEditing) "Save" else "Edit",
                            color = RoyalNavyLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                HorizontalDivider(color = ChurchBorder.copy(alpha = 0.5f))

                if (isEditing) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_name_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_phone_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = RoyalNavyLight, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Full Name", fontSize = 11.sp, color = ChurchTextMuted)
                            Text(user.fullName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ChurchTextPrimary)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = RoyalNavyLight, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Phone Number", fontSize = 11.sp, color = ChurchTextMuted)
                            Text(user.phoneNumber, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ChurchTextPrimary)
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = RoyalNavyLight, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Email Address (Primary Login)", fontSize = 11.sp, color = ChurchTextMuted)
                        Text(user.email, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ChurchTextPrimary)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Badge, contentDescription = null, tint = RadiantGoldDark, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("User Identifier", fontSize = 11.sp, color = ChurchTextMuted)
                        Text("ID: #${user.userId} • Verified Account", fontSize = 13.sp, color = ChurchTextSecondary)
                    }
                }
            }
        }
    }
}
