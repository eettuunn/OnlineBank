package com.akimov.mobilebank.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "credits")
data class CreditEntity(
    @PrimaryKey
    val id: UUID,
    val debt: Int,
    val monthlyPayment: Float,
    val bankAccountId: UUID
)