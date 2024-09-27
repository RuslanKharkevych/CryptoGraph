package me.khruslan.cryptograph.ui.tests

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.fixtures.STUB_COINS
import me.khruslan.cryptograph.data.fixtures.STUB_NOTIFICATIONS
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.coins.shared.CoinInfo
import me.khruslan.cryptograph.ui.fakes.FakeNotificationsRepository
import me.khruslan.cryptograph.ui.notifications.details.NotificationDetailsArgKeys
import me.khruslan.cryptograph.ui.notifications.details.NotificationDetailsViewModel
import me.khruslan.cryptograph.ui.rules.MainDispatcherRule
import me.khruslan.cryptograph.ui.util.UiState
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private val NOTIFICATION_DETAILS_ARGS = mapOf(
    NotificationDetailsArgKeys.NOTIFICATION_ID_ARG to 6L,
    NotificationDetailsArgKeys.NOTIFICATION_TITLE_ARG to "Bitcoin < 5000$",
    NotificationDetailsArgKeys.COIN_ID_ARG to "Qwsogvtv82FCd",
    NotificationDetailsArgKeys.COIN_NAME_ARG to "Bitcoin",
    NotificationDetailsArgKeys.COIN_PRICE_ARG to "$63374.15",
    NotificationDetailsArgKeys.COIN_EDITABLE_ARG to true
)

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

    @Test
    fun `Update coin info`() {
        initViewModel()
        val coinInfo = CoinInfo.fromCoin(STUB_COINS[0])
        viewModel.updateCoinInfo(coinInfo)

        assertThat(viewModel.notificationDetailsState.coinInfo).isEqualTo(coinInfo)
    }

    @Test
    fun `Save notification - success`() = runTest {
        initViewModel()
        viewModel.saveNotification(STUB_NOTIFICATIONS[0])

        assertThat(viewModel.notificationDetailsState.notificationSavedOrDeleted).isTrue()
    }

    @Test
    fun `Save notification - failure`() = runTest {
        initViewModel()
        fakeNotificationsRepository.isDatabaseCorrupted = true
        viewModel.saveNotification(STUB_NOTIFICATIONS[0])

        val expectedWarningMessageRes = R.string.save_notification_warning_msg
        val actualWarningMessageRes = viewModel.notificationDetailsState.warningMessageRes
        assertThat(actualWarningMessageRes).isEqualTo(expectedWarningMessageRes)
    }

    @Test
    fun `Save notification - updating`() = runTest {
        initViewModel()
        val notification = STUB_NOTIFICATIONS[0].copy(id = 0L)
        viewModel.saveNotification(notification)
        viewModel.saveNotification(notification)

        assertThat(fakeNotificationsRepository.notificationsAdded).isEqualTo(1)
    }

    @Test
    fun `Delete notification - success`() = runTest {
        initViewModel()
        viewModel.deleteNotification()

        assertThat(viewModel.notificationDetailsState.notificationSavedOrDeleted).isTrue()
    }

    @Test
    fun `Delete notification - failure`() = runTest {
        initViewModel()
        fakeNotificationsRepository.isDatabaseCorrupted = true
        viewModel.deleteNotification()

        val expectedWarningMessageRes = R.string.delete_notification_warning_msg
        val actualWarningMessageRes = viewModel.notificationDetailsState.warningMessageRes
        assertThat(actualWarningMessageRes).isEqualTo(expectedWarningMessageRes)
    }

    @Test
    fun `Delete notification - updating`() = runTest {
        initViewModel()
        viewModel.deleteNotification()
        viewModel.deleteNotification()

        assertThat(fakeNotificationsRepository.notificationsDeleted).isEqualTo(1)
    }

    @Test
    fun `Warning shown`() {
        initViewModel()
        fakeNotificationsRepository.isDatabaseCorrupted = true
        viewModel.saveNotification(STUB_NOTIFICATIONS[0])
        viewModel.warningShown()

        assertThat(viewModel.notificationDetailsState.warningMessageRes).isNull()
    }
}