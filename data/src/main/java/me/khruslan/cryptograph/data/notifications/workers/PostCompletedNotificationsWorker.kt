package me.khruslan.cryptograph.data.notifications.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.core.DataConfig
import me.khruslan.cryptograph.data.core.DataException
import me.khruslan.cryptograph.data.notifications.interactors.completed.CompletedNotification
import me.khruslan.cryptograph.data.notifications.interactors.completed.CompletedNotificationsInteractor
import me.khruslan.cryptograph.data.notifications.managers.PushNotificationsManager
import java.util.concurrent.TimeUnit

private const val LOG_TAG = "PostCompletedNotificationsWorker"
private const val WORK_NAME = "PostCompletedNotifications"

class PostCompletedNotificationsWorker internal constructor(
    appContext: Context,
    params: WorkerParameters,
    private val completedNotificationsInteractor: CompletedNotificationsInteractor,
    private val pushNotificationsManager: PushNotificationsManager,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val completedNotifications = getCompletedNotifications()
            if (completedNotifications.isNotEmpty()) {
                postCompletedNotifications(completedNotifications)
            }
            Logger.info(LOG_TAG, "Work completed successfully")
            Result.success()
        } catch (_: DataException) {
            Logger.info(LOG_TAG, "Failed to load completed notifications")
            Result.failure()
        }
    }

    private suspend fun getCompletedNotifications(): List<CompletedNotification> {
        return completedNotificationsInteractor.getCompletedNotifications()
    }

    private fun postCompletedNotifications(completedNotifications: List<CompletedNotification>) {
        pushNotificationsManager.postCompletedNotifications(completedNotifications)
    }

    companion object {
        fun launch(context: Context, config: DataConfig) {
            val workManager = WorkManager.getInstance(context)

            val workRequest = PeriodicWorkRequestBuilder<PostCompletedNotificationsWorker>(
                repeatInterval = config.postNotificationsIntervalMinutes,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            ).setInitialDelay(
                duration = config.postNotificationsIntervalMinutes,
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