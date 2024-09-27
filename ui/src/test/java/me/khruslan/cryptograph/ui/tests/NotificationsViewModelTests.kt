package me.khruslan.cryptograph.ui.tests

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import me.khruslan.cryptograph.data.fixtures.STUB_COIN_NOTIFICATIONS
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.fakes.FakeCoinNotificationsManager
import me.khruslan.cryptograph.ui.notifications.main.NotificationsViewModel
import me.khruslan.cryptograph.ui.rules.MainDispatcherRule
import me.khruslan.cryptograph.ui.util.UiState
import org.junit.Before
import org.junit.Rule
import org.junit.Test

internal class NotificationsViewModelTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeCoinNotificationsManager: FakeCoinNotificationsManager
    private lateinit var viewModel: NotificationsViewModel

    @Before
    fun setUp() {
        val savedStateHandle = SavedStateHandle()
        fakeCoinNotificationsManager = FakeCoinNotificationsManager()
        viewModel = NotificationsViewModel(savedStateHandle, fakeCoinNotificationsManager)
    }

    @Test
    fun `Reload notifications - success`() {
        viewModel.reloadNotifications()

        val expectedState = UiState.Data(STUB_COIN_NOTIFICATIONS)
        val actualState = viewModel.notificationsState.listState
        assertThat(actualState).isEqualTo(expectedState)
    }

    @Test
    fun `Reload notifications - failure`() {
        fakeCoinNotificationsManager.isNetworkReachable = false
        viewModel.reloadNotifications()

        val expectedState = UiState.Error(R.string.network_error_msg)
        val actualState = viewModel.notificationsState.listState
        assertThat(actualState).isEqualTo(expectedState)
    }
}