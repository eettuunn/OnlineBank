package com.akimov.mobilebank.data.workers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.akimov.mobilebank.UserSettings
import com.akimov.mobilebank.data.models.GetLoanUpload
import com.akimov.mobilebank.data.network.LoanService
import kotlinx.coroutines.flow.first

class GetLoanWorker(
    context: Context,
    private val params: WorkerParameters,
    private val loanService: LoanService,
    private val dataStore: DataStore<UserSettings>
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            loanService.getLoan(
                getLoanUpload = GetLoanUpload(
                    months = params.inputData.getInt(
                        "months",
                        12
                    ),
                    loanAmount = params.inputData.getInt(
                        "loanAmount",
                        1000
                    ),
                    loanRateId = params.inputData.getString("loanRateId")
                        ?: throw IllegalArgumentException("Loan rate id is required"),
                    userId = dataStore.data.first().token,
                    bankAccountId = params.inputData.getString("bankAccountId")
                        ?: throw IllegalArgumentException("Bank account id is required")
                )
            )
            Result.success()
        } catch (e: Throwable) {
            Result.failure()
        }
    }
}