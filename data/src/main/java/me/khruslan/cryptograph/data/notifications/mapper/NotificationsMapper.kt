package me.khruslan.cryptograph.data.notifications.mapper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.common.DataValidationException
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationTrigger
import me.khruslan.cryptograph.data.notifications.local.NotificationDto
import java.time.DateTimeException
import java.time.LocalDate

private const val LOG_TAG = "NotificationsMapper"

internal interface NotificationsMapper {
    suspend fun mapNotifications(notifications: List<NotificationDto>): List<Notification>
    suspend fun mapNotification(notification: NotificationDto): Notification
    suspend fun mapNotification(notification: Notification): NotificationDto
}

internal class NotificationsMapperImpl(
    private val dispatcher: CoroutineDispatcher,
) : NotificationsMapper {

    // TODO: Sort by created / updated at descending
    override suspend fun mapNotifications(
        notifications: List<NotificationDto>,
    ): List<Notification> {
        return withContext(dispatcher) {
            notifications.mapNotNull { notificationDto ->
                mapNotificationOrNull(notificationDto)
            }
        }
    }

    override suspend fun mapNotification(notification: NotificationDto): Notification {
        return withContext(dispatcher) {
            try {
                mapNotificationInternal(notification)
            } catch (e: IllegalArgumentException) {
                Logger.error(LOG_TAG, "Failed to map $notification", e)
                throw DataValidationException(e)
            }
        }
    }

    override suspend fun mapNotification(notification: Notification): NotificationDto {
        return withContext(dispatcher) {
            NotificationDto(
                id = notification.id,
                coinUuid = notification.coinId,
                title = notification.title,
                createdAtDate = notification.createdAt.toString(),
                expirationDate = notification.expirationDate?.toString(),
                priceLessThenTrigger = notification.trigger.targetPrice.takeIf {
                    notification.trigger is NotificationTrigger.PriceLessThen
                },
                priceMoreThenTrigger = notification.trigger.targetPrice.takeIf {
                    notification.trigger is NotificationTrigger.PriceMoreThen
                },
            )
        }
    }

    private fun mapNotificationInternal(notificationDto: NotificationDto): Notification {
        return Notification(
            id = notificationDto.id,
            coinId = notificationDto.coinUuid,
            title = notificationDto.title,
            createdAt = mapDate(notificationDto.createdAtDate),
            expirationDate = notificationDto.expirationDate?.let(::mapDate),
            trigger = mapTrigger(
                priceLessThen = notificationDto.priceLessThenTrigger,
                priceMoreThen = notificationDto.priceMoreThenTrigger
            )
        )
    }

    private fun mapNotificationOrNull(notificationDto: NotificationDto): Notification? {
        return try {
            mapNotificationInternal(notificationDto)
        } catch (e: IllegalArgumentException) {
            Logger.error(LOG_TAG, "Failed to map $notificationDto", e)
            null
        }
    }

    private fun mapDate(dateString: String): LocalDate {
        return try {
            LocalDate.parse(dateString)
        } catch (e: DateTimeException) {
            throw IllegalArgumentException("Invalid date string: $dateString", e)
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