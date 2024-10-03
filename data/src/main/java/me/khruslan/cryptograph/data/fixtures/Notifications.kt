package me.khruslan.cryptograph.data.fixtures

import me.khruslan.cryptograph.data.interactors.notifications.coin.CoinNotification
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationStatus
import me.khruslan.cryptograph.data.notifications.NotificationTrigger
import me.khruslan.cryptograph.data.notifications.local.NotificationDto
import java.time.LocalDate
import java.time.OffsetDateTime

val PREVIEW_NOTIFICATIONS
    get() = STUB_NOTIFICATIONS + listOf(
        Notification(
            id = 3L,
            coinId = "WcwrkfNI4FUAe",
            title = "BNB > 600$",
            createdAt = OffsetDateTime.parse("2024-09-15T17:23:41+09:00"),
            completedAt = null,
            expirationDate = LocalDate.parse("2025-11-10"),
            trigger = NotificationTrigger.PriceMoreThan(600.0),
            status = NotificationStatus.Pending,
        ),
        Notification(
            id = 2L,
            coinId = "zNZHO_Sjf",
            title = "Solana > 200$",
            createdAt = OffsetDateTime.parse("2024-09-15T16:20:05+09:00"),
            completedAt = null,
            expirationDate = null,
            trigger = NotificationTrigger.PriceLessThan(200.0),
            status = NotificationStatus.Pending,
        ),
        Notification(
            id = 1L,
            coinId = "aKzUVe4Hh_CON",
            title = "USDC < 0.99$",
            createdAt = OffsetDateTime.parse("2024-09-15T15:32:57+09:00"),
            completedAt = null,
            expirationDate = null,
            trigger = NotificationTrigger.PriceLessThan(0.99),
            status = NotificationStatus.Pending,
        ),
    )

val STUB_NOTIFICATIONS
    get() = listOf(
        Notification(
            id = 6L,
            coinId = "Qwsogvtv82FCd",
            title = "Bitcoin < 50000$",
            createdAt = OffsetDateTime.parse("2024-09-13T22:31:23+09:00"),
            completedAt = LocalDate.parse("2024-09-25"),
            expirationDate = null,
            trigger = NotificationTrigger.PriceLessThan(50000.0),
            status = NotificationStatus.Completed,
        ),
        Notification(
            id = 5L,
            coinId = "razxDUgYGNAdQ",
            title = "Ethereum > 4000$",
            createdAt = OffsetDateTime.parse("2024-09-13T22:29:40+09:00"),
            completedAt = null,
            expirationDate = LocalDate.parse("2024-09-20"),
            trigger = NotificationTrigger.PriceMoreThan(4000.0),
            status = NotificationStatus.Expired,
        ),
        Notification(
            id = 4L,
            coinId = "HIVsRcGKkPFtW",
            title = "Tether USD < 1$",
            createdAt = OffsetDateTime.parse("2024-09-15T21:30:28+09:00"),
            completedAt = null,
            expirationDate = null,
            trigger = NotificationTrigger.PriceLessThan(1.0),
            status = NotificationStatus.Pending,
        )
    )

val PREVIEW_COIN_NOTIFICATIONS
    get() = PREVIEW_NOTIFICATIONS.mapIndexed { index, notification ->
        CoinNotification(PREVIEW_COINS[index], notification)
    }

val STUB_COIN_NOTIFICATIONS
    get() = STUB_NOTIFICATIONS.mapIndexed { index, notification ->
        CoinNotification(STUB_COINS[index], notification)
    }

internal val STUB_DTO_NOTIFICATIONS
    get() = listOf(
        NotificationDto(
            id = 6L,
            coinUuid = "Qwsogvtv82FCd",
            title = "Bitcoin < 50000$",
            createdAtDateTime = "2024-09-13T22:31:23+09:00",
            completedAtDate = "2024-09-25",
            expirationDate = null,
            priceLessThanTrigger = 50000.0,
            priceMoreThanTrigger = null,
        ),
        NotificationDto(
            id = 5L,
            coinUuid = "razxDUgYGNAdQ",
            title = "Ethereum > 4000$",
            createdAtDateTime = "2024-09-13T22:29:40+09:00",
            completedAtDate = null,
            expirationDate = "2024-09-20",
            priceLessThanTrigger = null,
            priceMoreThanTrigger = 4000.0,
        ),
        NotificationDto(
            id = 4L,
            coinUuid = "HIVsRcGKkPFtW",
            title = "Tether USD < 1$",
            createdAtDateTime = "2024-09-15T21:30:28+09:00",
            completedAtDate = null,
            expirationDate = null,
            priceLessThanTrigger = 1.0,
            priceMoreThanTrigger = null,
        ),
    )