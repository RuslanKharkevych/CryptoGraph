package me.khruslan.cryptograph.ui

import me.khruslan.cryptograph.ui.coins.coinsModule
import org.koin.dsl.module

val uiModule = module {
    includes(coinsModule)
}