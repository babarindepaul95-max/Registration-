package com.example.data.repository

import com.example.data.local.ChurchDatabase
import com.example.data.local.ChurchEvent
import com.example.data.local.ChurchEventDao
import com.example.data.local.Registration
import com.example.data.local.RegistrationDao
import com.example.data.local.RegistrationWithDetails
import com.example.data.local.User
import com.example.data.local.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.random.Random

class ChurchRepository(
    private val userDao: UserDao,
    private val churchEventDao: ChurchEventDao,
    private val registrationDao: RegistrationDao
) {
    val allEvents: Flow<List<ChurchEvent>> = churchEventDao.getAllEvents()
    val allUsers: Flow<List<User>> = userDao.getAllUsers()
    val totalUsersCount: Flow<Int> = userDao.getTotalUsersCount()
    val totalRegistrationsCount: Flow<Int> = registrationDao.getTotalRegistrationsCount()
    val allRegistrations: Flow<List<RegistrationWithDetails>> = registrationDao.getAllRegistrationsWithDetails()

    fun getUserById(userId: Long): Flow<User?> = userDao.getUserById(userId)

    fun getUserRegistrations(userId: Long): Flow<List<RegistrationWithDetails>> =
        registrationDao.getRegistrationsByUser(userId)

    fun getEventRegistrationsCount(eventId: Long): Flow<Int> =
        registrationDao.getRegistrationsCountByEvent(eventId)

    fun getEventTotalAttendees(eventId: Long): Flow<Int?> =
        registrationDao.getTotalAttendeesForEvent(eventId)

    suspend fun registerUser(
        fullName: String,
        phone: String,
        email: String,
        password: String
    ): Result<User> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim().lowercase()
        val trimmedPhone = phone.trim()

        if (userDao.getUserByEmail(trimmedEmail) != null) {
            return@withContext Result.failure(IllegalArgumentException("An account with this email address already exists."))
        }

        if (userDao.getUserByPhone(trimmedPhone) != null) {
            return@withContext Result.failure(IllegalArgumentException("An account with this phone number already exists."))
        }

        val newUser = User(
            fullName = fullName.trim(),
            phoneNumber = trimmedPhone,
            email = trimmedEmail,
            passwordHash = ChurchDatabase.hashPassword(password),
            role = "USER"
        )
        val id = userDao.insertUser(newUser)
        Result.success(newUser.copy(userId = id))
    }

    suspend fun login(
        emailOrPhone: String,
        password: String
    ): Result<User> = withContext(Dispatchers.IO) {
        val query = emailOrPhone.trim()
        val user = if (query.contains("@")) {
            userDao.getUserByEmail(query.lowercase())
        } else {
            userDao.getUserByPhone(query) ?: userDao.getUserByEmail(query.lowercase())
        }

        if (user == null) {
            return@withContext Result.failure(IllegalArgumentException("No account found with this email or phone number."))
        }

        val hash = ChurchDatabase.hashPassword(password)
        if (user.passwordHash != hash) {
            return@withContext Result.failure(IllegalArgumentException("Incorrect password. Please verify and try again."))
        }

        Result.success(user)
    }

    suspend fun resetPassword(
        email: String,
        newPassword: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim().lowercase()
        val user = userDao.getUserByEmail(trimmedEmail)
            ?: return@withContext Result.failure(IllegalArgumentException("No account found with that email address."))

        val newHash = ChurchDatabase.hashPassword(newPassword)
        userDao.updatePassword(user.userId, newHash)
        Result.success(Unit)
    }

    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(userId: Long) = withContext(Dispatchers.IO) {
        userDao.deleteUser(userId)
    }

    // Event operations
    suspend fun createEvent(event: ChurchEvent): Long = withContext(Dispatchers.IO) {
        churchEventDao.insertEvent(event)
    }

    suspend fun updateEvent(event: ChurchEvent) = withContext(Dispatchers.IO) {
        churchEventDao.updateEvent(event)
    }

    suspend fun deleteEvent(eventId: Long) = withContext(Dispatchers.IO) {
        churchEventDao.deleteEvent(eventId)
    }

    suspend fun toggleEventStatus(eventId: Long, currentStatus: String) = withContext(Dispatchers.IO) {
        val newStatus = if (currentStatus.equals("OPEN", ignoreCase = true)) "CLOSED" else "OPEN"
        churchEventDao.updateEventStatus(eventId, newStatus)
    }

    // Registration operations
    suspend fun registerForEvent(
        userId: Long,
        eventId: Long,
        attendeesCount: Int,
        comments: String
    ): Result<Registration> = withContext(Dispatchers.IO) {
        val existing = registrationDao.checkExistingRegistration(userId, eventId)
        if (existing != null) {
            return@withContext Result.failure(
                IllegalStateException("You are already registered for this event (Ref: ${existing.referenceNumber}).")
            )
        }

        val event = churchEventDao.getEventByIdSync(eventId)
        if (event == null || !event.status.equals("OPEN", ignoreCase = true)) {
            return@withContext Result.failure(IllegalStateException("Registration for this event is currently closed."))
        }

        val randomSuffix = Random.nextInt(1000, 9999)
        val year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        val refNumber = "CHR-$year-$randomSuffix"

        val reg = Registration(
            referenceNumber = refNumber,
            userId = userId,
            eventId = eventId,
            attendeesCount = attendeesCount,
            registrationDate = System.currentTimeMillis(),
            comments = comments.trim(),
            status = "CONFIRMED"
        )
        val id = registrationDao.insertRegistration(reg)
        Result.success(reg.copy(registrationId = id))
    }

    suspend fun cancelRegistration(registrationId: Long) = withContext(Dispatchers.IO) {
        registrationDao.updateRegistrationStatus(registrationId, "CANCELLED")
    }

    suspend fun deleteRegistration(registrationId: Long) = withContext(Dispatchers.IO) {
        registrationDao.deleteRegistration(registrationId)
    }
}
