package me.khruslan.cryptograph.ui.tests

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.khruslan.cryptograph.data.fixtures.STUB_NOTIFICATIONS
import me.khruslan.cryptograph.ui.fakes.FakeNotificationsRepository
import me.khruslan.cryptograph.ui.notifications.main.NotificationsViewModel
import me.khruslan.cryptograph.ui.rules.MainDispatcherRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class NotificationsViewModelTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeNotificationsRepository: FakeNotificationsRepository
    private lateinit var viewModel: NotificationsViewModel

    @Before
    fun setUp() {
        fakeNotificationsRepository = FakeNotificationsRepository()
        viewModel = NotificationsViewModel(fakeNotificationsRepository)
    }

    @Test
    fun `Load notifications`() {
        val expectedNotifications = STUB_NOTIFICATIONS
        val actualNotifications = viewModel.notificationsState.notifications
        assertThat(actualNotifications).isEqualTo(expectedNotifications)
    }
}