package me.khruslan.cryptograph.ui.tests

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.khruslan.cryptograph.data.fixtures.COINS
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.coins.CoinsListState
import me.khruslan.cryptograph.ui.coins.CoinsViewModel
import me.khruslan.cryptograph.ui.fakes.FakeCoinsRepository
import me.khruslan.cryptograph.ui.rules.MainDispatcherRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
internal class CoinsViewModelTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeCoinsRepository: FakeCoinsRepository
    private lateinit var viewModel: CoinsViewModel

    @Before
    fun setUp() {
        fakeCoinsRepository = FakeCoinsRepository()
        viewModel = CoinsViewModel(fakeCoinsRepository)
    }

    @Test
    fun `Reload coins - success`() {
        viewModel.reloadCoins()

        val expectedListState = CoinsListState.Data(COINS)
        val actualListState = viewModel.coinsState.listState
        assertThat(actualListState).isEqualTo(expectedListState)
    }

    @Test
    fun `Reload coins - failure`() {
        fakeCoinsRepository.isNetworkReachable = false
        viewModel.reloadCoins()

        val expectedListState = CoinsListState.Error(R.string.network_error_msg)
        val actualListState = viewModel.coinsState.listState
        assertThat(actualListState).isEqualTo(expectedListState)
    }

    @Test
    fun `Pin coin - success`() {
        viewModel.pinCoin(id = COINS[1].id)

        val expectedCoins = listOf(COINS[1].copy(isPinned = true), COINS[0], COINS[2])
        val expectedListState = CoinsListState.Data(expectedCoins)
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
        val coinId = COINS[0].id
        viewModel.pinCoin(coinId)
        viewModel.unpinCoin(coinId)

        val expectedCoins = COINS
        val expectedListState = CoinsListState.Data(expectedCoins)
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
}