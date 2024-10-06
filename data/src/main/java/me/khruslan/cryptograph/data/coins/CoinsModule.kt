package me.khruslan.cryptograph.data.coins

import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import kotlinx.coroutines.Dispatchers
import me.khruslan.cryptograph.data.coins.local.CoinsLocalDataSourceImpl
import me.khruslan.cryptograph.data.coins.mapper.CoinsMapper
import me.khruslan.cryptograph.data.coins.remote.CoinsRemoteDataSourceImpl
import me.khruslan.cryptograph.data.coins.remote.interceptors.CoinsCacheInterceptor
import me.khruslan.cryptograph.data.core.DataConfig
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope
import org.koin.dsl.module

internal val coinsModule = module {
    single { coinsRepository() }
}

private fun Scope.coinsRepository(): CoinsRepository {
    val cacheInterceptor = CoinsCacheInterceptor(
        context = androidContext(),
        config = get<DataConfig>()
    )

    return CoinsRepositoryImpl(
        localDataSource = CoinsLocalDataSourceImpl(
            box = get<BoxStore>().boxFor(),
            dispatcher = Dispatchers.IO
        ),
        remoteDataSource = CoinsRemoteDataSourceImpl(
            client = get<OkHttpClient>().newBuilder()
                .addInterceptor(cacheInterceptor)
                .build(),
            config = get<DataConfig>(),
            dispatcher = Dispatchers.IO
        ),
        mapper = CoinsMapper(
            dispatcher = Dispatchers.Default
        )
    )
}