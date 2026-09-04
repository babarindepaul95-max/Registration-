package com.example.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class RegistrationWithDetails(
    @Embedded
    val registration: Registration,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val user: User,
    @Relation(
        parentColumn = "eventId",
        entityColumn = "eventId"
    )
    val event: ChurchEvent
)
