package me.khruslan.cryptograph.data.interactors.notifications.completed

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.coins.CoinsRepository
import me.khruslan.cryptograph.data.common.DataException
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationTrigger
import me.khruslan.cryptograph.data.notifications.NotificationsRepository
import java.time.Clock
import java.time.LocalDate

private const val LOG_TAG = "UpdateNotificationsInteractor"

interface CompletedNotificationsInteractor {
    suspend fun getCompletedNotifications(): List<Notification>
    suspend fun tryRefreshCompletedNotifications()
}

// TODO: Improve performance with bulk update instead of a loop
internal class CompletedNotificationsInteractorImpl(
    private val notificationsRepository: NotificationsRepository,
    private val coinsRepository: CoinsRepository,
    private val clock: Clock,
) : CompletedNotificationsInteractor {

    override suspend fun getCompletedNotifications(): List<Notification> {
        val pendingNotifications = loadPendingNotifications().ifEmpty {
            Logger.info(LOG_TAG, "No pending notifications found")
            return emptyList()
        }

        val coins = loadCoins()
        val completedNotifications = mutableListOf<Notification>()

        pendingNotifications.forEach { notification ->
            val coin = coins.findCoin(notification) ?: return@forEach
            if (isNotificationCompleted(notification, coin)) {
                completeNotification(notification) { completedNotifications += it }
            }
        }

        Logger.info(LOG_TAG, "Completed ${completedNotifications.count()} notifications")
        return completedNotifications
    }

    override suspend fun tryRefreshCompletedNotifications() {
        try {
            getCompletedNotifications()
        } catch (_: DataException) {
            Logger.info(LOG_TAG, "Failed to refresh completed notifications")
        }
    }

    private fun isNotificationCompleted(notification: Notification, coin: Coin): Boolean {
        val notificationPrice = notification.trigger.targetPrice
        val coinPrice = resolvePrice(coin.price) ?: run {
            Logger.info(LOG_TAG, "Invalid coin price. Skipping $notification")
            return false
        }

        return when (notification.trigger) {
            is NotificationTrigger.PriceLessThan -> coinPrice < notificationPrice
            is NotificationTrigger.PriceMoreThan -> coinPrice > notificationPrice
        }
    }

    private suspend fun completeNotification(
        notification: Notification,
        onSuccess: (notification: Notification) -> Unit,
    ) {
        val completedNotification = notification.copy(completedAt = LocalDate.now(clock))
        try {
            notificationsRepository.addOrUpdateNotification(completedNotification)
            onSuccess(completedNotification)
        } catch (_: DataException) {
            Logger.info(LOG_TAG, "Failed to complete $notification")
        }
    }

    private suspend fun loadPendingNotifications(): List<Notification> {
        val notifications = notificationsRepository.getNotifications().takeSingle()
        return notifications.filter { it.isPending }
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

    private suspend fun <T> Flow<T>.takeSingle(): T {
        return take(1).single()
    }
}