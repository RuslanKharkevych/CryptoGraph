package me.khruslan.cryptograph.data.fakes

import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.fixtures.STUB_COIN_NOTIFICATIONS
import me.khruslan.cryptograph.data.managers.CoinNotification
import me.khruslan.cryptograph.data.managers.CoinNotificationsMapper
import me.khruslan.cryptograph.data.notifications.Notification

class FakeCoinNotificationsMapper : CoinNotificationsMapper {

    override suspend fun mapCoinNotifications(
        coins: List<Coin>,
        notifications: List<Notification>,
    ): List<CoinNotification> {
        return STUB_COIN_NOTIFICATIONS.filter { (coin, notification) ->
            coin in coins && notification in notifications
        }
    }
}