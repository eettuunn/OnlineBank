package com.akimov.mobilebank.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.akimov.mobilebank.data.network.CoreService

class CreateAccountWorker(
    context: Context,
    private val params: WorkerParameters,
    private val api: CoreService,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val name: String? = params.inputData.getString("name")
        name?.let {
            try {
                api.createAccount(it)
                return Result.success()
            } catch (e: Throwable) {
                return Result.retry()
            }
        }
        return Result.failure()
    }
}