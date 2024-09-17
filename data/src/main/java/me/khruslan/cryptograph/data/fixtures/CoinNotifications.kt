package me.khruslan.cryptograph.data.fixtures

import me.khruslan.cryptograph.data.managers.CoinNotification

val PREVIEW_COIN_NOTIFICATIONS
    get() = PREVIEW_NOTIFICATIONS.mapIndexed { index, notification ->
        CoinNotification(PREVIEW_COINS[index], notification)
    }

val STUB_COIN_NOTIFICATIONS
    get() = STUB_NOTIFICATIONS.mapIndexed { index, notification ->
        CoinNotification(STUB_COINS[index], notification)
    }
