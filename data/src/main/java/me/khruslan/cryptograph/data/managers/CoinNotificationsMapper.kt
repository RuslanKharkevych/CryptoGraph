package me.khruslan.cryptograph.data.managers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.notifications.Notification

private const val LOG_TAG = "CoinNotificationsMapper"

internal class CoinNotificationsMapper(private val dispatcher: CoroutineDispatcher) {

    suspend fun mapCoinNotifications(
        coins: List<Coin>,
        notifications: List<Notification>,
    ): List<CoinNotification> {
        return withContext(dispatcher) {
            notifications.mapNotNull { notification ->
                mapCoinNotification(coins, notification)
            }
        }
    }

    private fun mapCoinNotification(
        coins: List<Coin>,
        notification: Notification,
    ): CoinNotification? {
        return try {
            CoinNotification(
                coin = coins.first { it.id == notification.coinId },
                notification = notification
            )
        } catch (e: NoSuchElementException) {
            Logger.error(LOG_TAG, "Failed to map coin notification", e)
            null
        }
    }
}