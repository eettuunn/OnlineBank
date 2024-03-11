package com.akimov.mobilebank.ui

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akimov.mobilebank.UserSettings
import com.akimov.mobilebank.data.models.BankAccountNetwork
import com.akimov.mobilebank.data.models.CreditUi
import com.akimov.mobilebank.data.models.LoanRate
import com.akimov.mobilebank.data.models.OperationType
import com.akimov.mobilebank.data.models.SelectedScreen
import com.akimov.mobilebank.data.repository.Repository
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
import java.util.UUID

class AccountsViewModel(
    private val dataStore: DataStore<UserSettings>,
    private val repository: Repository,
) : ViewModel() {
    private val _actions = MutableSharedFlow<ViewAction>()
    val actions = _actions.shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000L))

    private val selectedAccount: MutableStateFlow<BankAccountNetwork?> = MutableStateFlow(null)
    private var loanRates: List<LoanRate> = listOf(LoanRate(UUID.randomUUID().toString(), 0.0))
    private val isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val state = combine(
        dataStore.data,
        repository.accounts,
        selectedAccount,
        repository.credits,
        isRefreshing
    ) { settings, accounts, selectedAccount, credits, isNowRefreshing ->
        AccountsScreenState(
            loanRates = loanRates.toImmutableList(),
            isRefreshing = isNowRefreshing,
            accountsState = AccountsState.Content(
                userName = settings.name,
                isDarkTheme = settings.isDarkMode,
                accountsList = accounts.toImmutableList(),
                selectedAccount = selectedAccount,
                creditsList = credits.toImmutableList(),
            )
        )
    }
        .catch { _actions.emit(ViewAction.ShowError(it.message ?: "Unknown error")) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            AccountsScreenState(
                loanRates = loanRates.toImmutableList(),
                isRefreshing = false,
                accountsState = AccountsState.Loading
            )
        )

    init {
        updateData()
    }

    private fun updateData() {
        viewModelScope.launch {
            repository.updateAccounts()
        }
        viewModelScope.launch {
            repository.updateCredits()
        }

        viewModelScope.launch {
            loanRates = repository.getLoanRates()
        }
    }

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

            UIIntent.UpdateDataFromRemote -> {
                isRefreshing.update { true }

                val job1 = viewModelScope.launch {
                    repository.updateAccounts()
                }
                val job2 = viewModelScope.launch {
                    repository.updateCredits()
                }

                val job3 = viewModelScope.launch {
                    loanRates = repository.getLoanRates()
                }

                viewModelScope.launch {
                    job1.join()
                    job2.join()
                    job3.join()
                    isRefreshing.update { false }
                }
            }

            UIIntent.NavigateToGetLoan -> {
                viewModelScope.launch {
                    dataStore.updateData {
                        it.toBuilder()
                            .setSelectedScreen(SelectedScreen.ADD_CREDIT.name)
                            .build()
                    }
                }
            }

            is UIIntent.DeleteAccount -> {
                repository.closeAccount(intent.it)
            }

            UIIntent.NavigateToOperations -> {
                viewModelScope.launch {
                    dataStore.updateData {
                        it.toBuilder()
                            .setSelectedScreen(SelectedScreen.OPERATIONS.name)
                            .build()
                    }
                }

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

    data class RenameAccount(val it: String) : UIIntent()

    data class SelectAccount(val it: BankAccountNetwork) : UIIntent()
    data class DeleteAccount(val it: String) : UIIntent()

    data object UnselectAccount : UIIntent()
    data object UpdateDataFromRemote : UIIntent()
    data object NavigateToGetLoan : UIIntent()
    data object NavigateToOperations : UIIntent()
}

sealed class ViewAction {
    data class ShowError(val message: String) : ViewAction()
}

data class AccountsScreenState(
    val loanRates: ImmutableList<LoanRate>,
    val isRefreshing: Boolean,
    val accountsState: AccountsState
)

sealed class AccountsState {
    data object Loading : AccountsState()
    data class Content(
        val userName: String,
        val isDarkTheme: Boolean,
        val accountsList: ImmutableList<BankAccountNetwork>,
        val selectedAccount: BankAccountNetwork?,
        val creditsList: ImmutableList<CreditUi>,
    ) : AccountsState()
}
