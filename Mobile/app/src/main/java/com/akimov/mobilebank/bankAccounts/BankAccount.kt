package com.akimov.mobilebank.bankAccounts

import java.math.BigDecimal
import java.util.UUID

data class BankAccount(
    val id: UUID,
    val name: String,
    val number: String,
    val balance: BigDecimal
)
