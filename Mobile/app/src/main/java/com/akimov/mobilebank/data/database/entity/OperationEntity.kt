package com.akimov.mobilebank.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akimov.mobilebank.data.models.OperationNetwork
import com.akimov.mobilebank.data.models.OperationType
import java.util.UUID

@Entity("operations")
data class OperationEntity(
    @PrimaryKey
    val id: String,
    val transactionDate: String,
    val amount: Int,
    val additionalInformation: String,
    val transactionType: OperationType,
    val bankAccountId: UUID
) {
    fun toNetwork(): OperationNetwork {
        return OperationNetwork(
            id = id,
            transactionDate = transactionDate,
            amount = amount,
            additionalInformation = additionalInformation,
            transactionType = transactionType,
            bankAccountId = bankAccountId
        )

    }
}
