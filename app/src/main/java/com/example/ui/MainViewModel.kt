package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ChurchDatabase
import com.example.data.local.ChurchEvent
import com.example.data.local.Registration
import com.example.data.local.RegistrationWithDetails
import com.example.data.local.User
import com.example.data.repository.ChurchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class Screen {
    object Landing : Screen()
    object Login : Screen()
    object SignUp : Screen()
    object ForgotPassword : Screen()
    data class UserDashboard(val initialTab: DashboardTab = DashboardTab.HOME) : Screen()
    data class EventRegistration(val event: ChurchEvent) : Screen()
    data class RegistrationConfirmation(val registrationId: Long, val refNumber: String) : Screen()
    data class RegistrationDetail(val registration: RegistrationWithDetails) : Screen()
    object AdminDashboard : Screen()
}

enum class DashboardTab {
    HOME,
    AVAILABLE_EVENTS,
    MY_REGISTRATIONS,
    MY_PROFILE,
    SETTINGS
}

enum class AdminTab {
    OVERVIEW,
    EVENTS,
    REGISTRATIONS,
    USERS
}

data class DashboardStats(
    val totalUsers: Int = 0,
    val totalRegistrations: Int = 0,
    val childrenPartyCount: Int = 0,
    val adultsPartyCount: Int = 0,
    val womenPartyCount: Int = 0,
    val generalHarvestCount: Int = 0
)

