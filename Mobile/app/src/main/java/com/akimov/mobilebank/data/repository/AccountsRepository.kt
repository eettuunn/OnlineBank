package com.akimov.mobilebank.data.repository

import androidx.datastore.core.DataStore
import com.akimov.mobilebank.UserSettings
import com.akimov.mobilebank.data.database.AccountsDao
import com.akimov.mobilebank.data.models.BankAccountNetwork
import com.akimov.mobilebank.data.models.toEntity
import com.akimov.mobilebank.data.network.CoreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import java.util.UUID

private const val DELAY_BETWEEN_REQUEST_UPDATES = 5000L

class AccountsRepository(
    private val api: CoreService,
    private val dao: AccountsDao,
    private val dataStore: DataStore<UserSettings>,
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
//                emit(Result.Error(Exception("Ошибка: ${response.code()}")))
            }
            delay(DELAY_BETWEEN_REQUEST_UPDATES)
        }
    }
        .retryWhen { cause, attempt ->
            emit(getFromLocalStorage())
            true
//            emit(Result.Error(Exception("Отсутствует подключение к интернету")))
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

    public suspend fun createAccount(name: String) {

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
