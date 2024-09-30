package me.khruslan.cryptograph.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.coins.CoinsRepository
import me.khruslan.cryptograph.data.common.DataException
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationStatus
import me.khruslan.cryptograph.data.notifications.NotificationTrigger
import me.khruslan.cryptograph.data.notifications.NotificationsRepository
import java.time.LocalDate
import java.util.concurrent.TimeUnit

private const val LOG_TAG = "UpdateNotificationsWorker"

private const val REPEAT_INTERVAL_MINUTES = 12L * 60L
private const val WORK_NAME = "UpdateNotifications"

// TODO: Implement sending push notifications
class UpdateNotificationsWorker internal constructor(
    appContext: Context,
    params: WorkerParameters,
    private val notificationsRepository: NotificationsRepository,
    private val coinsRepository: CoinsRepository,
) : CoroutineWorker(appContext, params) {

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

    override suspend fun doWork(): Result {
        Logger.info(LOG_TAG, "Worker started")

        val pendingNotifications = try {
            loadPendingNotifications()
        } catch (_: DataException) {
            return failedWithMessage("Failed to load pending notifications. Halting the worker")
        }

        if (pendingNotifications.isEmpty()) {
            return succeededWithMessage("Completed successfully. No pending notifications found")
        }

        val coins = try {
            loadCoins()
        } catch (_: DataException) {
            return failedWithMessage("Failed to load coins. Halting the worker")
        }

        // TODO: Improve performance with bulk update instead of a loop
        var updatedNotificationsCount = 0
        pendingNotifications.forEach { notification ->
            val coin = coins.findCoin(notification) ?: return@forEach
            if (isNotificationCompleted(notification, coin)) {
                completeNotification(notification) { updatedNotificationsCount++ }
            }
        }

        return succeededWithMessage(
            "Completed successfully. Updated $updatedNotificationsCount notifications"
        )
    }

    private fun isNotificationCompleted(notification: Notification, coin: Coin): Boolean {
        val notificationPrice = notification.trigger.targetPrice
        val coinPrice = resolvePrice(coin.price) ?: run {
            Logger.info(LOG_TAG, "Invalid coin price. Skipping $notification")
            return false
        }

        return when (notification.trigger) {
            is NotificationTrigger.PriceLessThan -> notificationPrice < coinPrice
            is NotificationTrigger.PriceMoreThan -> notificationPrice > coinPrice
        }
    }

    private suspend fun completeNotification(notification: Notification, onSuccess: () -> Unit) {
        val completedNotification = notification.copy(completedAt = LocalDate.now())
        try {
            notificationsRepository.addOrUpdateNotification(completedNotification)
            onSuccess()
        } catch (_: DataException) {
            Logger.info(LOG_TAG, "Failed to complete $notification")
        }
    }

    private suspend fun loadPendingNotifications(): List<Notification> {
        val notifications = notificationsRepository.getNotifications().takeSingle()
        return notifications.filter { it.status == NotificationStatus.Pending }
    }

    private suspend fun loadCoins(): List<Coin> {
        return coinsRepository.getCoins().takeSingle()
    }

    private fun List<Coin>.findCoin(notification: Notification): Coin? {
        return try {
            first { it.id == notification.coinId }
        } catch (e: NoSuchElementException) {
            Logger.error(LOG_TAG, "Coin not found. Skipping $notification", e)
            return null
        }
    }

    private fun resolvePrice(priceString: String?): Double? {
        return priceString?.removePrefix("$")?.toDoubleOrNull()
    }

    private fun succeededWithMessage(message: String): Result {
        Logger.info(LOG_TAG, message)
        return Result.success()
    }

    private fun failedWithMessage(message: String): Result {
        Logger.info(LOG_TAG, message)
        return Result.failure()
    }

    private suspend fun <T> Flow<T>.takeSingle(): T {
        return take(1).single()
    }
}