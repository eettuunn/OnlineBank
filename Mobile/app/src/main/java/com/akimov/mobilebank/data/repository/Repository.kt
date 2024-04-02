package com.akimov.mobilebank.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.akimov.mobilebank.UserSettings
import com.akimov.mobilebank.data.database.AccountsDao
import com.akimov.mobilebank.data.database.CreditsDao
import com.akimov.mobilebank.data.database.OperationsDao
import com.akimov.mobilebank.data.database.entity.LoanRateEntity
import com.akimov.mobilebank.data.models.BankAccountNetwork
import com.akimov.mobilebank.data.models.CreditNetwork
import com.akimov.mobilebank.data.models.CreditUi
import com.akimov.mobilebank.data.models.LoanRate
import com.akimov.mobilebank.data.models.OperationNetwork
import com.akimov.mobilebank.data.models.OperationType
import com.akimov.mobilebank.data.models.toEntity
import com.akimov.mobilebank.data.network.CoreService
import com.akimov.mobilebank.data.network.LoanService
import com.akimov.mobilebank.data.workers.ChangeBalanceWorker
import com.akimov.mobilebank.data.workers.CloseAccountWorker
import com.akimov.mobilebank.data.workers.CreateAccountWorker
import com.akimov.mobilebank.data.workers.GetLoanWorker
import com.akimov.mobilebank.data.workers.RenameAccountWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class Repository(
    private val coreService: CoreService,
    private val loanService: LoanService,
    private val accountsDao: AccountsDao,
    private val creditsDao: CreditsDao,
    private val operationsDao: OperationsDao,
    private val dataStore: DataStore<UserSettings>,
    private val workManager: WorkManager,
) {

    private val _accountsFlow: MutableStateFlow<List<BankAccountNetwork>?> =
        MutableStateFlow(null)
    val accounts = _accountsFlow.asStateFlow()

    private val _creditsFlow: MutableStateFlow<List<CreditUi>?> = MutableStateFlow(null)
    val credits = _creditsFlow.asStateFlow()

    suspend fun updateAccounts() {
        val response = try {
            coreService.getAccounts(UUID.fromString(dataStore.data.first().token))
        } catch (e: Throwable) {
            Log.e("Repository", "updateAccounts: ${e.message}")
            null
        }

        if (response == null) {
            _accountsFlow.update { getAccountsFromLocalStorage() }
            return
        }

        if (response!!.isSuccessful) {
            val body = response.body()
            if (body != null) {
                _accountsFlow.update { body.data }
                body.data.map { accountFromNetwork ->
                    val dbEntity = accountFromNetwork.toEntity()
                    if (accountsDao.findAccountById(accountFromNetwork.id) != dbEntity) {
                        accountsDao.insertAccount(dbEntity)
                    }
                }
            }
        } else {
            _accountsFlow.update { getAccountsFromLocalStorage() }
        }
    }

    suspend fun updateCredits() {
        val response = try {
            loanService.getLoansList(dataStore.data.first().token)
        } catch (e: Throwable) {
            null
        }
        if (response == null) {
            _creditsFlow.update { getCreditsFromLocalStorage().map { it.toUi() } }
            return
        }

        if (response!!.isSuccessful) {
            val body = response.body()
            if (body != null) {
                _creditsFlow.update {
                    body.map {
                        it.toUi()
                    }
                }
                body.map { creditNetwork ->
                    val dbEntity = creditNetwork.toEntity()
                    val localCredit = creditsDao.findCreditById(creditNetwork.id)
                    if (localCredit != dbEntity) {
                        coroutineScope {
                            launch(Dispatchers.IO) {
                                creditsDao.insertCredit(dbEntity)
                            }
                        }
                    }
                }
            }
        } else {
            _creditsFlow.update {
                getCreditsFromLocalStorage().map {
                    it.toUi()
                }
            }
        }
    }

    @Deprecated("")
    fun getAccountsOldVersion(): Flow<List<BankAccountNetwork>> = flow {
        val response = coreService.getAccounts(UUID.fromString(dataStore.data.first().token))
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                emit(body.data)
            }
        } else {
            emit(getAccountsFromLocalStorage())
        }
    }
        .retryWhen { cause, attempt ->
            emit(getAccountsFromLocalStorage())
            true
        }
        .onEach {
            it.map { accountFromNetwork ->
                val dbEntity = accountFromNetwork.toEntity()
                if (accountsDao.findAccountById(accountFromNetwork.id) != dbEntity) {
                    accountsDao.insertAccount(dbEntity)
                }
            }
        }
        .flowOn(Dispatchers.IO)

    fun createAccount(name: String) {
        createWorkerRequest<CreateAccountWorker>(
            inputData = Data.Builder().putString("name", name).build()
        )
    }

    fun changeBalance(amount: String, accountId: String, operationType: OperationType) {
        createWorkerRequest<ChangeBalanceWorker>(
            inputData = Data.Builder()
                .putString("operationType", operationType.name)
                .putString("accountId", accountId)
                .putLong("amount", amount.toLong())
                .build()
        )
    }

    fun renameAccount(newName: String, accountId: String) {
        createWorkerRequest<RenameAccountWorker>(
            inputData = Data.Builder()
                .putString("name", newName)
                .putString("accountId", accountId)
                .build()
        )
    }

    @Deprecated("")
    fun getUserCreditsOldVersion(): Flow<List<CreditUi>> = flow {
        val response = loanService.getLoansList(dataStore.data.first().token)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                emit(body)
            }
        } else {
            emit(getCreditsFromLocalStorage())
        }
    }
        .retryWhen { cause, attempt ->
            emit(getCreditsFromLocalStorage())
            true
        }
        .onEach {
            it.map { creditNetwork ->
                val dbEntity = creditNetwork.toEntity()
                val localCredit = creditsDao.findCreditById(creditNetwork.id)
                if (localCredit != dbEntity) {
                    creditsDao.insertCredit(dbEntity)
                }
            }
        }
        .map { creditsNetwork ->
            creditsNetwork.map {
                CreditUi(
                    id = it.id.toString(),
                    debt = it.debt.toString(),
                    monthlyPayment = it.monthlyPayment.toString(),
                    bankAccountName = accountsDao.findAccountById(it.bankAccountId)?.name
                        ?: "Незвестный аккаунт"
                )
            }
        }
        .flowOn(Dispatchers.IO)

    private suspend fun CreditNetwork.toUi(): CreditUi {
        return CreditUi(
            id = id.toString(),
            debt = debt.toString(),
            monthlyPayment = monthlyPayment.toString(),
            bankAccountName = accountsDao.findAccountById(bankAccountId)?.name
                ?: "Незвестный аккаунт"
        )
    }

    fun getNewLoan(months: Int, loanAmount: Int, loanRateId: String, bankAccountId: String) {
        createWorkerRequest<GetLoanWorker>(
            inputData = Data.Builder()
                .putInt("months", months)
                .putInt("loanAmount", loanAmount)
                .putString("loanRateId", loanRateId)
                .putString("bankAccountId", bankAccountId)
                .build()
        )
    }

    private suspend fun getCreditsFromLocalStorage(): List<CreditNetwork> {
        return withContext(Dispatchers.IO) {
            creditsDao.getLoansList().map { creditEntity ->
                CreditNetwork(
                    id = creditEntity.id,
                    debt = creditEntity.debt,
                    monthlyPayment = creditEntity.monthlyPayment,
                    bankAccountId = creditEntity.bankAccountId
                )
            }
        }
    }

    fun closeAccount(accountId: String) {
        createWorkerRequest<CloseAccountWorker>(
            inputData = Data.Builder().putString("accountId", accountId).build()
        )
    }

    suspend fun getOperations(accountId: String): List<OperationNetwork> {
        try {
            val operations: List<OperationNetwork> = coreService.getOperations(accountId).data

            coroutineScope {
                launch(Dispatchers.IO) {
                    operations.map {
                        val dbEntity = it.toEntity()
                        val localOperation = operationsDao.findOperationById(it.id)
                        if (localOperation != dbEntity) {
                            operationsDao.insertOperation(dbEntity)
                        }
                    }
                }
            }

            return operations
        } catch (e: Throwable) {
            return withContext(Dispatchers.IO) {
                operationsDao.getOperations().map { it.toNetwork() }
            }
        }
    }

    private suspend fun getAccountsFromLocalStorage(): List<BankAccountNetwork> {
        return withContext(Dispatchers.IO) {
            accountsDao.getAccounts().map { accountEntity ->
                BankAccountNetwork(
                    id = accountEntity.id,
                    name = accountEntity.name,
                    balance = accountEntity.balance.toBigDecimal(),
                    number = accountEntity.number,
                    isClosed = accountEntity.isClosed
                )
            }
        }
    }

    private inline fun <reified T : ListenableWorker> createWorkerRequest(
        inputData: Data
    ) {
        val request: OneTimeWorkRequest = OneTimeWorkRequestBuilder<T>()
            .setInputData(inputData)
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .build()

        workManager.enqueue(request)
    }

    suspend fun getLoanRates(): List<LoanRate> {
        val rates = try {
            loanService.getLoanRates()
        } catch (e: Throwable) {
            listOf(
                LoanRate(UUID.randomUUID().toString(), 15.0),
                LoanRate(UUID.randomUUID().toString(), 20.0)
            )
        }

        coroutineScope {
            launch(Dispatchers.IO) {
                rates.map {
                    val dbEntity = LoanRateEntity(it.id, it.interestRate)
                    val localRate = creditsDao.findRateById(it.id)
                    if (localRate != dbEntity) {
                        creditsDao.insertRate(dbEntity)
                    }
                }
            }
        }
        return rates
    }
}
