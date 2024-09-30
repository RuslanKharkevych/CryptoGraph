package me.khruslan.cryptograph.ui.tests

import com.google.common.truth.Truth.assertThat
import me.khruslan.cryptograph.data.fixtures.STUB_COINS
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.coins.main.CoinsViewModel
import me.khruslan.cryptograph.ui.fakes.FakeCoinsRepository
import me.khruslan.cryptograph.ui.fakes.FakeNotificationsRepository
import me.khruslan.cryptograph.ui.rules.MainDispatcherRule
import me.khruslan.cryptograph.ui.util.UiState
import org.junit.Before
import org.junit.Rule
import org.junit.Test

internal class CoinsViewModelTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeCoinsRepository: FakeCoinsRepository
    private lateinit var fakeNotificationsRepository: FakeNotificationsRepository
    private lateinit var viewModel: CoinsViewModel

    @Before
    fun setUp() {
        fakeCoinsRepository = FakeCoinsRepository()
        fakeNotificationsRepository = FakeNotificationsRepository()
        viewModel = CoinsViewModel(fakeCoinsRepository, fakeNotificationsRepository)
    }

    @Test
    fun `Reload coins - success`() {
        viewModel.reloadCoins()

        val expectedListState = UiState.Data(STUB_COINS)
        val actualListState = viewModel.coinsState.listState
        assertThat(actualListState).isEqualTo(expectedListState)
    }

    @Test
    fun `Reload coins - failure`() {
        fakeCoinsRepository.isNetworkReachable = false
        viewModel.reloadCoins()

        val expectedListState = UiState.Error(R.string.network_error_msg)
        val actualListState = viewModel.coinsState.listState
        assertThat(actualListState).isEqualTo(expectedListState)
    }

    @Test
    fun `Pin coin - success`() {
        viewModel.pinCoin(id = STUB_COINS[1].id)

        val expectedCoins = listOf(
            STUB_COINS[1].copy(isPinned = true),
            STUB_COINS[0],
            STUB_COINS[2]
        )
        val expectedListState = UiState.Data(expectedCoins)
        val actualListState = viewModel.coinsState.listState
        assertThat(actualListState).isEqualTo(expectedListState)
    }

    @Test
    fun `Pin coin - failure`() {
        fakeCoinsRepository.isDatabaseCorrupted = true
        viewModel.pinCoin(id = "test-id")

        val expectedWarningMessageRes = R.string.pin_coin_warning_msg
        val actualWarningMessageRes = viewModel.coinsState.warningMessageRes
        assertThat(actualWarningMessageRes).isEqualTo(expectedWarningMessageRes)
    }

    @Test
    fun `Unpin coin - success`() {
        val coinId = STUB_COINS[0].id
        viewModel.pinCoin(coinId)
        viewModel.unpinCoin(coinId)

        val expectedCoins = STUB_COINS
        val expectedListState = UiState.Data(expectedCoins)
        val actualListState = viewModel.coinsState.listState
        assertThat(actualListState).isEqualTo(expectedListState)
    }

    @Test
    fun `Unpin coin - failure`() {
        fakeCoinsRepository.isDatabaseCorrupted = true
        viewModel.unpinCoin(id = "test-id")

        val expectedWarningMessageRes = R.string.unpin_coin_warning_msg
        val actualWarningMessageRes = viewModel.coinsState.warningMessageRes
        assertThat(actualWarningMessageRes).isEqualTo(expectedWarningMessageRes)
    }

    @Test
    fun `Warning shown`() {
        fakeCoinsRepository.isDatabaseCorrupted = true
        viewModel.pinCoin(id = "test-id")
        viewModel.warningShown()

        val warningMessageRes = viewModel.coinsState.warningMessageRes
        assertThat(warningMessageRes).isNull()
    }

    @Test
    fun `Load unread notifications`() {
        val expectedUnreadNotificationsCount = fakeNotificationsRepository.unreadNotificationsCount
        val actualUnreadNotificationsCount = viewModel.coinsState.unreadNotificationsCount
        assertThat(actualUnreadNotificationsCount).isEqualTo(expectedUnreadNotificationsCount)
    }
}