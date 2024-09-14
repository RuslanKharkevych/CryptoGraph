package me.khruslan.cryptograph.data.notifications.mapper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationTrigger
import me.khruslan.cryptograph.data.notifications.local.NotificationDto
import java.time.Clock
import java.time.DateTimeException
import java.time.Instant
import java.time.OffsetDateTime

private const val LOG_TAG = "NotificationsMapper"

internal interface NotificationsMapper {
    suspend fun mapNotifications(notifications: List<NotificationDto>): List<Notification>
    suspend fun mapNotification(notification: Notification): NotificationDto
}

internal class NotificationsMapperImpl(
    private val dispatcher: CoroutineDispatcher,
    private val clock: Clock,
) : NotificationsMapper {

    override suspend fun mapNotifications(
        notifications: List<NotificationDto>,
    ): List<Notification> {
        return withContext(dispatcher) {
            notifications.mapNotNull { notificationDto ->
                mapNotification(notificationDto)
            }
        }
    }

    override suspend fun mapNotification(notification: Notification): NotificationDto {
        return withContext(dispatcher) {
            NotificationDto(
                id = notification.id,
                coinUuid = notification.coinId,
                title = notification.title,
                createdAtTimestamp = notification.createdAt.toEpochSecond(),
                expirationDateTimestamp = notification.expirationDate?.toEpochSecond(),
                priceLessThenTrigger = notification.trigger.targetPrice.takeIf {
                    notification.trigger is NotificationTrigger.PriceLessThen
                },
                priceMoreThenTrigger = notification.trigger.targetPrice.takeIf {
                    notification.trigger is NotificationTrigger.PriceMoreThen
                },
            )
        }
    }

    private fun mapNotification(notificationDto: NotificationDto): Notification? {
        return try {
            Notification(
                id = notificationDto.id,
                coinId = notificationDto.coinUuid,
                title = notificationDto.title,
                createdAt = mapTimestamp(notificationDto.createdAtTimestamp),
                expirationDate = notificationDto.expirationDateTimestamp?.let(::mapTimestamp),
                trigger = mapTrigger(
                    priceLessThen = notificationDto.priceLessThenTrigger,
                    priceMoreThen = notificationDto.priceMoreThenTrigger
                )
            )
        } catch (e: IllegalArgumentException) {
            Logger.error(LOG_TAG, "Failed to map $notificationDto", e)
            null
        }
    }

    private fun mapTimestamp(timestamp: Long): OffsetDateTime {
        return try {
            val instant = Instant.ofEpochSecond(timestamp)
            OffsetDateTime.ofInstant(instant, clock.zone)
        } catch (e: DateTimeException) {
            throw IllegalArgumentException("Invalid timestamp: $timestamp", e)
        }
    }

    private fun mapTrigger(priceLessThen: Double?, priceMoreThen: Double?): NotificationTrigger {
        require((priceLessThen == null) xor (priceMoreThen == null)) {
            "Invalid trigger (priceLessThen: $priceMoreThen, priceMoreThen: $priceMoreThen)"
        }

        return if (priceLessThen != null) {
            NotificationTrigger.PriceLessThen(priceLessThen)
        } else {
            NotificationTrigger.PriceMoreThen(priceMoreThen!!)
        }
    }
}