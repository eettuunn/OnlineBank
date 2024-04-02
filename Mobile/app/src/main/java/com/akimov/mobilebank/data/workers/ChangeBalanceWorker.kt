package com.akimov.mobilebank.data.workers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.akimov.mobilebank.UserSettings
import com.akimov.mobilebank.data.models.OperationType
import com.akimov.mobilebank.data.models.TransactionBody
import com.akimov.mobilebank.data.network.CoreService
import kotlinx.coroutines.flow.first

class ChangeBalanceWorker(
    context: Context,
    private val params: WorkerParameters,
    private val api: CoreService,
    private val dataStore: DataStore<UserSettings>,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val operationType =
            OperationType.valueOf(params.inputData.getString("operationType") ?: "DEPOSIT")
        val amount = params.inputData.getLong("amount", 0)
        val accountId =
            params.inputData.getString("accountId") ?: throw Exception("Missing accountId")

        return when (operationType) {
            OperationType.DEPOSIT -> networkBalanceOperation {
                api.deposit(
                    accountID = accountId,
                    transactionBody = TransactionBody(
                        amount = amount.toInt(),
                        transactionType = OperationType.DEPOSIT.name,
                        userId = dataStore.data.first().token
                    )
                )
            }

            OperationType.WITHDRAW -> networkBalanceOperation {
                api.withdraw(
                    accountID = accountId,
                    transactionBody = TransactionBody(
                        amount = amount.toInt(),
                        transactionType = OperationType.WITHDRAW.name,
                        userId = dataStore.data.first().token
                    )
                )
            }

            OperationType.TAKE_LOAN -> networkBalanceOperation {
                api.deposit(
                    accountID = accountId,
                    transactionBody = TransactionBody(
                        amount = amount.toInt(),
                        transactionType = OperationType.TAKE_LOAN.name,
                        userId = dataStore.data.first().token
                    )
                )
            }

            OperationType.REPAY_LOAN -> networkBalanceOperation {
                api.withdraw(
                    accountID = accountId,
                    transactionBody = TransactionBody(
                        amount = amount.toInt(),
                        transactionType = OperationType.REPAY_LOAN.name,
                        userId = dataStore.data.first().token
                    )
                )
            }
        }

    }

    private suspend fun networkBalanceOperation(
        apiCall: suspend () -> Unit,
    ): Result {
        return try {
            apiCall()
            Result.success()
        } catch (e: Throwable) {
            Result.failure()
        }
    }
}
