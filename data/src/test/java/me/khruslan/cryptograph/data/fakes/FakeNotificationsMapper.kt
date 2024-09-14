package me.khruslan.cryptograph.data.fakes

import me.khruslan.cryptograph.data.fixtures.STUB_DTO_NOTIFICATIONS
import me.khruslan.cryptograph.data.fixtures.STUB_NOTIFICATIONS
import me.khruslan.cryptograph.data.notifications.Notification
import me.khruslan.cryptograph.data.notifications.local.NotificationDto
import me.khruslan.cryptograph.data.notifications.mapper.NotificationsMapper

internal class FakeNotificationsMapper : NotificationsMapper {

    override suspend fun mapNotifications(
        notifications: List<NotificationDto>
    ): List<Notification> {
        return notifications.map { notification ->
            STUB_NOTIFICATIONS.first { it.id == notification.id }
        }
    }

    override suspend fun mapNotification(notification: Notification): NotificationDto {
        return STUB_DTO_NOTIFICATIONS.first { it.id == notification.id }
    }
}