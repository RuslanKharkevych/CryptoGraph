package me.khruslan.cryptograph.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import me.khruslan.cryptograph.data.common.DataException
import me.khruslan.cryptograph.data.interactors.sync.UpdateNotificationsInteractor
import java.util.concurrent.TimeUnit

private const val WORK_NAME = "UpdateNotifications"
private const val REPEAT_INTERVAL_MINUTES = 12L * 60L

// TODO: Implement sending push notifications
class UpdateNotificationsWorker internal constructor(
    appContext: Context,
    params: WorkerParameters,
    private val updateNotificationsInteractor: UpdateNotificationsInteractor,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            updateNotificationsInteractor.updateNotifications()
            Result.success()
        } catch (_: DataException) {
            Result.failure()
        }
    }

    companion object {
        fun launch(context: Context) {
            val workManager = WorkManager.getInstance(context)

            val workRequest = PeriodicWorkRequestBuilder<UpdateNotificationsWorker>(
                repeatInterval = REPEAT_INTERVAL_MINUTES,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            ).setInitialDelay(
                duration = REPEAT_INTERVAL_MINUTES,
                timeUnit = TimeUnit.MINUTES
            ).build()

            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }
    }
}