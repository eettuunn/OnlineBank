package com.akimov.mobilebank.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.akimov.mobilebank.data.models.OperationType
import com.akimov.mobilebank.data.network.CoreService

class ChangeBalanceWorker(
    context: Context,
    private val params: WorkerParameters,
    private val api: CoreService,
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
                    amount = amount,
                    transactionType = OperationType.DEPOSIT.name,
                    accountID = accountId
                )
            }

            OperationType.WITHDRAW -> networkBalanceOperation {
                api.withdraw(
                    amount = amount,
                    transactionType = OperationType.TAKE_LOAN.name,
                    accountID = accountId
                )
            }

            OperationType.TAKE_LOAN -> networkBalanceOperation {
                api.deposit(
                    amount = amount,
                    transactionType = OperationType.TAKE_LOAN.name,
                    accountID = accountId
                )
            }

            OperationType.REPAY_LOAN -> networkBalanceOperation {
                api.withdraw(
                    amount = amount,
                    transactionType = OperationType.REPAY_LOAN.name,
                    accountID = accountId
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
            Result.retry()
        }
    }
}
