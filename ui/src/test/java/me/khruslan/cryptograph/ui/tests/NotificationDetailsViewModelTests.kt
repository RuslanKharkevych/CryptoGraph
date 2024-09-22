package me.khruslan.cryptograph.ui.tests

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.fixtures.STUB_NOTIFICATIONS
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.fakes.FakeNotificationsRepository
import me.khruslan.cryptograph.ui.notifications.details.NotificationDetailsArgKeys
import me.khruslan.cryptograph.ui.notifications.details.NotificationDetailsViewModel
import me.khruslan.cryptograph.ui.rules.MainDispatcherRule
import me.khruslan.cryptograph.ui.util.UiState
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private val NOTIFICATION_DETAILS_ARGS = mapOf(
    NotificationDetailsArgKeys.NOTIFICATION_ID_ARG to 1L,
    NotificationDetailsArgKeys.NOTIFICATION_TITLE_ARG to "Bitcoin < 5000$",
    NotificationDetailsArgKeys.COIN_ID_ARG to "Qwsogvtv82FCd",
    NotificationDetailsArgKeys.COIN_NAME_ARG to "Bitcoin",
    NotificationDetailsArgKeys.COIN_PRICE_ARG to "$63374.15"
)

@OptIn(ExperimentalCoroutinesApi::class)
internal class NotificationDetailsViewModelTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeNotificationsRepository: FakeNotificationsRepository
    private lateinit var viewModel: NotificationDetailsViewModel

    @Before
    fun setUp() {
        fakeNotificationsRepository = FakeNotificationsRepository()
    }

    private fun initViewModel(args: Map<String, Any?> = NOTIFICATION_DETAILS_ARGS) {
        val savedStateHandle = SavedStateHandle(args)
        viewModel = NotificationDetailsViewModel(savedStateHandle, fakeNotificationsRepository)
    }

    @Test
    fun `Initialize notification state for a new notification`() {
        val args = NOTIFICATION_DETAILS_ARGS.toMutableMap()
        args[NotificationDetailsArgKeys.NOTIFICATION_ID_ARG] = 0L
        args.remove(NotificationDetailsArgKeys.NOTIFICATION_TITLE_ARG)
        initViewModel(args)

        val expectedNotificationState = UiState.Data(null)
        val actualNotificationState = viewModel.notificationDetailsState.notificationState
        assertThat(actualNotificationState).isEqualTo(expectedNotificationState)
    }

    @Test
    fun `Reload notifications - success`() = runTest {
        initViewModel()
        viewModel.reloadNotification()

        val expectedNotificationState = UiState.Data(STUB_NOTIFICATIONS[0])
        val actualNotificationState = viewModel.notificationDetailsState.notificationState
        assertThat(actualNotificationState).isEqualTo(expectedNotificationState)
    }

    @Test
    fun `Reload notifications - failure`() = runTest {
        fakeNotificationsRepository.isDatabaseCorrupted = true
        initViewModel()
        viewModel.reloadNotification()

        val expectedNotificationState = UiState.Error(R.string.database_error_msg)
        val actualNotificationState = viewModel.notificationDetailsState.notificationState
        assertThat(actualNotificationState).isEqualTo(expectedNotificationState)
    }
}