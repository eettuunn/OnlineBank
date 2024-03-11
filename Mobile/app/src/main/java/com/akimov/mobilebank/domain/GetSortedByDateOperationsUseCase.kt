package com.akimov.mobilebank.domain

import com.akimov.mobilebank.data.models.OperationNetwork
import com.akimov.mobilebank.data.repository.Repository
import com.akimov.mobilebank.formatDate

class GetSortedByDateOperationsUseCase(
    private val repository: Repository
) {
    suspend operator fun invoke(accountId: String): Map<String, List<OperationUI>> {
        return repository.getOperations(accountId)
            .map {
                it.toUI()
            }
            .sortedByDescending { it.transactionDate }
            .groupBy { it.transactionDate }
    }

    private fun OperationNetwork.toUI(): OperationUI {
        return OperationUI(
            id = id,
            transactionDate = formatDate(transactionDate),
            amount = amount,
            additionalInformation = additionalInformation,
            transactionType = transactionType,
            bankAccountId = bankAccountId.toString()
        )
    }
}
