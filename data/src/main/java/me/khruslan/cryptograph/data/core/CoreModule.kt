package me.khruslan.cryptograph.data.core

import me.khruslan.cryptograph.data.MyObjectBox
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val coreModule = module {
    single {
        MyObjectBox.builder()
            .androidContext(androidContext())
            .build()
    }
}