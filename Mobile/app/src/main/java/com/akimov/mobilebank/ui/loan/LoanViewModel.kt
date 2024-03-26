package com.akimov.mobilebank.ui.loan

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akimov.mobilebank.data.database.AccountsDao
import com.akimov.mobilebank.data.database.CreditsDao
import com.akimov.mobilebank.data.database.entity.BankAccountEntity
import com.akimov.mobilebank.data.database.entity.LoanRateEntity
import com.akimov.mobilebank.data.repository.Repository
import kotlinx.coroutines.launch

class LoanViewModel(
    private val creditsDao: CreditsDao,
    private val accountsDao: AccountsDao,
    private val repository: Repository,
) : ViewModel() {

    var accounts: MutableState<List<BankAccountEntity>> = mutableStateOf(listOf())
        private set

    var rates: MutableState<List<LoanRateEntity>> = mutableStateOf(listOf())
        private set

    init {
        viewModelScope.launch {
            accounts.value = accountsDao.getAccounts()
            rates.value = creditsDao.getRates()
        }
    }

    fun getCredit(
        accountId: String,
        rateId: String,
        months: Int,
        amount: Int
    ) {
        repository.getNewLoan(
            months = months,
            loanAmount = amount,
            bankAccountId = accountId,
            loanRateId = rateId
        )
    }
}