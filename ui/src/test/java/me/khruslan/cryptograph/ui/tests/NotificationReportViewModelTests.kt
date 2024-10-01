package me.khruslan.cryptograph.ui.tests

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.fixtures.STUB_NOTIFICATIONS
import me.khruslan.cryptograph.data.notifications.NotificationStatus
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.fakes.FakeNotificationsRepository
import me.khruslan.cryptograph.ui.notifications.report.NotificationReportArgKeys
import me.khruslan.cryptograph.ui.notifications.report.NotificationReportViewModel
import me.khruslan.cryptograph.ui.rules.MainDispatcherRule
import me.khruslan.cryptograph.ui.util.UiState
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private val NOTIFICATION_REPORT_ARGS = mapOf(
    NotificationReportArgKeys.NOTIFICATION_ID_ARG to 6L,
    NotificationReportArgKeys.NOTIFICATION_STATUS_ARG to NotificationStatus.Completed,
    NotificationReportArgKeys.COIN_ID_ARG to "Qwsogvtv82FCd",
    NotificationReportArgKeys.COIN_NAME_ARG to "Bitcoin",
    NotificationReportArgKeys.COIN_PRICE_ARG to "$63374.15"
)

internal class NotificationReportViewModelTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeNotificationsRepository: FakeNotificationsRepository
    private lateinit var viewModel: NotificationReportViewModel

    @Before
    fun setUp() {
        val savedStateHandle = SavedStateHandle(NOTIFICATION_REPORT_ARGS)
        fakeNotificationsRepository = FakeNotificationsRepository()
        viewModel = NotificationReportViewModel(savedStateHandle, fakeNotificationsRepository)
    }

    @Test
    fun `Reload notifications - success`() = runTest {
        viewModel.reloadNotification()

        val expectedNotificationState = UiState.Data(STUB_NOTIFICATIONS[0])
        val actualNotificationState = viewModel.notificationReportState.notificationState
        assertThat(actualNotificationState).isEqualTo(expectedNotificationState)
    }

    @Test
    fun `Reload notifications - failure`() = runTest {
        fakeNotificationsRepository.isDatabaseCorrupted = true
        viewModel.reloadNotification()

        val expectedNotificationState = UiState.Error(R.string.database_error_msg)
        val actualNotificationState = viewModel.notificationReportState.notificationState
        assertThat(actualNotificationState).isEqualTo(expectedNotificationState)
    }
}