package me.khruslan.cryptograph.data.core

import io.objectbox.BoxStore
import me.khruslan.cryptograph.data.MyObjectBox
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope
import org.koin.dsl.module

internal val coreModule = module {
    single { boxStore() }
    single { okHttpClient() }
}

private fun Scope.boxStore(): BoxStore {
    return MyObjectBox.builder()
        .androidContext(androidContext())
        .build()
}

private fun Scope.okHttpClient(): OkHttpClient {
    return buildHttpClient(androidContext())
}