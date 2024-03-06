package com.akimov.mobilebank.data.models

import com.akimov.mobilebank.data.database.entity.BankAccountEntity
import java.math.BigDecimal
import java.util.UUID

data class BankAccountNetwork(
    val id: UUID,
    val name: String,
    val balance: BigDecimal,
    val number: String,
    val isClosed: Boolean
)

fun BankAccountNetwork.toEntity(): BankAccountEntity {
    return BankAccountEntity(
        id = id,
        name = name,
        balance = balance.toString(),
        number = number,
        isClosed = isClosed
    )
}