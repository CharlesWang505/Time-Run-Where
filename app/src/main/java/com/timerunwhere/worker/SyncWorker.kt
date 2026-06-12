package com.timerunwhere.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.timerunwhere.data.network.SyncRepository

class SyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val result = SyncRepository(applicationContext).syncNow()
        return result.fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() }
        )
    }
}
