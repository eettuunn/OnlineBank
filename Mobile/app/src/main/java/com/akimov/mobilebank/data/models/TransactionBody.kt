package com.akimov.mobilebank.data.models

data class TransactionBody(
    val amount: Int,
    val transactionType: String,
    val userId: String
)
