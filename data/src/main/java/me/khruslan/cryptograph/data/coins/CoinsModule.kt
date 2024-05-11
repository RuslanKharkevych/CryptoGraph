package me.khruslan.cryptograph.data.coins

import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import kotlinx.coroutines.Dispatchers
import me.khruslan.cryptograph.data.coins.local.CoinsStore
import me.khruslan.cryptograph.data.coins.remote.CoinrankingService
import me.khruslan.cryptograph.data.common.objectBoxModule
import okhttp3.OkHttpClient
import org.koin.dsl.module

internal val coinsModule = module {
    includes(objectBoxModule)
    single<CoinsRepository> {
        CoinsRepositoryImpl(
            localDataSource = CoinsStore(
                box = get<BoxStore>().boxFor(),
                dispatcher = Dispatchers.IO
            ),
            remoteDataSource = CoinrankingService(
                client = OkHttpClient(),
                dispatcher = Dispatchers.IO
            )
        )
    }
}