package me.khruslan.cryptograph.data.notifications.interactors.coin

import me.khruslan.cryptograph.data.coins.Coin
import me.khruslan.cryptograph.data.notifications.repository.Notification

data class CoinNotification(
    val coin: Coin,
    val notification: Notification
)