package com.akimov.mobilebank.data.models

import com.akimov.mobilebank.data.database.entity.CreditEntity
import java.util.UUID

data class CreditNetwork(
    val id: UUID,
    val debt: Int,
    val monthlyPayment: Float,
    val bankAccountId: UUID
)

fun CreditNetwork.toEntity(): CreditEntity = CreditEntity(
    id = id,
    debt = debt,
    monthlyPayment = monthlyPayment,
    bankAccountId = bankAccountId
)
