package me.khruslan.cryptograph

import me.khruslan.cryptograph.base.AppVersion
import org.koin.dsl.module

val appModule = module {
    single { AppVersion(BuildConfig.VERSION_NAME) }
}