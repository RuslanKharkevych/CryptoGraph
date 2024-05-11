package me.khruslan.cryptograph.ui.coins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.khruslan.cryptograph.data.coins.CoinsRepository
import me.khruslan.cryptograph.data.common.DataException
import me.khruslan.cryptograph.ui.R
import me.khruslan.cryptograph.ui.common.displayMessageRes

internal class CoinsViewModel(private val coinsRepository: CoinsRepository) : ViewModel() {

    private val _coinsState = MutableCoinsState()
    val coinsState: CoinsState = _coinsState

    init {
        loadCoins()
    }

    fun reloadCoins() {
        _coinsState.listState = CoinsListState.Loading
        loadCoins()
    }

    fun pinCoin(id: String) {
        viewModelScope.launch {
            try {
                coinsRepository.pinCoin(id)
            } catch (_: DataException) {
                _coinsState.warningMessageRes = R.string.pin_coin_warning_msg
            }
        }
    }

    fun unpinCoin(id: String) {
        viewModelScope.launch {
            try {
                coinsRepository.unpinCoin(id)
            } catch (_: DataException) {
                _coinsState.warningMessageRes = R.string.unpin_coin_warning_msg
            }
        }
    }

    fun warningShown() {
        _coinsState.warningMessageRes = null
    }

    private fun loadCoins() {
        viewModelScope.launch {
            try {
                coinsRepository.coins.collect { coins ->
                    _coinsState.listState = CoinsListState.Data(coins)
                }
            } catch (e: DataException) {
                _coinsState.listState = CoinsListState.Error(e.displayMessageRes)
            }
        }
    }
}