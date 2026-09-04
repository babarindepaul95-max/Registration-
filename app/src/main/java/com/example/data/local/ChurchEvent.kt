package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class ChurchEvent(
    @PrimaryKey(autoGenerate = true)
    val eventId: Long = 0,
    val eventName: String,
    val category: String, // "CHILDREN", "ADULT", "WOMEN", "HARVEST", "GENERAL"
    val description: String,
    val eventDate: String,
    val venue: String,
    val status: String = "OPEN", // "OPEN", "CLOSED"
    val capacity: Int = 250,
    val createdAt: Long = System.currentTimeMillis()
)
