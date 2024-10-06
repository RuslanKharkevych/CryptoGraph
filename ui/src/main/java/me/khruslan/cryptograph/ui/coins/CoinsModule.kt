package me.khruslan.cryptograph.ui.coins

import me.khruslan.cryptograph.ui.coins.history.CoinHistoryViewModel
import me.khruslan.cryptograph.ui.coins.main.CoinsViewModel
import me.khruslan.cryptograph.ui.coins.picker.CoinPickerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val coinsModule = module {
    viewModelOf(::CoinsViewModel)
    viewModelOf(::CoinHistoryViewModel)
    viewModelOf(::CoinPickerViewModel)
}