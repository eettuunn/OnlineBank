package com.akimov.mobilebank.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loan_rates")
data class LoanRateEntity(
    @PrimaryKey
    val id: String,
    val interestRate: Double
)
