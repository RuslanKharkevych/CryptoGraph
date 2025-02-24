package me.khruslan.cryptograph.data.tests

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.fakes.FakeNotificationsLocalDataSource
import me.khruslan.cryptograph.data.fixtures.STUB_NOTIFICATIONS
import me.khruslan.cryptograph.data.notifications.repository.NotificationsRepository
import me.khruslan.cryptograph.data.notifications.repository.NotificationsRepositoryImpl
import me.khruslan.cryptograph.data.notifications.repository.mapper.NotificationsMapper
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

private val CLOCK: Clock = Clock.fixed(
    Instant.parse("2024-09-30T00:06:14.00Z"),
    ZoneId.of("Asia/Tokyo")
)

internal class NotificationsRepositoryTests {

    private lateinit var repository: NotificationsRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        val dispatcher = UnconfinedTestDispatcher()

        repository = NotificationsRepositoryImpl(
            localDataSource = FakeNotificationsLocalDataSource(),
            mapper = NotificationsMapper(dispatcher, CLOCK)
        )
    }

    @Test
    fun `Get notification`() = runTest {
        val addedNotification = STUB_NOTIFICATIONS[0]
        repository.addOrUpdateNotification(addedNotification)

        val actualNotification = repository.getNotification(addedNotification.id)
        assertThat(actualNotification).isEqualTo(addedNotification)
    }

    @Test
    fun `Add notification`() = runTest {
        val notification = STUB_NOTIFICATIONS[0]
        repository.addOrUpdateNotification(notification)

        repository.getNotifications().test {
            assertThat(awaitItem()).containsExactly(notification)
        }
    }

    @Test
    fun `Update notification`() = runTest {
        val initialNotification = STUB_NOTIFICATIONS[0].copy(title = "Bitcoin < 5500$")
        repository.addOrUpdateNotification(initialNotification)

        val updatedNotification = STUB_NOTIFICATIONS[0]
        repository.addOrUpdateNotification(updatedNotification)

        repository.getNotifications().test {
            assertThat(awaitItem()).containsExactly(updatedNotification)
        }
    }

    @Test
    fun `Delete notification`() = runTest {
        val notification = STUB_NOTIFICATIONS[0]
        repository.addOrUpdateNotification(notification)
        repository.deleteNotification(notification.id)

        repository.getNotifications().test {
            assertThat(awaitItem()).isEmpty()
        }
    }

    @Test
    fun `Filter notifications`() = runTest {
        val notification1 = STUB_NOTIFICATIONS[0]
        val notification2 = STUB_NOTIFICATIONS[1]
        repository.addOrUpdateNotification(notification1)
        repository.addOrUpdateNotification(notification2)

        repository.getNotifications(notification2.coinId).test {
            assertThat(awaitItem()).containsExactly(notification2)
        }
    }
}