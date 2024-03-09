package com.akimov.mobilebank.data.repository

import androidx.datastore.core.DataStore
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.akimov.mobilebank.UserSettings
import com.akimov.mobilebank.data.database.AccountsDao
import com.akimov.mobilebank.data.models.BankAccountNetwork
import com.akimov.mobilebank.data.models.toEntity
import com.akimov.mobilebank.data.network.CoreService
import com.akimov.mobilebank.data.workers.CreateAccountWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import java.util.UUID
import java.util.concurrent.TimeUnit

private const val DELAY_BETWEEN_REQUEST_UPDATES = 5000L

class AccountsRepository(
    private val api: CoreService,
    private val dao: AccountsDao,
    private val dataStore: DataStore<UserSettings>,
    private val workManager: WorkManager,
) {

    fun getAccounts(): Flow<List<BankAccountNetwork>> = flow {
        while (true) {
            val response = api.getAccounts(UUID.fromString(dataStore.data.first().uuid))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    emit(body)
                }
            } else {
                emit(getFromLocalStorage())
            }
            delay(DELAY_BETWEEN_REQUEST_UPDATES)
        }
    }
        .retryWhen { cause, attempt ->
            emit(getFromLocalStorage())
            true
        }
        .onEach {
            it.map { accountFromNetwork ->
                val dbEntity = accountFromNetwork.toEntity()
                if (dao.findAccountById(accountFromNetwork.id) != dbEntity) {
                    dao.insertAccount(dbEntity)
                }
            }
        }
        .flowOn(Dispatchers.IO)

    fun createAccount(name: String) {
        val request = OneTimeWorkRequestBuilder<CreateAccountWorker>()
            .setInputData(Data.Builder().putString("name", name).build())
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, DELAY_BETWEEN_REQUEST_UPDATES, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueue(request)
    }

    private suspend fun getFromLocalStorage(): List<BankAccountNetwork> {
        return dao.getAccounts().map { accountEntity ->
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
