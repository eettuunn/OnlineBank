package com.akimov.mobilebank.data.workers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.akimov.mobilebank.UserSettings
import com.akimov.mobilebank.data.models.CreateAccountUpload
import com.akimov.mobilebank.data.network.CoreService
import kotlinx.coroutines.flow.first
import java.util.UUID

class CreateAccountWorker(
    context: Context,
    private val params: WorkerParameters,
    private val api: CoreService,
    private val dataStore: DataStore<UserSettings>
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val name: String? = params.inputData.getString("name")
        name?.let {
            try {
                api.createAccount(
                    createAccountUpload = CreateAccountUpload(
                        name,
                        userId = UUID.fromString(dataStore.data.first().uuid)
                    )
                )
                return Result.success()
            } catch (e: Throwable) {
                return Result.failure()
            }
        }
        return Result.failure()
    }
}