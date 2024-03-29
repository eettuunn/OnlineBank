package com.akimov.mobilebank.domain

import com.akimov.mobilebank.data.models.OperationType

data class OperationUI(
    val id: String,
    val transactionDate: String,
    val amount: Int,
    val additionalInformation: String,
    val transactionType: OperationType,
    val bankAccountId: String
)
