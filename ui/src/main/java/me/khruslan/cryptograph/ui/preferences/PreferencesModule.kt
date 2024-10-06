package me.khruslan.cryptograph.ui.preferences

import me.khruslan.cryptograph.ui.preferences.main.PreferencesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val preferencesModule = module {
    viewModelOf(::PreferencesViewModel)
}