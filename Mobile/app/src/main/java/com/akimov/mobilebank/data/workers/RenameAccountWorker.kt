package com.akimov.mobilebank.data.workers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.akimov.mobilebank.UserSettings
import com.akimov.mobilebank.data.models.ChangeNameUpload
import com.akimov.mobilebank.data.network.CoreService
import kotlinx.coroutines.flow.first
import java.util.UUID

class RenameAccountWorker(
    private val params: WorkerParameters,
    private val api: CoreService,
    private val dataStore: DataStore<UserSettings>,
    context: Context
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            api.changeName(
                accountID = params.inputData.getString("accountId")
                    ?: throw Exception("Missing accountId"),
                changeNameUpload = ChangeNameUpload(
                    name = params.inputData.getString("name") ?: throw Exception("Missing name"),
                    userId = UUID.fromString(dataStore.data.first().uuid)
                )
            )
            Result.success()
        } catch (e: Throwable) {
            Result.failure()
        }
    }
}