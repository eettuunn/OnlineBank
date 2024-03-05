package com.akimov.mobilebank.bankAccounts

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akimov.mobilebank.UserSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AccountsViewModel(
    private val dataStore: DataStore<UserSettings>,
) : ViewModel() {
    val state = dataStore.data.map {
        AcountsScreenState(
            userName = it.name,
            isDarkTheme = it.isDarkMode,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
        initialValue = AcountsScreenState(
            userName = "Максим",
            isDarkTheme = false,
        ),
    )

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
        }
    }
}

sealed class UIIntent {
    data object UpdateTheme : UIIntent()
}

data class AcountsScreenState(
    val userName: String,
    val isDarkTheme: Boolean,
)