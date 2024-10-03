package me.khruslan.cryptograph.data.tests

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.fakes.FakeCoinsRepository
import me.khruslan.cryptograph.data.fakes.FakeNotificationsRepository
import me.khruslan.cryptograph.data.fixtures.STUB_COINS
import me.khruslan.cryptograph.data.fixtures.STUB_NOTIFICATIONS
import me.khruslan.cryptograph.data.notifications.interactors.completed.CompletedNotification
import me.khruslan.cryptograph.data.notifications.interactors.completed.CompletedNotificationsInteractor
import me.khruslan.cryptograph.data.notifications.interactors.completed.CompletedNotificationsInteractorImpl
import me.khruslan.cryptograph.data.notifications.interactors.completed.CompletedNotificationsMapper
import me.khruslan.cryptograph.data.notifications.repository.NotificationStatus
import me.khruslan.cryptograph.data.notifications.repository.NotificationTrigger
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

private val CLOCK: Clock = Clock.fixed(
    Instant.parse("2024-10-03T14:05:25.00Z"),
    ZoneId.of("America/Phoenix")
)

internal class CompletedNotificationsInteractorTests {

    private lateinit var fakeNotificationsRepository: FakeNotificationsRepository
    private lateinit var fakeCoinsRepository: FakeCoinsRepository
    private lateinit var interactor: CompletedNotificationsInteractor

    @Before
    fun setUp() {
        fakeNotificationsRepository = FakeNotificationsRepository()
        fakeCoinsRepository = FakeCoinsRepository()

        interactor = CompletedNotificationsInteractorImpl(
            notificationsRepository = fakeNotificationsRepository,
            coinsRepository = fakeCoinsRepository,
            mapper = CompletedNotificationsMapper(CLOCK)
        )
    }

    @Test
    fun `Update notifications - failed to load pending notifications`() = runTest {
        fakeNotificationsRepository.isDatabaseCorrupted = true
        val result = runCatching { interactor.getCompletedNotifications() }

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `Update notifications - no pending notifications found`() = runTest {
        fakeNotificationsRepository.deleteAllNotifications()
        val completedNotifications = interactor.getCompletedNotifications()

        assertThat(completedNotifications).isEmpty()
    }

    @Test
    fun `Update notifications - failed to load coins`() = runTest {
        fakeCoinsRepository.isNetworkReachable = false
        val result = runCatching { interactor.getCompletedNotifications() }

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `Get completed notifications - completed successfully`() = runTest {
        val notification = STUB_NOTIFICATIONS[0].copy(
            status = NotificationStatus.Pending,
            completedAt = null,
            trigger = NotificationTrigger.PriceMoreThan(100.0)
        )
        fakeNotificationsRepository.addOrUpdateNotification(notification)

        val completedNotifications = interactor.getCompletedNotifications()
        val expectedCompletedNotification = CompletedNotification(
            title = notification.title,
            coinName = STUB_COINS[0].name,
            trigger = notification.trigger
        )

        assertThat(completedNotifications).containsExactly(expectedCompletedNotification)
    }

    @Test
    fun `Try refresh completed notifications`() = runTest {
        fakeCoinsRepository.isNetworkReachable = true
        val result = runCatching { interactor.tryRefreshCompletedNotifications() }

        assertThat(result.isSuccess)
    }
}