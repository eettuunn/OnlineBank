package com.akimov.mobilebank.ui.accounts

import com.akimov.mobilebank.data.models.BankAccountNetwork
import com.akimov.mobilebank.data.models.CreditUi
import com.akimov.mobilebank.data.models.LoanRate
import kotlinx.collections.immutable.ImmutableList

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
