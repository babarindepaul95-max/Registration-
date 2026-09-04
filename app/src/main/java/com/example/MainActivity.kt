package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.DashboardTab
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthMode
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.EventRegistrationScreen
import com.example.ui.screens.LandingScreen
import com.example.ui.screens.RegistrationConfirmationScreen
import com.example.ui.screens.UserDashboardScreen
import com.example.ui.theme.ChurchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChurchTheme {
                ChurchApp()
            }
        }
    }
}

@Composable
fun ChurchApp(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val allEvents by viewModel.allEvents.collectAsState()
    val userRegistrations by viewModel.userRegistrations.collectAsState()
    val allRegistrations by viewModel.allRegistrations.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val adminStats by viewModel.adminStats.collectAsState()
    val currentDashboardTab by viewModel.currentDashboardTab.collectAsState()
    val latestRegistration by viewModel.latestRegistration.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()
    val emailNotifications by viewModel.emailNotificationsEnabled.collectAsState()
    val smsReminders by viewModel.smsRemindersEnabled.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Landing -> {
                    LandingScreen(
                        events = allEvents,
                        onRegisterNowClick = {
                            if (currentUser != null) {
                                viewModel.navigateTo(Screen.UserDashboard(DashboardTab.AVAILABLE_EVENTS))
                            } else {
                                viewModel.navigateTo(Screen.SignUp)
                            }
                        },
                        onLoginClick = {
                            if (currentUser != null) {
                                viewModel.navigateTo(Screen.UserDashboard(DashboardTab.HOME))
                            } else {
                                viewModel.navigateTo(Screen.Login)
                            }
                        },
                        onEventSelected = { event ->
                            if (currentUser != null) {
                                viewModel.navigateTo(Screen.EventRegistration(event))
                            } else {
                                viewModel.showMessage("Please log in or create an account to register for ${event.eventName}.")
                                viewModel.navigateTo(Screen.Login)
                            }
                        },
                        onAdminPortalClick = {
                            if (currentUser?.role == "ADMIN") {
                                viewModel.navigateTo(Screen.AdminDashboard)
                            } else {
                                viewModel.showMessage("Please log in with church administrator credentials.")
                                viewModel.navigateTo(Screen.Login)
                            }
                        }
                    )
                }

                is Screen.Login -> {
                    BackHandler {
                        viewModel.navigateTo(Screen.Landing)
                    }
                    AuthScreen(
                        initialMode = AuthMode.LOGIN,
                        isLoading = isLoading,
                        onLogin = { emailOrPhone, password ->
                            viewModel.login(
                                emailOrPhone = emailOrPhone,
                                password = password,
                                onAdmin = { viewModel.navigateTo(Screen.AdminDashboard) },
                                onSuccess = { viewModel.navigateTo(Screen.UserDashboard(DashboardTab.HOME)) }
                            )
                        },
                        onRegister = { fullName, phone, email, pass, confirmPass ->
                            viewModel.register(fullName, phone, email, pass, confirmPass) {
                                viewModel.navigateTo(Screen.UserDashboard(DashboardTab.HOME))
                            }
                        },
                        onResetPassword = { email, pass, confirmPass ->
                            viewModel.resetPassword(email, pass, confirmPass) {}
                        },
                        onBackToLanding = {
                            viewModel.navigateTo(Screen.Landing)
                        }
                    )
                }

                is Screen.SignUp -> {
                    BackHandler {
                        viewModel.navigateTo(Screen.Landing)
                    }
                    AuthScreen(
                        initialMode = AuthMode.SIGN_UP,
                        isLoading = isLoading,
                        onLogin = { emailOrPhone, password ->
                            viewModel.login(
                                emailOrPhone = emailOrPhone,
                                password = password,
                                onAdmin = { viewModel.navigateTo(Screen.AdminDashboard) },
                                onSuccess = { viewModel.navigateTo(Screen.UserDashboard(DashboardTab.HOME)) }
                            )
                        },
                        onRegister = { fullName, phone, email, pass, confirmPass ->
                            viewModel.register(fullName, phone, email, pass, confirmPass) {
                                viewModel.navigateTo(Screen.UserDashboard(DashboardTab.HOME))
                            }
                        },
                        onResetPassword = { email, pass, confirmPass ->
                            viewModel.resetPassword(email, pass, confirmPass) {}
                        },
                        onBackToLanding = {
                            viewModel.navigateTo(Screen.Landing)
                        }
                    )
                }

                is Screen.ForgotPassword -> {
                    BackHandler {
                        viewModel.navigateTo(Screen.Login)
                    }
                    AuthScreen(
                        initialMode = AuthMode.FORGOT_PASSWORD,
                        isLoading = isLoading,
                        onLogin = { emailOrPhone, password ->
                            viewModel.login(emailOrPhone, password)
                        },
                        onRegister = { fullName, phone, email, pass, confirmPass ->
                            viewModel.register(fullName, phone, email, pass, confirmPass)
                        },
                        onResetPassword = { email, pass, confirmPass ->
                            viewModel.resetPassword(email, pass, confirmPass) {}
                        },
                        onBackToLanding = {
                            viewModel.navigateTo(Screen.Landing)
                        }
                    )
                }

                is Screen.UserDashboard -> {
                    val user = currentUser
                    if (user == null) {
                        viewModel.navigateTo(Screen.Login)
                    } else {
                        UserDashboardScreen(
                            user = user,
                            currentTab = currentDashboardTab,
                            events = allEvents,
                            userRegistrations = userRegistrations,
                            onTabSelected = { tab ->
                                viewModel.setDashboardTab(tab)
                            },
                            onRegisterEventClick = { event ->
                                viewModel.navigateTo(Screen.EventRegistration(event))
                            },
                            onViewRegistrationClick = { regWithDetails ->
                                viewModel.navigateTo(
                                    Screen.RegistrationConfirmation(
                                        regWithDetails.registration.registrationId,
                                        regWithDetails.registration.referenceNumber
                                    )
                                )
                            },
                            onCancelRegistration = { regId ->
                                viewModel.cancelUserRegistration(regId)
                            },
                            onUpdateProfile = { name, phone ->
                                viewModel.updateProfile(name, phone)
                            },
                            emailNotifications = emailNotifications,
                            onToggleEmailNotifications = {
                                viewModel.emailNotificationsEnabled.value = it
                                viewModel.showMessage("Email notifications ${if (it) "enabled" else "disabled"}.")
                            },
                            smsReminders = smsReminders,
                            onToggleSmsReminders = {
                                viewModel.smsRemindersEnabled.value = it
                                viewModel.showMessage("SMS reminders ${if (it) "enabled" else "disabled"}.")
                            },
                            onResetPassword = { email, newPass, confirmPass, onSuccess ->
                                viewModel.resetPassword(email, newPass, confirmPass, onSuccess)
                            },
                            onAdminPortalClick = {
                                viewModel.navigateTo(Screen.AdminDashboard)
                            },
                            onLogout = {
                                viewModel.logout()
                            }
                        )
                    }
                }

                is Screen.EventRegistration -> {
                    BackHandler {
                        viewModel.navigateTo(Screen.UserDashboard(DashboardTab.AVAILABLE_EVENTS))
                    }
                    val user = currentUser
                    if (user == null) {
                        viewModel.navigateTo(Screen.Login)
                    } else {
                        EventRegistrationScreen(
                            event = screen.event,
                            currentUser = user,
                            isLoading = isLoading,
                            onSubmitRegistration = { event, count, comments ->
                                viewModel.submitRegistration(event, count, comments)
                            },
                            onBackClick = {
                                viewModel.navigateTo(Screen.UserDashboard(DashboardTab.AVAILABLE_EVENTS))
                            }
                        )
                    }
                }

                is Screen.RegistrationConfirmation -> {
                    BackHandler {
                        viewModel.navigateTo(Screen.UserDashboard(DashboardTab.MY_REGISTRATIONS))
                    }
                    val regDetail = latestRegistration
                        ?: userRegistrations.firstOrNull { it.registration.registrationId == screen.registrationId }
                        ?: allRegistrations.firstOrNull { it.registration.registrationId == screen.registrationId }

                    RegistrationConfirmationScreen(
                        registrationDetails = regDetail,
                        onViewRegistration = {
                            viewModel.setDashboardTab(DashboardTab.MY_REGISTRATIONS)
                            viewModel.navigateTo(Screen.UserDashboard(DashboardTab.MY_REGISTRATIONS))
                        },
                        onBackToDashboard = {
                            viewModel.setDashboardTab(DashboardTab.HOME)
                            viewModel.navigateTo(Screen.UserDashboard(DashboardTab.HOME))
                        },
                        onSendEmailConfirmation = { email ->
                            Toast.makeText(context, "Confirmation sent to $email", Toast.LENGTH_SHORT).show()
                            viewModel.showMessage("Confirmation email receipt sent to $email.")
                        }
                    )
                }

                is Screen.RegistrationDetail -> {
                    BackHandler {
                        viewModel.navigateTo(Screen.UserDashboard(DashboardTab.MY_REGISTRATIONS))
                    }
                    RegistrationConfirmationScreen(
                        registrationDetails = screen.registration,
                        onViewRegistration = {
                            viewModel.navigateTo(Screen.UserDashboard(DashboardTab.MY_REGISTRATIONS))
                        },
                        onBackToDashboard = {
                            viewModel.navigateTo(Screen.UserDashboard(DashboardTab.HOME))
                        },
                        onSendEmailConfirmation = { email ->
                            Toast.makeText(context, "Confirmation sent to $email", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                is Screen.AdminDashboard -> {
                    BackHandler {
                        if (currentUser != null) {
                            viewModel.navigateTo(Screen.UserDashboard(DashboardTab.HOME))
                        } else {
                            viewModel.navigateTo(Screen.Landing)
                        }
                    }
                    AdminDashboardScreen(
                        stats = adminStats,
                        events = allEvents,
                        registrations = allRegistrations,
                        users = allUsers,
                        onSaveEvent = { id, name, category, desc, date, venue, status ->
                            viewModel.saveEvent(id, name, category, desc, date, venue, status)
                        },
                        onDeleteEvent = { eventId ->
                            viewModel.deleteEvent(eventId)
                        },
                        onToggleEventStatus = { event ->
                            viewModel.toggleEventRegistrationStatus(event)
                        },
                        onDeleteUser = { userId ->
                            viewModel.deleteUserAccount(userId)
                        },
                        onBackClick = {
                            if (currentUser != null) {
                                viewModel.navigateTo(Screen.UserDashboard(DashboardTab.HOME))
                            } else {
                                viewModel.navigateTo(Screen.Landing)
                            }
                        }
                    )
                }
            }
        }
    }
}

