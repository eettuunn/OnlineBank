package com.akimov.mobilebank.data.models

import com.akimov.mobilebank.data.database.entity.OperationEntity
import java.util.UUID

data class OperationNetwork(
    val id: String,
    val transactionDate: String,
    val amount: Int,
    val additionalInformation: String,
    val transactionType: OperationType,
    val bankAccountId: UUID
) {
    fun toEntity(): OperationEntity {
        return OperationEntity(
            id = id,
            transactionDate = transactionDate,
            amount = amount,
            additionalInformation = additionalInformation,
            transactionType = transactionType,
            bankAccountId = bankAccountId
        )

    }
}
