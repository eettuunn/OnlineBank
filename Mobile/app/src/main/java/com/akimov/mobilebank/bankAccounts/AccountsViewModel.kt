package com.akimov.mobilebank.bankAccounts

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akimov.mobilebank.UserSettings
import com.akimov.mobilebank.data.models.BankAccountNetwork
import com.akimov.mobilebank.data.repository.AccountsRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AccountsViewModel(
    private val dataStore: DataStore<UserSettings>,
    private val repository: AccountsRepository,
) : ViewModel() {
    private val _actions = MutableSharedFlow<ViewAction>()
    val actions = _actions.shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000L))

    val state = combine(dataStore.data, repository.getAccounts()) { settings, accounts ->
        AccountsScreenState.Content(
            userName = settings.name,
            isDarkTheme = settings.isDarkMode,
            accountsList = accounts.toImmutableList(),
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
        }
    }
}

sealed class UIIntent {
    data object UpdateTheme : UIIntent()
    data class OpenAccount(val name: String) : UIIntent()
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
    ) : AccountsScreenState()
}
