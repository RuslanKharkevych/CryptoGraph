package me.khruslan.cryptograph.data.notifications.interactors.completed

import me.khruslan.cryptograph.data.notifications.repository.Notification
import java.time.Clock
import java.time.LocalDate

internal class CompletedNotificationsMapper(private val clock: Clock) {

    fun mapCompletedNotification(
        notification: Notification,
        coinName: String,
    ): CompletedNotification {
        return CompletedNotification(
            title = notification.title,
            coinName = coinName,
            trigger = notification.trigger
        )
    }

    fun completeNotification(notification: Notification): Notification {
        return notification.copy(completedAt = LocalDate.now(clock))
    }
}