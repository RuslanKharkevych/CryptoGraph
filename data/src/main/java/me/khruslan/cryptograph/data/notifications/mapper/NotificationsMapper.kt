package me.khruslan.cryptograph.data.notifications.mapper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.common.DataValidationException
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.NotificationStatus
import me.khruslan.cryptograph.data.notifications.NotificationTrigger
import me.khruslan.cryptograph.data.notifications.local.NotificationDto
import java.time.Clock
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

private const val LOG_TAG = "NotificationsMapper"

internal class NotificationsMapper(
    private val dispatcher: CoroutineDispatcher,
    private val clock: Clock,
) {

    suspend fun mapNotifications(
        notifications: List<NotificationDto>,
    ): List<Notification> {
        return withContext(dispatcher) {
            notifications.mapNotNull { notificationDto ->
                mapNotificationOrNull(notificationDto)
            }.sortedWith(notificationsComparator())
        }
    }

    suspend fun mapNotification(notification: NotificationDto): Notification {
        return withContext(dispatcher) {
            try {
                mapNotificationInternal(notification)
            } catch (e: IllegalArgumentException) {
                Logger.error(LOG_TAG, "Failed to map $notification", e)
                throw DataValidationException(e)
            }
        }
    }

    suspend fun mapNotification(notification: Notification): NotificationDto {
        return withContext(dispatcher) {
            NotificationDto(
                id = notification.id,
                coinUuid = notification.coinId,
                title = notification.title,
                createdAtDateTime = notification.createdAt.toString(),
                completedAtDate = notification.completedAt?.toString(),
                expirationDate = notification.expirationDate?.toString(),
                priceLessThanTrigger = notification.trigger.targetPrice.takeIf {
                    notification.trigger is NotificationTrigger.PriceLessThan
                },
                priceMoreThanTrigger = notification.trigger.targetPrice.takeIf {
                    notification.trigger is NotificationTrigger.PriceMoreThan
                }
            )
        }
    }

    private fun mapNotificationInternal(notificationDto: NotificationDto): Notification {
        return Notification(
            id = notificationDto.id,
            coinId = notificationDto.coinUuid,
            title = notificationDto.title,
            createdAt = mapDateTime(notificationDto.createdAtDateTime),
            completedAt = notificationDto.completedAtDate?.let(::mapDate),
            expirationDate = notificationDto.expirationDate?.let(::mapDate),
            trigger = mapTrigger(
                priceLessThan = notificationDto.priceLessThanTrigger,
                priceMoreThan = notificationDto.priceMoreThanTrigger
            ),
            status = mapStatus(notificationDto)
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
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Invalid date string: $dateString", e)
        }
    }

    private fun mapDateTime(dateTimeString: String): OffsetDateTime {
        return try {
            OffsetDateTime.parse(dateTimeString)
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Invalid date time string: $dateTimeString", e)
        }
    }

    private fun mapTrigger(priceLessThan: Double?, priceMoreThan: Double?): NotificationTrigger {
        require((priceLessThan == null) xor (priceMoreThan == null)) {
            "Invalid trigger (priceLessThan: $priceMoreThan, priceMoreThan: $priceMoreThan)"
        }

        return if (priceLessThan != null) {
            NotificationTrigger.PriceLessThan(priceLessThan)
        } else {
            NotificationTrigger.PriceMoreThan(priceMoreThan!!)
        }
    }

    private fun mapStatus(notification: NotificationDto): NotificationStatus {
        return when {
            notification.completedAtDate != null -> NotificationStatus.Completed
            isExpired(notification) -> NotificationStatus.Expired
            else -> NotificationStatus.Pending
        }
    }

    private fun isExpired(notification: NotificationDto): Boolean {
        val expirationDate = notification.expirationDate?.let(::mapDate) ?: return false
        return LocalDate.now(clock).isAfter(expirationDate)
    }

    private fun notificationsComparator(): Comparator<Notification> {
        return compareBy<Notification> { it.status.sortOrder }.thenByDescending { it.createdAt }
    }
}