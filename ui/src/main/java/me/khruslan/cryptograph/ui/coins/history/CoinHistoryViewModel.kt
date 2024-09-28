package me.khruslan.cryptograph.ui.coins.history

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.khruslan.cryptograph.data.coins.CoinPrice
import me.khruslan.cryptograph.data.coins.CoinsRepository
import me.khruslan.cryptograph.data.common.DataException
import me.khruslan.cryptograph.data.preferences.ChartPeriod
import me.khruslan.cryptograph.data.preferences.ChartStyle
import me.khruslan.cryptograph.data.preferences.PreferencesRepository
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.util.UiState
import me.khruslan.cryptograph.ui.util.displayMessageRes

internal class CoinHistoryViewModel(
    savedStateHandle: SavedStateHandle,
    private val coinsRepository: CoinsRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    private val args = CoinHistoryArgs.fromSavedStateHandle(savedStateHandle)

    private val _coinHistoryState = MutableCoinHistoryState(args)
    val coinHistoryState: CoinHistoryState = _coinHistoryState

    init {
        loadPreferencesAndCoinHistory()
    }

    fun pinCoin() {
        viewModelScope.launch {
            try {
                coinsRepository.pinCoin(args.coinId)
                _coinHistoryState.isPinned = true
            } catch (_: DataException) {
                _coinHistoryState.warningMessageRes = R.string.pin_coin_warning_msg
            }
        }
    }

    fun unpinCoin() {
        viewModelScope.launch {
            try {
                coinsRepository.unpinCoin(args.coinId)
                _coinHistoryState.isPinned = false
            } catch (_: DataException) {
                _coinHistoryState.warningMessageRes = R.string.unpin_coin_warning_msg
            }
        }
    }

    fun warningShown() {
        _coinHistoryState.warningMessageRes = null
    }

    fun reloadCoinHistory() {
        _coinHistoryState.chartState = UiState.Loading
        viewModelScope.launch {
            loadCoinHistory()
        }
    }

    private fun loadPreferencesAndCoinHistory() {
        viewModelScope.launch {
            preferencesRepository.preferences.collect { preferences ->
                _coinHistoryState.defaultChartStyle = preferences.chartStyle
                _coinHistoryState.defaultChartPeriod = preferences.chartPeriod
                loadCoinHistory()
            }
        }
    }

    private suspend fun loadCoinHistory() {
        try {
            val history = coinsRepository.getCoinHistory(args.coinId)
            _coinHistoryState.chartState = UiState.Data(history)
        } catch (e: DataException) {
            _coinHistoryState.chartState = UiState.Error(e.displayMessageRes)
        }
    }
}

@Stable
internal interface CoinHistoryState {
    val coinName: String
    val colorHex: String?
    val chartState: UiState<List<CoinPrice>>
    val defaultChartStyle: ChartStyle
    val defaultChartPeriod: ChartPeriod
    val isPinned: Boolean
    val warningMessageRes: Int?
    val notificationBadgeCount: Int
}

internal class MutableCoinHistoryState(args: CoinHistoryArgs) : CoinHistoryState {
    override val coinName: String = args.coinName
    override val colorHex: String? = args.colorHex
    override var chartState: UiState<List<CoinPrice>> by mutableStateOf(UiState.Loading)
    override lateinit var defaultChartPeriod: ChartPeriod
    override lateinit var defaultChartStyle: ChartStyle
    override var isPinned: Boolean by mutableStateOf(args.isPinned)
    override var warningMessageRes: Int? by mutableStateOf(null)
    override var notificationBadgeCount: Int by mutableIntStateOf(0)
}