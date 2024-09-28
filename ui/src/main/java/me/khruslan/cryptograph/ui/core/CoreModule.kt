package me.khruslan.cryptograph.ui.core

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

internal val coreModule = module {
    viewModelOf(::CryptoGraphViewModel)
}