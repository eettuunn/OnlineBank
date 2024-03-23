package com.akimov.mobilebank.data.models

data class GetLoanUpload(
    val months: Int,
    val loanAmount: Int,
    val loanRateId: String,
    val userId: String,
    val bankAccountId: String
)