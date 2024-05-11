package me.khruslan.cryptograph.ui.coins

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

internal val coinsModule = module {
    viewModelOf(::CoinsViewModel)
}