package me.khruslan.cryptograph.data.coins

import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import kotlinx.coroutines.Dispatchers
import me.khruslan.cryptograph.data.coins.local.CoinsLocalDataSourceImpl
import me.khruslan.cryptograph.data.coins.mapper.CoinsMapper
import me.khruslan.cryptograph.data.coins.remote.CoinsRemoteDataSourceImpl
import me.khruslan.cryptograph.data.coins.remote.interceptors.CoinsCacheInterceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val coinsModule = module {
    single<CoinsRepository> {
        CoinsRepositoryImpl(
            localDataSource = CoinsLocalDataSourceImpl(
                box = get<BoxStore>().boxFor(),
                dispatcher = Dispatchers.IO
            ),
            remoteDataSource = CoinsRemoteDataSourceImpl(
                client = get<OkHttpClient>().newBuilder()
                    .addInterceptor(CoinsCacheInterceptor(androidContext()))
                    .build(),
                dispatcher = Dispatchers.IO
            ),
            mapper = CoinsMapper(
                dispatcher = Dispatchers.Default
            )
        )
    }
}