package me.khruslan.cryptograph.data.tests

import com.google.common.truth.Truth.assertThat
import me.khruslan.cryptograph.data.fixtures.STUB_COIN_NOTIFICATIONS
import me.khruslan.cryptograph.data.fixtures.STUB_NOTIFICATIONS
import me.khruslan.cryptograph.data.interactors.notifications.completed.CompletedNotification
import me.khruslan.cryptograph.data.interactors.notifications.completed.CompletedNotificationsMapper
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

private val CLOCK: Clock = Clock.fixed(
    Instant.parse("2024-10-03T17:41:37.00Z"),
    ZoneId.of("America/Chicago")
)

internal class CompletedNotificationsMapperTests {

    private lateinit var mapper: CompletedNotificationsMapper

    @Before
    fun setUp() {
        mapper = CompletedNotificationsMapper(CLOCK)
    }

    @Test
    fun `Map completed notification`() {
        val (coin, notification) = STUB_COIN_NOTIFICATIONS[0]

        val expectedCompletedNotification = CompletedNotification(
            title = notification.title,
            coinName = coin.name,
            trigger = notification.trigger
        )
        val actualCompletedNotification = mapper.mapCompletedNotification(
            notification = notification,
            coinName = coin.name
        )
        assertThat(actualCompletedNotification).isEqualTo(expectedCompletedNotification)
    }

    @Test
    fun `Complete notification`() {
        val notification = STUB_NOTIFICATIONS[0]

        val expectedNotification = notification.copy(completedAt = LocalDate.now(CLOCK))
        val actualNotification = mapper.completeNotification(notification)
        assertThat(actualNotification).isEqualTo(expectedNotification)
    }
}