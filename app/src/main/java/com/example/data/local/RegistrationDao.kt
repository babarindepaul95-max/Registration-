package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistrationDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertRegistration(registration: Registration): Long

    @Transaction
    @Query("SELECT * FROM registrations WHERE userId = :userId ORDER BY registrationDate DESC")
    fun getRegistrationsByUser(userId: Long): Flow<List<RegistrationWithDetails>>

    @Transaction
    @Query("SELECT * FROM registrations ORDER BY registrationDate DESC")
    fun getAllRegistrationsWithDetails(): Flow<List<RegistrationWithDetails>>

    @Transaction
    @Query("SELECT * FROM registrations WHERE registrationId = :registrationId LIMIT 1")
    fun getRegistrationById(registrationId: Long): Flow<RegistrationWithDetails?>

    @Transaction
    @Query("SELECT * FROM registrations WHERE referenceNumber = :referenceNumber LIMIT 1")
    suspend fun getRegistrationByReference(referenceNumber: String): RegistrationWithDetails?

    @Query("SELECT * FROM registrations WHERE userId = :userId AND eventId = :eventId AND status != 'CANCELLED' LIMIT 1")
    suspend fun checkExistingRegistration(userId: Long, eventId: Long): Registration?

    @Query("SELECT COUNT(*) FROM registrations WHERE status != 'CANCELLED'")
    fun getTotalRegistrationsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM registrations WHERE eventId = :eventId AND status != 'CANCELLED'")
    fun getRegistrationsCountByEvent(eventId: Long): Flow<Int>

    @Query("SELECT SUM(attendeesCount) FROM registrations WHERE eventId = :eventId AND status != 'CANCELLED'")
    fun getTotalAttendeesForEvent(eventId: Long): Flow<Int?>

    @Query("DELETE FROM registrations WHERE registrationId = :registrationId")
    suspend fun deleteRegistration(registrationId: Long)

    @Query("UPDATE registrations SET status = :status WHERE registrationId = :registrationId")
    suspend fun updateRegistrationStatus(registrationId: Long, status: String)
}
