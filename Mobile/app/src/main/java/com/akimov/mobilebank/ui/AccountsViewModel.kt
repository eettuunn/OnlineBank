package com.akimov.mobilebank.ui

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akimov.mobilebank.UserSettings
import com.akimov.mobilebank.data.models.BankAccountNetwork
import com.akimov.mobilebank.data.models.OperationType
import com.akimov.mobilebank.data.repository.AccountsRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountsViewModel(
    private val dataStore: DataStore<UserSettings>,
    private val repository: AccountsRepository,
) : ViewModel() {
    private val _actions = MutableSharedFlow<ViewAction>()
    val actions = _actions.shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000L))

    private val selectedAccount: MutableStateFlow<BankAccountNetwork?> = MutableStateFlow(null)

    val state = combine(
        dataStore.data,
        repository.getAccounts(),
        selectedAccount
    ) { settings, accounts, selectedAccount ->
        AccountsScreenState.Content(
            userName = settings.name,
            isDarkTheme = settings.isDarkMode,
            accountsList = accounts.toImmutableList(),
            selectedAccount = selectedAccount,
        )
    }
        .catch { _actions.emit(ViewAction.ShowError(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), AccountsScreenState.Loading)

    fun onIntent(intent: UIIntent) {
        when (intent) {
            is UIIntent.UpdateTheme -> viewModelScope.launch {
                delay(200L)
                dataStore.updateData {
                    it.toBuilder()
                        .setIsDarkMode(!it.isDarkMode)
                        .build()
                }
            }

            is UIIntent.OpenAccount -> repository.createAccount(intent.name)
            is UIIntent.ChangeBalance -> repository.changeBalance(
                amount = intent.amount,
                accountId = intent.accountId,
                operationType = if (intent.amount.contains("-")) OperationType.WITHDRAW else OperationType.DEPOSIT,
            )

            is UIIntent.SelectAccount -> {
                selectedAccount.update { intent.it }
            }

            UIIntent.UnselectAccount -> selectedAccount.update { null }
            is UIIntent.RenameAccount -> {
                val selectedAccountValue =
                    selectedAccount.value ?: throw Exception("No account selected")
                repository.renameAccount(
                    newName = intent.it,
                    accountId = selectedAccountValue.id.toString()
                )
                selectedAccount.update { null }
            }
        }
    }
}

sealed class UIIntent {
    data object UpdateTheme : UIIntent()
    data class OpenAccount(val name: String) : UIIntent()
    data class ChangeBalance(
        val amount: String,
        val accountId: String,
    ) : UIIntent()

    data class RenameAccount(val it: String) : UIIntent() {

    }

    data class SelectAccount(val it: BankAccountNetwork) : UIIntent() {

    }

    data object UnselectAccount : UIIntent()

}

sealed class ViewAction {
    data class ShowError(val message: String) : ViewAction()
}

sealed class AccountsScreenState {
    data object Loading : AccountsScreenState()
    data class Content(
        val userName: String,
        val isDarkTheme: Boolean,
        val accountsList: ImmutableList<BankAccountNetwork>,
        val selectedAccount: BankAccountNetwork?,
    ) : AccountsScreenState()
}
