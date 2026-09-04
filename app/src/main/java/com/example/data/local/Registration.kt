package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "registrations",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ChurchEvent::class,
            parentColumns = ["eventId"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["eventId"])
    ]
)
data class Registration(
    @PrimaryKey(autoGenerate = true)
    val registrationId: Long = 0,
    val referenceNumber: String,
    val userId: Long,
    val eventId: Long,
    val attendeesCount: Int = 1,
    val registrationDate: Long = System.currentTimeMillis(),
    val comments: String = "",
    val status: String = "CONFIRMED" // "CONFIRMED", "CANCELLED"
)
