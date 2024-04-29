package me.khruslan.cryptograph.data

import com.google.common.truth.Truth.assertThat
import me.khruslan.cryptograph.data.coins.rates.CoinrankingService
import me.khruslan.cryptograph.data.common.NetworkConnectionException
import me.khruslan.cryptograph.data.common.ResponseDeserializationException
import me.khruslan.cryptograph.data.common.UnsuccessfulResponseException
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.net.HttpURLConnection
import java.nio.charset.StandardCharsets

private const val RESPONSES_ROOT_PATH = "responses"
private const val GET_COINS_SUCCESSFUL_RESPONSE = "$RESPONSES_ROOT_PATH/get-coins-success.json"
private const val GET_COINS_FAILED_RESPONSE = "$RESPONSES_ROOT_PATH/get-coins-failure.json"
private const val GET_COINS_INVALID_RESPONSE = "$RESPONSES_ROOT_PATH/get-coins-invalid.json"

class CoinrankingServiceTests {

    private lateinit var mockServer: MockWebServer
    private lateinit var coinrankingService: CoinrankingService

    @Before
    fun init() {
        mockServer = MockWebServer()
        mockServer.start()

        val hostInterceptor = MockServerHostInterceptor(mockServer.url("/"))
        val client = OkHttpClient.Builder()
            .addInterceptor(hostInterceptor)
            .build()
        coinrankingService = CoinrankingService(client)
    }

    @After
    fun shutdown() {
        mockServer.shutdown()
    }

    @Test
    fun `Get coins - success`() {
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setResponseFile(GET_COINS_SUCCESSFUL_RESPONSE)
        mockServer.enqueue(mockResponse)

        val coins = coinrankingService.getCoins()
        assertThat(coins).isNotEmpty()
    }

    @Test
    fun `Get coins - failure`() {
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
            .setResponseFile(GET_COINS_FAILED_RESPONSE)
        mockServer.enqueue(mockResponse)

        assertThrows(UnsuccessfulResponseException::class.java) {
            coinrankingService.getCoins()
        }
    }

    @Test
    fun `Get coins - invalid response`() {
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setResponseFile(GET_COINS_INVALID_RESPONSE)
        mockServer.enqueue(mockResponse)

        assertThrows(ResponseDeserializationException::class.java) {
            coinrankingService.getCoins()
        }
    }

    @Test
    fun `Get coins - network connection error`() {
        val mockResponse = MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START)
        mockServer.enqueue(mockResponse)

        assertThrows(NetworkConnectionException::class.java) {
            coinrankingService.getCoins()
        }
    }

    private fun MockResponse.setResponseFile(fileName: String): MockResponse {
        val inputStream = javaClass.classLoader!!.getResourceAsStream(fileName)!!
        val responseBody = inputStream.source().buffer().readString(StandardCharsets.UTF_8)
        setBody(responseBody)
        return this
    }
}

private class MockServerHostInterceptor(private val mockServerUrl: HttpUrl): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val url = request.url.newBuilder()
            .scheme(mockServerUrl.scheme)
            .host(mockServerUrl.host)
            .port(mockServerUrl.port)
            .build()

        request = request.newBuilder()
            .url(url)
            .build()

        return chain.proceed(request)
    }
}