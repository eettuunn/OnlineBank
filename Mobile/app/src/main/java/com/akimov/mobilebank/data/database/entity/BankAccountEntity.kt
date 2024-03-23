package com.akimov.mobilebank.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akimov.mobilebank.data.models.BankAccountNetwork
import java.util.UUID

@Entity(tableName = "bank_accounts")
data class BankAccountEntity(
    @PrimaryKey
    val id: UUID,
    val name: String,
    val balance: String,
    val number: String,
    val isClosed: Boolean
)

fun BankAccountEntity.toNetworkAccount(): BankAccountNetwork {
    return BankAccountNetwork(
        id = id,
        name = name,
        balance = balance.toBigDecimal(),
        number = number,
        isClosed = isClosed
    )
}

fun BankAccountEntity.isEqualsToNetworkAccount(network: BankAccountNetwork): Boolean =
    this.toNetworkAccount() == network