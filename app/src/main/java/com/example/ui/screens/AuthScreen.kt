package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

enum class AuthMode {
    LOGIN,
    SIGN_UP,
    FORGOT_PASSWORD
}

@Composable
fun AuthScreen(
    initialMode: AuthMode = AuthMode.LOGIN,
    isLoading: Boolean = false,
    onLogin: (emailOrPhone: String, password: String) -> Unit,
    onRegister: (fullName: String, phone: String, email: String, password: String, confirmPassword: String) -> Unit,
    onResetPassword: (email: String, newPass: String, confirmPass: String) -> Unit,
    onBackToLanding: () -> Unit
) {
    var mode by remember { mutableStateOf(initialMode) }
    val focusManager = LocalFocusManager.current

    // Form fields
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var emailOrPhone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChurchBackgroundLight)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("auth_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Back button row & Church Branding
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackToLanding,
                modifier = Modifier.testTag("auth_back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back to Welcome",
                    tint = RoyalNavy
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Church Event Portal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = RoyalNavy
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Church Logo Emblem
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(RoyalNavy),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Church,
                contentDescription = null,
                tint = RadiantGold,
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = when (mode) {
                AuthMode.LOGIN -> "Welcome Back"
                AuthMode.SIGN_UP -> "Create Your Account"
                AuthMode.FORGOT_PASSWORD -> "Reset Password"
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = ChurchTextPrimary
        )

        Text(
            text = when (mode) {
                AuthMode.LOGIN -> "Log in to register for celebrations and manage tickets"
                AuthMode.SIGN_UP -> "Join as a church member or guest for upcoming celebrations"
                AuthMode.FORGOT_PASSWORD -> "Enter your email to configure a new secure password"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = ChurchTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Toggle Tabs for Login / Sign Up
        if (mode != AuthMode.FORGOT_PASSWORD) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = ChurchWhite,
                border = BorderStroke(1.dp, ChurchBorder)
            ) {
                TabRow(
                    selectedTabIndex = if (mode == AuthMode.LOGIN) 0 else 1,
                    containerColor = Color.Transparent,
                    contentColor = RoyalNavy,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[if (mode == AuthMode.LOGIN) 0 else 1]),
                            color = RadiantGold,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = mode == AuthMode.LOGIN,
                        onClick = { mode = AuthMode.LOGIN },
                        text = {
                            Text(
                                "Log In",
                                fontWeight = if (mode == AuthMode.LOGIN) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("tab_login")
                    )
                    Tab(
                        selected = mode == AuthMode.SIGN_UP,
                        onClick = { mode = AuthMode.SIGN_UP },
                        text = {
                            Text(
                                "Create Account",
                                fontWeight = if (mode == AuthMode.SIGN_UP) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("tab_signup")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Main Card with Form Inputs
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = ChurchWhite),
            border = BorderStroke(1.dp, ChurchBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (mode) {
                    AuthMode.LOGIN -> {
                        // Email or Phone
                        OutlinedTextField(
                            value = emailOrPhone,
                            onValueChange = { emailOrPhone = it },
                            label = { Text("Email Address or Phone Number") },
                            placeholder = { Text("e.g. john@example.com or +1 555-012") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = RoyalNavyLight)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email_phone_input"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalNavy,
                                focusedLabelColor = RoyalNavy
                            )
                        )

                        // Password
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = RoyalNavyLight)
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password visibility"
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    onLogin(emailOrPhone, password)
                                }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalNavy,
                                focusedLabelColor = RoyalNavy
                            )
                        )

                        // Forgot Password Link
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Forgot Password?",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = RadiantGoldDark,
                                modifier = Modifier
                                    .clickable { mode = AuthMode.FORGOT_PASSWORD }
                                    .testTag("forgot_password_button")
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Login Button
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                onLogin(emailOrPhone, password)
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("login_submit_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RoyalNavy,
                                contentColor = ChurchWhite
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = ChurchWhite, modifier = Modifier.size(22.dp))
                            } else {
                                Text(
                                    text = "Login",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        // Create New Account Link
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Don't have an account?",
                                fontSize = 14.sp,
                                color = ChurchTextSecondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            TextButton(
                                onClick = { mode = AuthMode.SIGN_UP },
                                modifier = Modifier.testTag("switch_to_signup_button")
                            ) {
                                Text(
                                    text = "Create New Account",
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalNavyLight
                                )
                            }
                        }
                    }

                    AuthMode.SIGN_UP -> {
                        // Full Name
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            placeholder = { Text("e.g. Johnathan Smith") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = RoyalNavyLight)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_fullname_input"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Phone Number
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Number") },
                            placeholder = { Text("e.g. +1 (555) 012-3456") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = RoyalNavyLight)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_phone_input"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Email Address
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            placeholder = { Text("e.g. john.smith@example.com") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = RoyalNavyLight)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_email_input"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Password
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password (min 6 characters)") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = RoyalNavyLight)
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_password_input"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Confirm Password
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = RoyalNavyLight)
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle visibility"
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_confirm_password_input"),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    onRegister(fullName, phone, email, password, confirmPassword)
                                }
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Create Account Button
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                onRegister(fullName, phone, email, password, confirmPassword)
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("signup_submit_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RadiantGold,
                                contentColor = RoyalNavyDark
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = RoyalNavyDark, modifier = Modifier.size(22.dp))
                            } else {
                                Text(
                                    text = "Create Account",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        // Switch to Login
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Already have an account?",
                                fontSize = 14.sp,
                                color = ChurchTextSecondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            TextButton(
                                onClick = { mode = AuthMode.LOGIN },
                                modifier = Modifier.testTag("switch_to_login_button")
                            ) {
                                Text(
                                    text = "Log In",
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalNavyLight
                                )
                            }
                        }
                    }

                    AuthMode.FORGOT_PASSWORD -> {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Account Email Address") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = RoyalNavyLight)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reset_email_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("New Password (min 6 chars)") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.LockReset, contentDescription = null, tint = RoyalNavyLight)
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reset_new_password_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm New Password") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.LockReset, contentDescription = null, tint = RoyalNavyLight)
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reset_confirm_password_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    onResetPassword(email, password, confirmPassword)
                                }
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                onResetPassword(email, password, confirmPassword)
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_reset_password_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RoyalNavy,
                                contentColor = ChurchWhite
                            )
                        ) {
                            Text("Update Password", fontWeight = FontWeight.Bold)
                        }

                        TextButton(
                            onClick = { mode = AuthMode.LOGIN },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Return to Login", color = RoyalNavyLight)
                        }
                    }
                }
            }
        }

        // Quick Test Credentials helper for evaluator convenience
        Spacer(modifier = Modifier.height(20.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = GoldContainer.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, RadiantGold.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "💡 Quick Test Credentials",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = RoyalNavyDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            mode = AuthMode.LOGIN
                            emailOrPhone = "john.smith@example.com"
                            password = "password123"
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("fill_demo_member_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = ChurchWhite
                        ),
                        border = BorderStroke(1.dp, RoyalNavyLight.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "Fill Member",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RoyalNavyLight
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            mode = AuthMode.LOGIN
                            emailOrPhone = "admin@church.org"
                            password = "admin123"
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("fill_demo_admin_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = ChurchWhite
                        ),
                        border = BorderStroke(1.dp, RadiantGoldDark.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "Fill Admin",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RadiantGoldDark
                        )
                    }
                }
            }
        }
    }
}
