package me.khruslan.cryptograph.data.fixtures

import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationTrigger
import me.khruslan.cryptograph.data.notifications.local.NotificationDto
import java.time.OffsetDateTime

val PREVIEW_NOTIFICATIONS
    get() = STUB_NOTIFICATIONS + listOf(
        Notification(
            id = 4L,
            coinId = "WcwrkfNI4FUAe",
            title = "BNB > 600$",
            createdAt = OffsetDateTime.parse("2024-09-15T23:13:34.000+00:00"),
            expirationDate = OffsetDateTime.parse("2025-11-10T12:00:00.000+00:00"),
            trigger = NotificationTrigger.PriceMoreThen(600.0)
        ),
        Notification(
            id = 5L,
            coinId = "zNZHO_Sjf",
            title = "Solana > 200$",
            createdAt = OffsetDateTime.parse("2024-09-15T23:16:00.000+00:00"),
            expirationDate = null,
            trigger = NotificationTrigger.PriceLessThen(200.0)
        ),
        Notification(
            id = 6L,
            coinId = "aKzUVe4Hh_CON",
            title = "USDC < 0.99$",
            createdAt = OffsetDateTime.parse("2024-09-15T23:20:00.000+00:00"),
            expirationDate = null,
            trigger = NotificationTrigger.PriceLessThen(0.99)
        ),
    )

val STUB_NOTIFICATIONS
    get() = listOf(
            Notification(
                id = 1L,
                coinId = "Qwsogvtv82FCd",
                title = "Bitcoin < 5000$",
                createdAt = OffsetDateTime.parse("2024-09-13T23:39:26.000+00:00"),
                expirationDate = null,
                trigger = NotificationTrigger.PriceLessThen(5000.0)
            ),
            Notification(
                id = 2L,
                coinId = "razxDUgYGNAdQ",
                title = "Ethereum > 3000$",
                createdAt = OffsetDateTime.parse("2024-09-13T23:43:40.000+00:00"),
                expirationDate = OffsetDateTime.parse("2025-09-13T23:47:07.000+00:00"),
                trigger = NotificationTrigger.PriceMoreThen(3000.0)
            ),
            Notification(
                id = 3L,
                coinId = "HIVsRcGKkPFtW",
                title = "Tether USD < 1$",
                createdAt = OffsetDateTime.parse("2024-09-15T19:47:09.000+00:00"),
                expirationDate = null,
                trigger = NotificationTrigger.PriceLessThen(1.0)
            )
        )

internal val STUB_DTO_NOTIFICATIONS
    get()  = listOf(
            NotificationDto(
                id = 1L,
                coinUuid = "Qwsogvtv82FCd",
                title = "Bitcoin < 5000$",
                createdAtTimestamp = 1726270766L,
                expirationDateTimestamp = null,
                priceLessThenTrigger = 5000.0,
                priceMoreThenTrigger = null
            ),
            NotificationDto(
                id = 2L,
                coinUuid = "razxDUgYGNAdQ",
                title = "Ethereum > 3000$",
                createdAtTimestamp = 1726271020L,
                expirationDateTimestamp = 1757807227L,
                priceLessThenTrigger = null,
                priceMoreThenTrigger = 3000.0
            ),
            NotificationDto(
                id = 3L,
                coinUuid = "HIVsRcGKkPFtW",
                title = "Tether USD < 1$",
                createdAtTimestamp = 1726429629L,
                expirationDateTimestamp = null,
                priceLessThenTrigger = 1.0,
                priceMoreThenTrigger = null
            )
        )