data class AdminStats(
    val totalUsers: Int = 0,
    val totalRegistrations: Int = 0,
    val childrenRegistrations: Int = 0,
    val adultRegistrations: Int = 0,
    val womenRegistrations: Int = 0,
    val harvestRegistrations: Int = 0
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ChurchDatabase.getDatabase(application, viewModelScope)
    val repository = ChurchRepository(
        database.userDao(),
        database.churchEventDao(),
        database.registrationDao()
    )

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Landing)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentDashboardTab = MutableStateFlow(DashboardTab.HOME)
    val currentDashboardTab: StateFlow<DashboardTab> = _currentDashboardTab.asStateFlow()

    private val _currentAdminTab = MutableStateFlow(AdminTab.OVERVIEW)
    val currentAdminTab: StateFlow<AdminTab> = _currentAdminTab.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    val allEvents: StateFlow<List<ChurchEvent>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRegistrations: StateFlow<List<RegistrationWithDetails>> = repository.allRegistrations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _userRegistrations = MutableStateFlow<List<RegistrationWithDetails>>(emptyList())
    val userRegistrations: StateFlow<List<RegistrationWithDetails>> = _userRegistrations.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Registration confirmation data
    private val _latestRegistration = MutableStateFlow<RegistrationWithDetails?>(null)
    val latestRegistration: StateFlow<RegistrationWithDetails?> = _latestRegistration.asStateFlow()

    // Settings preferences
    val emailNotificationsEnabled = MutableStateFlow(true)
    val smsRemindersEnabled = MutableStateFlow(true)

    // Admin and general statistics
    val dashboardStats: StateFlow<DashboardStats> = combine(
        repository.totalUsersCount,
        allRegistrations
    ) { usersCount, registrations ->
        var children = 0
        var adults = 0
        var women = 0
        var harvest = 0
        var totalActive = 0

        for (item in registrations) {
            if (item.registration.status != "CANCELLED") {
                totalActive += item.registration.attendeesCount
                when (item.event.category.uppercase(Locale.ROOT)) {
                    "CHILDREN" -> children += item.registration.attendeesCount
                    "ADULT" -> adults += item.registration.attendeesCount
                    "WOMEN" -> women += item.registration.attendeesCount
                    "HARVEST" -> harvest += item.registration.attendeesCount
                }
            }
        }

        DashboardStats(
            totalUsers = usersCount,
            totalRegistrations = registrations.count { it.registration.status != "CANCELLED" },
            childrenPartyCount = children,
            adultsPartyCount = adults,
            womenPartyCount = women,
            generalHarvestCount = harvest
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    val adminStats: StateFlow<AdminStats> = combine(
        repository.totalUsersCount,
        allRegistrations
    ) { usersCount, registrations ->
        var children = 0
        var adults = 0
        var women = 0
        var harvest = 0

        for (item in registrations) {
            if (item.registration.status != "CANCELLED") {
                when (item.event.category.uppercase(Locale.ROOT)) {
                    "CHILDREN" -> children += item.registration.attendeesCount
                    "ADULT" -> adults += item.registration.attendeesCount
                    "WOMEN" -> women += item.registration.attendeesCount
                    "HARVEST" -> harvest += item.registration.attendeesCount
                }
            }
        }

        AdminStats(
            totalUsers = usersCount,
            totalRegistrations = registrations.count { it.registration.status != "CANCELLED" },
            childrenRegistrations = children,
            adultRegistrations = adults,
            womenRegistrations = women,
            harvestRegistrations = harvest
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminStats())

    init {
        // Observe registrations for active logged in user
        viewModelScope.launch {
            _currentUser.collect { user ->
                if (user != null) {
                    repository.getUserRegistrations(user.userId).collect { list ->
                        _userRegistrations.value = list
                    }
                } else {
                    _userRegistrations.value = emptyList()
                }
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun setDashboardTab(tab: DashboardTab) {
        _currentDashboardTab.value = tab
    }

    fun setAdminTab(tab: AdminTab) {
        _currentAdminTab.value = tab
    }

    fun showMessage(message: String) {
        _uiMessage.value = message
    }

    fun clearMessage() {
        _uiMessage.value = null
    }

    // --- Authentication ---

    fun login(emailOrPhone: String, password: String, onAdmin: () -> Unit = {}, onSuccess: () -> Unit = {}) {
        if (emailOrPhone.isBlank() || password.isBlank()) {
            showMessage("Please fill in both your email/phone and password.")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.login(emailOrPhone, password)
            _isLoading.value = false

            result.onSuccess { user ->
                _currentUser.value = user
                showMessage("Welcome back, ${user.fullName}!")
                if (user.role == "ADMIN") {
                    _currentScreen.value = Screen.AdminDashboard
                    onAdmin()
                } else {
                    _currentScreen.value = Screen.UserDashboard(DashboardTab.HOME)
                    onSuccess()
                }
            }.onFailure { error ->
                showMessage(error.message ?: "Authentication failed. Please check credentials.")
            }
        }
    }

    fun register(
        fullName: String,
        phone: String,
        email: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit = {}
    ) {
        if (fullName.trim().length < 3) {
            showMessage("Please enter your full name (minimum 3 characters).")
            return
        }

        val phoneClean = phone.trim()
        if (phoneClean.length < 7 || !phoneClean.any { it.isDigit() }) {
            showMessage("Please enter a valid phone number.")
            return
        }

        val emailClean = email.trim()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailClean).matches()) {
            showMessage("Please enter a valid email address.")
            return
        }

        if (password.length < 6) {
            showMessage("Password must be at least 6 characters long.")
            return
        }

        if (password != confirmPassword) {
            showMessage("Passwords do not match. Please re-enter.")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.registerUser(fullName, phoneClean, emailClean, password)
            _isLoading.value = false

            result.onSuccess { user ->
                _currentUser.value = user
                showMessage("Account created successfully! Welcome to the portal.")
                _currentScreen.value = Screen.UserDashboard(DashboardTab.HOME)
                onSuccess()
            }.onFailure { error ->
                showMessage(error.message ?: "Registration error. Account may already exist.")
            }
        }
    }

    fun resetPassword(email: String, newPass: String, confirmPass: String, onSuccess: () -> Unit) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            showMessage("Please enter a valid email address.")
            return
        }
        if (newPass.length < 6) {
            showMessage("New password must be at least 6 characters long.")
            return
        }
        if (newPass != confirmPass) {
            showMessage("Passwords do not match.")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.resetPassword(email.trim(), newPass)
            _isLoading.value = false

            result.onSuccess {
                showMessage("Password updated successfully! Please log in.")
                _currentScreen.value = Screen.Login
                onSuccess()
            }.onFailure { error ->
                showMessage(error.message ?: "Could not reset password.")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentScreen.value = Screen.Landing
        _currentDashboardTab.value = DashboardTab.HOME
        showMessage("Logged out successfully.")
    }

    // --- User Profile ---

    fun updateProfile(fullName: String, phone: String) {
        val user = _currentUser.value ?: return
        if (fullName.isBlank()) {
            showMessage("Name cannot be empty.")
            return
        }
        val updated = user.copy(fullName = fullName.trim(), phoneNumber = phone.trim())
        viewModelScope.launch {
            repository.updateUser(updated)
            _currentUser.value = updated
            showMessage("Profile updated successfully.")
        }
    }

    // --- Event Registration ---

    fun submitRegistration(
        event: ChurchEvent,
        attendeesCount: Int,
        comments: String
    ) {
        val user = _currentUser.value
        if (user == null) {
            showMessage("Please log in to complete event registration.")
            _currentScreen.value = Screen.Login
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.registerForEvent(
                userId = user.userId,
                eventId = event.eventId,
                attendeesCount = attendeesCount,
                comments = comments
            )
            _isLoading.value = false

            result.onSuccess { reg ->
                val details = RegistrationWithDetails(
                    registration = reg,
                    user = user,
                    event = event
                )
                _latestRegistration.value = details
                _currentScreen.value = Screen.RegistrationConfirmation(reg.registrationId, reg.referenceNumber)
                showMessage("Registration Confirmed! Reference: ${reg.referenceNumber}")
            }.onFailure { error ->
                showMessage(error.message ?: "Registration failed.")
            }
        }
    }

    fun cancelUserRegistration(registrationId: Long) {
        viewModelScope.launch {
            repository.cancelRegistration(registrationId)
            showMessage("Registration has been cancelled.")
        }
    }

    // --- Admin Event Operations ---

    fun createEvent(
        name: String,
        category: String,
        description: String,
        date: String,
        venue: String,
        capacity: Int,
        onSuccess: () -> Unit
    ) {
        if (name.isBlank() || description.isBlank() || date.isBlank() || venue.isBlank()) {
            showMessage("Please fill in all event details.")
            return
        }

        viewModelScope.launch {
            val newEvent = ChurchEvent(
                eventName = name.trim(),
                category = category,
                description = description.trim(),
                eventDate = date.trim(),
                venue = venue.trim(),
                capacity = capacity,
                status = "OPEN"
            )
            repository.createEvent(newEvent)
            showMessage("Event '${newEvent.eventName}' created successfully!")
            onSuccess()
        }
    }

    fun updateEvent(event: ChurchEvent, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.updateEvent(event)
            showMessage("Event '${event.eventName}' updated successfully!")
            onSuccess()
        }
    }

    fun saveEvent(
        eventId: Long,
        name: String,
        category: String,
        desc: String,
        date: String,
        venue: String,
        status: String
    ) {
        if (eventId == 0L) {
            createEvent(name, category, desc, date, venue, 500) {}
        } else {
            val updated = ChurchEvent(
                eventId = eventId,
                eventName = name,
                category = category,
                description = desc,
                eventDate = date,
                venue = venue,
                capacity = 500,
                status = status
            )
            updateEvent(updated) {}
        }
    }

    fun deleteEvent(eventId: Long) {
        viewModelScope.launch {
            repository.deleteEvent(eventId)
            showMessage("Event deleted successfully.")
        }
    }

    fun toggleEventRegistrationStatus(event: ChurchEvent) {
        viewModelScope.launch {
            repository.toggleEventStatus(event.eventId, event.status)
            val updatedStatus = if (event.status.equals("OPEN", ignoreCase = true)) "CLOSED" else "OPEN"
            showMessage("Registration for '${event.eventName}' is now $updatedStatus.")
        }
    }

    fun deleteUserAccount(userId: Long) {
        viewModelScope.launch {
            repository.deleteUser(userId)
            showMessage("User account removed.")
        }
    }

    // --- CSV Export ---

    fun exportRegistrationsToCsv(context: Context) {
        val list = allRegistrations.value
        if (list.isEmpty()) {
            showMessage("No registration records found to export.")
            return
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val csvHeader = "Reference ID,Event Name,Category,Event Date,Registrant Name,Email,Phone,Attendees,Registration Date,Status,Comments\n"
        val csvBody = list.joinToString("\n") { item ->
            val reg = item.registration
            val regDate = dateFormat.format(Date(reg.registrationDate))
            val safeComment = reg.comments.replace("\"", "\"\"").replace("\n", " ")
            "\"${reg.referenceNumber}\",\"${item.event.eventName}\",\"${item.event.category}\",\"${item.event.eventDate}\",\"${item.user.fullName}\",\"${item.user.email}\",\"${item.user.phoneNumber}\",${reg.attendeesCount},\"$regDate\",\"${reg.status}\",\"$safeComment\""
        }
        val fullCsv = csvHeader + csvBody

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, fullCsv)
            putExtra(Intent.EXTRA_SUBJECT, "Church_Event_Registrations_${System.currentTimeMillis()}.csv")
            type = "text/csv"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Export Registrations CSV")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
        showMessage("Export file generated (${list.size} records)!")
    }
}
