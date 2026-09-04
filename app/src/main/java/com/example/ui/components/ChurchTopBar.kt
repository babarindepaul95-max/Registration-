package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.User
import com.example.ui.theme.ChurchBorder
import com.example.ui.theme.ChurchTextMuted
import com.example.ui.theme.ChurchTextPrimary
import com.example.ui.theme.ChurchTextSecondary
import com.example.ui.theme.ChurchWhite
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RadiantGoldLight
import com.example.ui.theme.RoyalNavy
import com.example.ui.theme.WarmOrganicSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChurchTopBar(
    title: String,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null,
    currentUser: User? = null,
    onAdminClick: (() -> Unit)? = null,
    onLogoutClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = ChurchWhite,
                titleContentColor = ChurchTextPrimary,
                navigationIconContentColor = ChurchTextPrimary,
                actionIconContentColor = ChurchTextPrimary
            ),
            navigationIcon = {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("top_bar_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = RoyalNavy
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .padding(start = 14.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(WarmOrganicSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Church,
                            contentDescription = "Parish Sanctuary",
                            tint = RadiantGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            title = {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "SAINT ANDREWS PARISH",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = RadiantGold,
                        letterSpacing = 1.8.sp
                    )
                    Text(
                        text = title,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = ChurchTextPrimary,
                        lineHeight = 24.sp
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = ChurchTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            },
            actions = {
                if (currentUser != null) {
                    if (currentUser.role == "ADMIN" && onAdminClick != null) {
                        IconButton(
                            onClick = onAdminClick,
                            modifier = Modifier.testTag("top_bar_admin_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Portal",
                                tint = RadiantGold
                            )
                        }
                    }

                    // Cultural Avatar badge with member initials
                    val initials = currentUser.fullName
                        .split(" ")
                        .filter { it.isNotBlank() }
                        .take(2)
                        .map { it.first().uppercase() }
                        .joinToString("")
                        .ifEmpty { "ME" }

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(RoyalNavy)
                            .border(BorderStroke(1.5.dp, RadiantGold.copy(alpha = 0.4f)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = ChurchWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    if (onLogoutClick != null) {
                        IconButton(
                            onClick = onLogoutClick,
                            modifier = Modifier.testTag("top_bar_logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Logout",
                                tint = ChurchTextMuted
                            )
                        }
                    }
                }
            }
        )
        HorizontalDivider(color = ChurchBorder, thickness = 1.dp)
    }
}
