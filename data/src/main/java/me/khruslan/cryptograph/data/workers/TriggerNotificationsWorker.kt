package me.khruslan.cryptograph.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.common.DataException
import me.khruslan.cryptograph.data.interactors.notifications.completed.CompletedNotificationsInteractor
import me.khruslan.cryptograph.data.notifications.Notification
import java.util.concurrent.TimeUnit

private const val LOG_TAG = "TriggerNotificationsWorker"

private const val WORK_NAME = "UpdateNotifications"
private const val REPEAT_INTERVAL_MINUTES = 12L * 60L

class TriggerNotificationsWorker internal constructor(
    appContext: Context,
    params: WorkerParameters,
    private val completedNotificationsInteractor: CompletedNotificationsInteractor,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val completedNotifications = getCompletedNotifications()
            // TODO: Send push notification(s)
            Result.success()
        } catch (_: DataException) {
            Logger.info(LOG_TAG, "Failed to load completed notifications")
            Result.failure()
        }
    }

    private suspend fun getCompletedNotifications(): List<Notification> {
        return completedNotificationsInteractor.getCompletedNotifications()
    }

    companion object {
        fun launch(context: Context) {
            val workManager = WorkManager.getInstance(context)

            val workRequest = PeriodicWorkRequestBuilder<TriggerNotificationsWorker>(
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