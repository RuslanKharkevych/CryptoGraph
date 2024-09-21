package me.khruslan.cryptograph.ui.tests

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.khruslan.cryptograph.data.fixtures.STUB_COINS
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.coins.picker.CoinPickerViewModel
import me.khruslan.cryptograph.ui.fakes.FakeCoinsRepository
import me.khruslan.cryptograph.ui.rules.MainDispatcherRule
import me.khruslan.cryptograph.ui.util.UiState
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class CoinPickerViewModelTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeCoinsRepository: FakeCoinsRepository
    private lateinit var viewModel: CoinPickerViewModel

    @Before
    fun setUp() {
        fakeCoinsRepository = FakeCoinsRepository()
        viewModel = CoinPickerViewModel(
            savedStateHandle = SavedStateHandle(),
            coinsRepository = fakeCoinsRepository
        )
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
}