package me.khruslan.cryptograph.data.interactors.sync

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

private const val LOG_TAG = "UpdateNotificationsInteractor"

interface UpdateNotificationsInteractor {
    suspend fun updateNotifications()
}

// TODO: Improve performance with bulk update instead of a loop
internal class UpdateNotificationsInteractorImpl(
    private val notificationsRepository: NotificationsRepository,
    private val coinsRepository: CoinsRepository,
) : UpdateNotificationsInteractor {

    override suspend fun updateNotifications() {
        val pendingNotifications = loadPendingNotifications()
        if (pendingNotifications.isEmpty()) {
            Logger.info(LOG_TAG, "No pending notifications found")
            return
        }

        val coins = loadCoins()
        var updatedNotificationsCount = 0

        pendingNotifications.forEach { notification ->
            val coin = coins.findCoin(notification) ?: return@forEach
            if (isNotificationCompleted(notification, coin)) {
                completeNotification(notification) { updatedNotificationsCount++ }
            }
        }

        Logger.info(LOG_TAG, "Updated $updatedNotificationsCount notifications")
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

    private suspend fun <T> Flow<T>.takeSingle(): T {
        return take(1).single()
    }
}