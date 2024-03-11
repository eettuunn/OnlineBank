package com.akimov.mobilebank.data.workers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.akimov.mobilebank.UserSettings
import com.akimov.mobilebank.data.models.UserIdModel
import com.akimov.mobilebank.data.network.CoreService
import kotlinx.coroutines.flow.first

class CloseAccountWorker(
    context: Context,
    private val params: WorkerParameters,
    private val dataStore: DataStore<UserSettings>,
    private val coreService: CoreService,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            coreService.closeAccount(
                accountId = params.inputData.getString("accountId")
                    ?: throw IllegalArgumentException("Account id is required"),
                userId = UserIdModel(dataStore.data.first().uuid)
            )
            Result.success()
        } catch (e: Throwable) {
            Result.retry()
        }
    }
}