package me.khruslan.cryptograph.data.coins.rates

import kotlinx.serialization.json.Json
import me.khruslan.cryptograph.data.BuildConfig
import me.khruslan.cryptograph.data.common.NetworkConnectionException
import me.khruslan.cryptograph.data.common.ResponseDeserializationException
import me.khruslan.cryptograph.data.common.UnsuccessfulResponseException
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

internal interface RatesRemoteDataSource {
    fun getCoins(): List<CoinDto>
}

private const val COINRANKING_BASE_URL = "https://api.coinranking.com/v2"
private const val ACCESS_TOKEN_HEADER = "x-access-token"

private const val GET_COINS_REQUEST_URL = "$COINRANKING_BASE_URL/coins"
private const val LIMIT_QUERY_PARAM = "limit"
private const val LIMIT_QUERY_VALUE = "100"

internal class CoinrankingService(private val client: OkHttpClient) : RatesRemoteDataSource {

    private val jsonDeserializer = Json {
        ignoreUnknownKeys = true
    }

    override fun getCoins(): List<CoinDto> {
        val requestUrl = GET_COINS_REQUEST_URL.toHttpUrl()
            .newBuilder()
            .addQueryParameter(LIMIT_QUERY_PARAM, LIMIT_QUERY_VALUE)
            .build()
        return executeRequest<CoinsDto>(requestUrl).coins
    }

    private inline fun <reified T> executeRequest(url: HttpUrl): T {
        return try {
            val request = Request.Builder()
                .url(url)
                .header(ACCESS_TOKEN_HEADER, BuildConfig.COINRANKING_API_KEY)
                .build()

            client.newCall(request).execute().use { response ->
                deserializeResponse<T>(response)
            }
        } catch (e: IOException) {
            throw NetworkConnectionException(e)
        }
    }

    private inline fun <reified T> deserializeResponse(response: Response): T {
        return try {
            val responseBody = requireNotNull(response.body).string()
            if (!response.isSuccessful) {
                throw UnsuccessfulResponseException(response.code, responseBody)
            }
            jsonDeserializer.decodeFromString<CoinrankingResponse<T>>(responseBody).data
        } catch (e: IllegalArgumentException) {
            throw ResponseDeserializationException(e)
        }
    }
}
