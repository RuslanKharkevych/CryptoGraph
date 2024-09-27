package me.khruslan.cryptograph.data.tests

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.fixtures.STUB_DTO_NOTIFICATIONS
import me.khruslan.cryptograph.data.notifications.local.NotificationsLocalDataSource
import me.khruslan.cryptograph.data.notifications.local.NotificationsLocalDataSourceImpl
import me.khruslan.cryptograph.data.rules.ObjectBoxRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class NotificationsLocalDataSourceTests {

    @get:Rule
    val objectBoxRule = ObjectBoxRule()

    private lateinit var dataSource: NotificationsLocalDataSource

    @Before
    fun setUp() {
        dataSource = NotificationsLocalDataSourceImpl(
            box = objectBoxRule.getBox(),
            dispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun `Get notification`() = runTest {
        val addedNotification = STUB_DTO_NOTIFICATIONS[0].copy(id = 0L)
        dataSource.addOrUpdateNotification(addedNotification)

        val actualNotification = dataSource.getNotification(addedNotification.id)
        assertThat(actualNotification).isEqualTo(addedNotification)
    }

    @Test
    fun `Add notification`() = runTest {
        val notification = STUB_DTO_NOTIFICATIONS[0].copy(id = 0L)
        dataSource.addOrUpdateNotification(notification)

        dataSource.getNotifications().test {
            assertThat(awaitItem()).containsExactly(notification)
        }
    }

    @Test
    fun `Update notification`() = runTest {
        val initialNotification = STUB_DTO_NOTIFICATIONS[0].copy(id = 0L)
        dataSource.addOrUpdateNotification(initialNotification)

        val updatedNotification = initialNotification.copy(priceLessThenTrigger = 100.0)
        dataSource.addOrUpdateNotification(updatedNotification)

        dataSource.getNotifications().test {
            assertThat(awaitItem()).containsExactly(updatedNotification)
        }
    }

    @Test
    fun `Delete notification`() = runTest {
        val notification = STUB_DTO_NOTIFICATIONS[0].copy(id = 0L)
        dataSource.addOrUpdateNotification(notification)
        dataSource.deleteNotification(notification.id)

        dataSource.getNotifications().test {
            assertThat(awaitItem()).isEmpty()
        }
    }

    @Test
    fun `Filter notifications`() = runTest {
        val notification1 = STUB_DTO_NOTIFICATIONS[0].copy(id = 0L)
        val notification2 = STUB_DTO_NOTIFICATIONS[1].copy(id = 0L)
        dataSource.addOrUpdateNotification(notification1)
        dataSource.addOrUpdateNotification(notification2)

        dataSource.getNotifications(notification2.coinUuid).test {
            assertThat(awaitItem()).containsExactly(notification2)
        }
    }
}