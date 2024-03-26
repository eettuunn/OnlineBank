package com.akimov.mobilebank.ui.operations

import androidx.lifecycle.ViewModel
import com.akimov.mobilebank.data.models.OperationType
import com.akimov.mobilebank.data.repository.Repository
import com.akimov.mobilebank.domain.OperationUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

class OperationsViewModel(
    private val repository: Repository,
) : ViewModel() {
    private val _operations = MutableStateFlow<Map<String, List<OperationUI>>>(mapOf())
    private val _isLoading = MutableStateFlow(false)
    private val _accountName = MutableStateFlow("Account")

    val operations = _operations.asStateFlow()
    val isLoading = _isLoading.asStateFlow()
    val accountName = _accountName.asStateFlow()

    val state = combine(
        operations,
        isLoading,
        accountName
    ) { operations, isLoading, accountName ->
        OperationsScreenState(
            operations = operations,
            showProgress = isLoading,
            accountName = accountName
        )
    }

    fun reduce(intent: OperationsScreenIntent) {
        when (intent) {
            is OperationsScreenIntent.OnAddOperationClicked -> {
                invokeOperation(
                    amount = intent.amount,
                    operationType = intent.operationType,
                    accountId = intent.accountId
                )
            }
        }
    }

    private fun invokeOperation(amount: String, operationType: OperationType, accountId: String) {
        repository.changeBalance(
            amount = amount,
            accountId = accountId,
            operationType = operationType
        )
    }
}

sealed class OperationsScreenIntent {
    data class OnAddOperationClicked(
        val amount: String,
        val operationType: OperationType,
        val accountId: String
    ) : OperationsScreenIntent()
}

data class OperationsScreenState(
    val operations: Map<String, List<OperationUI>>,
    val showProgress: Boolean,
    val accountName: String
)
