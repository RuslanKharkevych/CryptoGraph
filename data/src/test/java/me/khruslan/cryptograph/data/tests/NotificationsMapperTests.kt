package me.khruslan.cryptograph.data.tests

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.fixtures.STUB_DTO_NOTIFICATIONS
import me.khruslan.cryptograph.data.fixtures.STUB_NOTIFICATIONS
import me.khruslan.cryptograph.data.notifications.mapper.NotificationsMapper
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

private val CLOCK: Clock = Clock.fixed(
    Instant.parse("2024-09-30T00:06:14.00Z"),
    ZoneId.of("Asia/Tokyo")
)

internal class NotificationsMapperTests {

    private lateinit var mapper: NotificationsMapper

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        mapper = NotificationsMapper(
            dispatcher = UnconfinedTestDispatcher(),
            clock = CLOCK,
        )
    }

    @Test
    fun `Map notifications`() = runTest {
        val notifications = mapper.mapNotifications(STUB_DTO_NOTIFICATIONS)
        assertThat(notifications).isEqualTo(STUB_NOTIFICATIONS)
    }

    @Test
    fun `Map notification - from DTO`() = runTest {
        val notification = mapper.mapNotification(STUB_DTO_NOTIFICATIONS[0])
        assertThat(notification).isEqualTo(STUB_NOTIFICATIONS[0])
    }

    @Test
    fun `Map notification - to DTO`() = runTest {
        val notification = mapper.mapNotification(STUB_NOTIFICATIONS[0])
        assertThat(notification).isEqualTo(STUB_DTO_NOTIFICATIONS[0])
    }
}