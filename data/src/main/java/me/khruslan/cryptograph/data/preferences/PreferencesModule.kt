package me.khruslan.cryptograph.data.preferences

import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import kotlinx.coroutines.Dispatchers
import me.khruslan.cryptograph.data.common.objectBoxModule
import me.khruslan.cryptograph.data.preferences.local.PreferencesLocalDataSourceImpl
import me.khruslan.cryptograph.data.preferences.mapper.PreferencesMapper
import org.koin.dsl.module

internal val preferencesModule = module {
    includes(objectBoxModule)
    single<PreferencesRepository> {
        PreferencesRepositoryImpl(
            localDataSource = PreferencesLocalDataSourceImpl(
                box = get<BoxStore>().boxFor(),
                dispatcher = Dispatchers.IO
            ),
            mapper = PreferencesMapper()
        )
    }
}