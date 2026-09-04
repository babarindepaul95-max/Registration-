package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChurchEventDao {
    @Query("SELECT * FROM events ORDER BY eventId ASC")
    fun getAllEvents(): Flow<List<ChurchEvent>>

    @Query("SELECT * FROM events WHERE eventId = :eventId LIMIT 1")
    fun getEventById(eventId: Long): Flow<ChurchEvent?>

    @Query("SELECT * FROM events WHERE eventId = :eventId LIMIT 1")
    suspend fun getEventByIdSync(eventId: Long): ChurchEvent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: ChurchEvent): Long

    @Update
    suspend fun updateEvent(event: ChurchEvent)

    @Query("DELETE FROM events WHERE eventId = :eventId")
    suspend fun deleteEvent(eventId: Long)

    @Query("UPDATE events SET status = :status WHERE eventId = :eventId")
    suspend fun updateEventStatus(eventId: Long, status: String)

    @Query("SELECT COUNT(*) FROM events")
    suspend fun getEventsCount(): Int
}
