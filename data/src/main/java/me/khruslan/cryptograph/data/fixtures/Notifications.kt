package me.khruslan.cryptograph.data.fixtures

import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationStatus
import me.khruslan.cryptograph.data.notifications.NotificationTrigger
import me.khruslan.cryptograph.data.notifications.local.NotificationDto
import java.time.LocalDate

val PREVIEW_NOTIFICATIONS
    get() = STUB_NOTIFICATIONS + listOf(
        Notification(
            id = 3L,
            coinId = "WcwrkfNI4FUAe",
            title = "BNB > 600$",
            createdAt = LocalDate.parse("2024-09-15"),
            completedAt = null,
            expirationDate = LocalDate.parse("2025-11-10"),
            trigger = NotificationTrigger.PriceMoreThan(600.0),
            status = NotificationStatus.Pending,
        ),
        Notification(
            id = 2L,
            coinId = "zNZHO_Sjf",
            title = "Solana > 200$",
            createdAt = LocalDate.parse("2024-09-15"),
            completedAt = null,
            expirationDate = null,
            trigger = NotificationTrigger.PriceLessThan(200.0),
            status = NotificationStatus.Pending,
        ),
        Notification(
            id = 1L,
            coinId = "aKzUVe4Hh_CON",
            title = "USDC < 0.99$",
            createdAt = LocalDate.parse("2024-09-15"),
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
            createdAt = LocalDate.parse("2024-09-13"),
            completedAt = LocalDate.parse("2024-09-25"),
            expirationDate = null,
            trigger = NotificationTrigger.PriceLessThan(50000.0),
            status = NotificationStatus.Completed,
        ),
        Notification(
            id = 5L,
            coinId = "razxDUgYGNAdQ",
            title = "Ethereum > 4000$",
            createdAt = LocalDate.parse("2024-09-13"),
            completedAt = null,
            expirationDate = LocalDate.parse("2024-09-20"),
            trigger = NotificationTrigger.PriceMoreThan(4000.0),
            status = NotificationStatus.Expired,
        ),
        Notification(
            id = 4L,
            coinId = "HIVsRcGKkPFtW",
            title = "Tether USD < 1$",
            createdAt = LocalDate.parse("2024-09-15"),
            completedAt = null,
            expirationDate = null,
            trigger = NotificationTrigger.PriceLessThan(1.0),
            status = NotificationStatus.Pending,
        )
    )

internal val STUB_DTO_NOTIFICATIONS
    get() = listOf(
        NotificationDto(
            id = 6L,
            coinUuid = "Qwsogvtv82FCd",
            title = "Bitcoin < 50000$",
            createdAtDate = "2024-09-13",
            completedAtDate = "2024-09-25",
            expirationDate = null,
            priceLessThanTrigger = 50000.0,
            priceMoreThanTrigger = null,
        ),
        NotificationDto(
            id = 5L,
            coinUuid = "razxDUgYGNAdQ",
            title = "Ethereum > 4000$",
            createdAtDate = "2024-09-13",
            completedAtDate = null,
            expirationDate = "2024-09-20",
            priceLessThanTrigger = null,
            priceMoreThanTrigger = 4000.0,
        ),
        NotificationDto(
            id = 4L,
            coinUuid = "HIVsRcGKkPFtW",
            title = "Tether USD < 1$",
            createdAtDate = "2024-09-15",
            completedAtDate = null,
            expirationDate = null,
            priceLessThanTrigger = 1.0,
            priceMoreThanTrigger = null,
        ),
    )