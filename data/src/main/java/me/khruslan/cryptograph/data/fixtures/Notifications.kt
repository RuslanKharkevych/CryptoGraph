package me.khruslan.cryptograph.data.fixtures

import me.khruslan.cryptograph.data.notifications.Notification
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
            expirationDate = LocalDate.parse("2025-11-10"),
            trigger = NotificationTrigger.PriceMoreThen(600.0)
        ),
        Notification(
            id = 2L,
            coinId = "zNZHO_Sjf",
            title = "Solana > 200$",
            createdAt = LocalDate.parse("2024-09-15"),
            expirationDate = null,
            trigger = NotificationTrigger.PriceLessThen(200.0)
        ),
        Notification(
            id = 1L,
            coinId = "aKzUVe4Hh_CON",
            title = "USDC < 0.99$",
            createdAt = LocalDate.parse("2024-09-15"),
            expirationDate = null,
            trigger = NotificationTrigger.PriceLessThen(0.99)
        ),
    )

val STUB_NOTIFICATIONS
    get() = listOf(
        Notification(
            id = 6L,
            coinId = "Qwsogvtv82FCd",
            title = "Bitcoin < 50000$",
            createdAt = LocalDate.parse("2024-09-13"),
            expirationDate = null,
            trigger = NotificationTrigger.PriceLessThen(50000.0)
        ),
        Notification(
            id = 5L,
            coinId = "razxDUgYGNAdQ",
            title = "Ethereum > 4000$",
            createdAt = LocalDate.parse("2024-09-13"),
            expirationDate = LocalDate.parse("2025-09-13"),
            trigger = NotificationTrigger.PriceMoreThen(4000.0)
        ),
        Notification(
            id = 4L,
            coinId = "HIVsRcGKkPFtW",
            title = "Tether USD < 1$",
            createdAt = LocalDate.parse("2024-09-15"),
            expirationDate = null,
            trigger = NotificationTrigger.PriceLessThen(1.0)
        )
    )

internal val STUB_DTO_NOTIFICATIONS
    get() = listOf(
        NotificationDto(
            id = 6L,
            coinUuid = "Qwsogvtv82FCd",
            title = "Bitcoin < 50000$",
            createdAtDate = "2024-09-13",
            expirationDate = null,
            priceLessThenTrigger = 50000.0,
            priceMoreThenTrigger = null
        ),
        NotificationDto(
            id = 5L,
            coinUuid = "razxDUgYGNAdQ",
            title = "Ethereum > 4000$",
            createdAtDate = "2024-09-13",
            expirationDate = "2025-09-13",
            priceLessThenTrigger = null,
            priceMoreThenTrigger = 4000.0
        ),
        NotificationDto(
            id = 4L,
            coinUuid = "HIVsRcGKkPFtW",
            title = "Tether USD < 1$",
            createdAtDate = "2024-09-15",
            expirationDate = null,
            priceLessThenTrigger = 1.0,
            priceMoreThenTrigger = null
        ),
    )