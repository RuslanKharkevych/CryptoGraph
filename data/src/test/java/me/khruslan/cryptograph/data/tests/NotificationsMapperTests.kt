package me.khruslan.cryptograph.data.tests

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.fixtures.STUB_DTO_NOTIFICATIONS
import me.khruslan.cryptograph.data.fixtures.STUB_NOTIFICATIONS
import me.khruslan.cryptograph.data.notifications.mapper.NotificationsMapperImpl
import me.khruslan.cryptograph.data.notifications.mapper.NotificationsMapper
import org.junit.Before
import org.junit.Test
import java.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
internal class NotificationsMapperTests {

    private lateinit var mapper: NotificationsMapper

    @Before
    fun setUp() {
        mapper = NotificationsMapperImpl(
            dispatcher = UnconfinedTestDispatcher(),
            clock = Clock.systemUTC()
        )
    }

    @Test
    fun `Map notifications`() = runTest {
        val notifications = mapper.mapNotifications(STUB_DTO_NOTIFICATIONS)
        assertThat(notifications).isEqualTo(STUB_NOTIFICATIONS)
    }

    @Test
    fun `Map notification`() = runTest {
        val notification = mapper.mapNotification(STUB_NOTIFICATIONS[0])
        assertThat(notification).isEqualTo(STUB_DTO_NOTIFICATIONS[0])
    }
}