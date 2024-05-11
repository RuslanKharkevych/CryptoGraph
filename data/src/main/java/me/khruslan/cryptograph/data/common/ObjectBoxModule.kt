package me.khruslan.cryptograph.data.common

import me.khruslan.cryptograph.data.coins.local.MyObjectBox
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val objectBoxModule = module {
    single {
        MyObjectBox.builder()
            .androidContext(androidContext())
            .build()
    }
}