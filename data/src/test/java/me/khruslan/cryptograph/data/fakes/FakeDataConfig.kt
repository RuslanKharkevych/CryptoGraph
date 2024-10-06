package me.khruslan.cryptograph.data.fakes

import me.khruslan.cryptograph.data.core.DataConfig

internal class FakeDataConfig : DataConfig {
    override val cacheMaxStaleSeconds = 3600
    override val postNotificationsIntervalMinutes = 720L
    override val rateLimitingModeEnabled = true
}