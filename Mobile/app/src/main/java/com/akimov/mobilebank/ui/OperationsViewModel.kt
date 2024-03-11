package com.akimov.mobilebank.ui

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akimov.mobilebank.UserSettings
import com.akimov.mobilebank.data.database.AccountsDao
import com.akimov.mobilebank.data.models.OperationType
import com.akimov.mobilebank.data.models.SelectedScreen
import com.akimov.mobilebank.data.repository.Repository
import com.akimov.mobilebank.domain.GetSortedByDateOperationsUseCase
import com.akimov.mobilebank.domain.OperationUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

var ACCOUNT_ID: String? = "c5cd0a11-456e-46f5-ad9d-8a79565960d5"

class OperationsViewModel(
    private val getSortedByDateOperationsUseCase: GetSortedByDateOperationsUseCase,
    private val accountsDao: AccountsDao,
    private val dataStore: DataStore<UserSettings>,
    private val repository: Repository
) : ViewModel() {
    private val _operations = MutableStateFlow<Map<String, List<OperationUI>>>(mapOf())
    private val _isLoading = MutableStateFlow(false)
    private val _accountName = MutableStateFlow("Account")

    val operations = _operations.asStateFlow()
    val isLoading = _isLoading.asStateFlow()
    val accountName = _accountName.asStateFlow()

    init {
        _isLoading.update {
            true
        }
        val job1 = viewModelScope.launch {
            _operations.update {
                getSortedByDateOperationsUseCase(ACCOUNT_ID!!)
            }
            Log.d("OperationsViewModel", "job1 finished")
        }

        val job2 = viewModelScope.launch {
            _accountName.update {
                accountsDao.findAccountById(UUID.fromString(ACCOUNT_ID!!))?.name ?: "Account"
            }
        }

        viewModelScope.launch {
            job1.join()
            job2.join()
            _isLoading.update { false }
        }
    }

    fun refresh() {
        _isLoading.update { true }
        val job1 = viewModelScope.launch {
            _operations.update {
                getSortedByDateOperationsUseCase(ACCOUNT_ID!!)
            }
        }
        viewModelScope.launch {
            job1.join()
            _isLoading.update { false }
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            dataStore.updateData {
                it.toBuilder().setSelectedScreen(SelectedScreen.ACCOUNTS.name).build()
            }
        }
    }

    fun invokeOperation(amount: String, operationType: OperationType) {
        repository.changeBalance(
            amount = amount,
            accountId = ACCOUNT_ID!!,
            operationType = operationType
        )
    }
}