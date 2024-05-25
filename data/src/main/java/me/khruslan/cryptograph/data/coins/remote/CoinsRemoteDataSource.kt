package me.khruslan.cryptograph.data.coins.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.BuildConfig
import me.khruslan.cryptograph.data.common.NetworkConnectionException
import me.khruslan.cryptograph.data.common.ResponseDeserializationException
import me.khruslan.cryptograph.data.common.UnsuccessfulResponseException
import okhttp3.CacheControl
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

private const val LOG_TAG = "CoinsRemoteDataSource"

private const val COINRANKING_BASE_URL = "https://api.coinranking.com/v2"
private const val ACCESS_TOKEN_HEADER = "x-access-token"
private const val CACHE_MAX_STALE_SECONDS = 3600

private const val GET_COINS_REQUEST_URL = "$COINRANKING_BASE_URL/coins"
private const val LIMIT_QUERY_PARAM = "limit"
private const val LIMIT_QUERY_VALUE = "100"

internal interface CoinsRemoteDataSource {
    suspend fun getCoins(): List<CoinDto>
}

internal class CoinsRemoteDataSourceImpl(
    private val client: OkHttpClient,
    private val dispatcher: CoroutineDispatcher,
) : CoinsRemoteDataSource {

    private val jsonDeserializer = Json {
        ignoreUnknownKeys = true
    }

    private val cacheControl = CacheControl.Builder()
        .maxStale(CACHE_MAX_STALE_SECONDS, TimeUnit.SECONDS)
        .build()

    override suspend fun getCoins(): List<CoinDto> {
        return withContext(dispatcher) {
            val requestUrl = GET_COINS_REQUEST_URL.toHttpUrl()
                .newBuilder()
                .addQueryParameter(LIMIT_QUERY_PARAM, LIMIT_QUERY_VALUE)
                .build()
            executeRequest<CoinsDto>(requestUrl).coins
        }
    }

    private inline fun <reified T> executeRequest(url: HttpUrl): T {
        val request = Request.Builder()
            .url(url)
            .header(ACCESS_TOKEN_HEADER, BuildConfig.COINRANKING_API_KEY)
            .cacheControl(cacheControl)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                deserializeResponse<T>(response)
            }
        } catch (e: IOException) {
            val requestInfo = request.toString().maskAuthHeader()
            Logger.warning(LOG_TAG, "Failed to execute request: $requestInfo", e)
            throw NetworkConnectionException(e)
        }
    }

    private inline fun <reified T> deserializeResponse(response: Response): T {
        return try {
            val responseBody = requireNotNull(response.body).string()
            if (!response.isSuccessful) {
                val exception = UnsuccessfulResponseException(responseBody)
                Logger.error(LOG_TAG, "Response failed: $response", exception)
                throw exception
            }
            jsonDeserializer.decodeFromString<CoinrankingResponse<T>>(responseBody).data
        } catch (e: IllegalArgumentException) {
            Logger.error(LOG_TAG, "Failed to deserialize response: $response", e)
            throw ResponseDeserializationException(e)
        }
    }

    private fun String.maskAuthHeader(): String {
        val pattern = Regex("($ACCESS_TOKEN_HEADER:)[\\w-]+")
        return pattern.replace(this) { result ->
            val prefix = result.groupValues[1]
            "$prefix**"
        }
    }
}