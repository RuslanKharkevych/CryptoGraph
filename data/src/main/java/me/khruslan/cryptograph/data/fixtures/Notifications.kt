package me.khruslan.cryptograph.data.fixtures

import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationTrigger
import me.khruslan.cryptograph.data.notifications.local.NotificationDto
import java.time.OffsetDateTime

val STUB_NOTIFICATIONS: List<Notification>
    get() {
        if (stubNotifications != null) return stubNotifications!!
        stubNotifications = listOf(
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
            )
        )
        return stubNotifications!!
    }

internal val STUB_DTO_NOTIFICATIONS: List<NotificationDto>
    get() {
        if (stubDtoNotifications != null) return stubDtoNotifications!!
        stubDtoNotifications = listOf(
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
            )
        )
        return stubDtoNotifications!!
    }

private var stubNotifications: List<Notification>? = null
private var stubDtoNotifications: List<NotificationDto>? = null