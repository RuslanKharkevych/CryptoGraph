package me.khruslan.cryptograph.ui.tests

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import me.khruslan.cryptograph.data.fixtures.STUB_COIN_HISTORY
import me.khruslan.cryptograph.data.fixtures.STUB_PREFERENCES
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.coins.history.CoinHistoryArgKeys
import me.khruslan.cryptograph.ui.coins.history.CoinHistoryViewModel
import me.khruslan.cryptograph.ui.fakes.FakeCoinsRepository
import me.khruslan.cryptograph.ui.fakes.FakePreferencesRepository
import me.khruslan.cryptograph.ui.rules.MainDispatcherRule
import me.khruslan.cryptograph.ui.util.UiState
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private val COIN_HISTORY_ARGS = mapOf(
    CoinHistoryArgKeys.COIN_ID_ARG to "Qwsogvtv82FCd",
    CoinHistoryArgKeys.COIN_NAME_ARG  to "Bitcoin",
    CoinHistoryArgKeys.COLOR_HEX_ARG to "#f7931A",
    CoinHistoryArgKeys.IS_PINNED_ARG to false
)

internal class CoinHistoryViewModelTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeCoinsRepository: FakeCoinsRepository
    private lateinit var fakePreferencesRepository: FakePreferencesRepository
    private lateinit var viewModel: CoinHistoryViewModel

    @Before
    fun setUp() {
        val savedStateHandle = SavedStateHandle(COIN_HISTORY_ARGS)
        fakeCoinsRepository = FakeCoinsRepository()
        fakePreferencesRepository = FakePreferencesRepository()

        viewModel = CoinHistoryViewModel(
            savedStateHandle = savedStateHandle,
            coinsRepository = fakeCoinsRepository,
            preferencesRepository = fakePreferencesRepository
        )
    }

    @Test
    fun `Load preferences`() {
        val chartStyle = viewModel.coinHistoryState.defaultChartStyle
        val chartPeriod = viewModel.coinHistoryState.defaultChartPeriod

        assertThat(chartStyle).isEqualTo(STUB_PREFERENCES.chartStyle)
        assertThat(chartPeriod).isEqualTo(STUB_PREFERENCES.chartPeriod)
    }

    @Test
    fun `Reload coin history - success`() {
        viewModel.reloadCoinHistory()

        val expectedChartState = UiState.Data(STUB_COIN_HISTORY)
        val actualChartState = viewModel.coinHistoryState.chartState
        assertThat(actualChartState).isEqualTo(expectedChartState)
    }

    @Test
    fun `Reload coin history - failure`() {
        fakeCoinsRepository.isNetworkReachable = false
        viewModel.reloadCoinHistory()

        val expectedChartState = UiState.Error(R.string.network_error_msg)
        val actualChartState = viewModel.coinHistoryState.chartState
        assertThat(actualChartState).isEqualTo(expectedChartState)
    }

    @Test
    fun `Pin coin - success`() {
        viewModel.pinCoin()

        val isPinned = viewModel.coinHistoryState.isPinned
        assertThat(isPinned).isTrue()
    }

    @Test
    fun `Pin coin - failure`() {
        fakeCoinsRepository.isDatabaseCorrupted = true
        viewModel.pinCoin()

        val expectedWarningMessageRes = R.string.pin_coin_warning_msg
        val actualWarningMessageRes = viewModel.coinHistoryState.warningMessageRes
        assertThat(actualWarningMessageRes).isEqualTo(expectedWarningMessageRes)
    }

    @Test
    fun `Unpin coin - success`() {
        viewModel.pinCoin()
        viewModel.unpinCoin()

        val isPinned = viewModel.coinHistoryState.isPinned
        assertThat(isPinned).isFalse()
    }

    @Test
    fun `Unpin coin - failure`() {
        fakeCoinsRepository.isDatabaseCorrupted = true
        viewModel.unpinCoin()

        val expectedWarningMessageRes = R.string.unpin_coin_warning_msg
        val actualWarningMessageRes = viewModel.coinHistoryState.warningMessageRes
        assertThat(actualWarningMessageRes).isEqualTo(expectedWarningMessageRes)
    }

    @Test
    fun `Warning shown`() {
        fakeCoinsRepository.isDatabaseCorrupted = true
        viewModel.pinCoin()
        viewModel.warningShown()

        val warningMessageRes = viewModel.coinHistoryState.warningMessageRes
        assertThat(warningMessageRes).isNull()
    }
}