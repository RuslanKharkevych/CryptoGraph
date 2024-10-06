package me.khruslan.cryptograph.ui.core

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val coreModule = module {
    viewModelOf(::CryptoGraphViewModel)
}