package me.khruslan.cryptograph.data.tests

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import me.khruslan.cryptograph.data.coins.remote.CoinsRemoteDataSourceImpl
import me.khruslan.cryptograph.data.coins.remote.CoinsRemoteDataSource
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
import org.junit.Before
import org.junit.Test
import java.net.HttpURLConnection
import java.nio.charset.StandardCharsets
import java.util.UUID

private const val ROOT_PATH = "responses"

private const val GET_COINS_SUCCESSFUL_RESPONSE = "$ROOT_PATH/get-coins-success.json"
private const val GET_COINS_FAILED_RESPONSE = "$ROOT_PATH/get-coins-failure.json"
private const val GET_COINS_INVALID_RESPONSE = "$ROOT_PATH/get-coins-invalid.json"

private const val GET_COIN_HISTORY_SUCCESSFUL_RESPONSE = "$ROOT_PATH/get-coin-history-success.json"
private const val GET_COIN_HISTORY_FAILED_RESPONSE = "$ROOT_PATH/get-coin-history-failure.json"
private const val GET_COIN_HISTORY_INVALID_RESPONSE = "$ROOT_PATH/get-coin-history-invalid.json"

@OptIn(ExperimentalCoroutinesApi::class)
internal class CoinsRemoteDataSourceTests {

    private lateinit var mockServer: MockWebServer
    private lateinit var dataSource: CoinsRemoteDataSource

    @Before
    fun setUp() {
        mockServer = MockWebServer()
        mockServer.start()

        val hostInterceptor = MockServerHostInterceptor(mockServer.url("/"))
        val client = OkHttpClient.Builder()
            .addInterceptor(hostInterceptor)
            .build()
        val dispatcher = UnconfinedTestDispatcher()
        dataSource = CoinsRemoteDataSourceImpl(client, dispatcher)
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun `Get coins - success`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setResponseFile(GET_COINS_SUCCESSFUL_RESPONSE)
        mockServer.enqueue(mockResponse)

        val coins = dataSource.getCoins()
        assertThat(coins).isNotEmpty()
    }

    @Test
    fun `Get coins - failure`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED)
            .setResponseFile(GET_COINS_FAILED_RESPONSE)
        mockServer.enqueue(mockResponse)

        val result = runCatching { dataSource.getCoins() }
        val exception = result.exceptionOrNull()
        assertThat(exception).isInstanceOf(UnsuccessfulResponseException::class.java)
    }

    @Test
    fun `Get coins - invalid response`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setResponseFile(GET_COINS_INVALID_RESPONSE)
        mockServer.enqueue(mockResponse)

        val result = runCatching { dataSource.getCoins() }
        val exception = result.exceptionOrNull()
        assertThat(exception).isInstanceOf(ResponseDeserializationException::class.java)
    }

    @Test
    fun `Get coins - network connection error`() = runTest {
        val mockResponse = MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START)
        mockServer.enqueue(mockResponse)

        val result = runCatching { dataSource.getCoins() }
        val exception = result.exceptionOrNull()
        assertThat(exception).isInstanceOf(NetworkConnectionException::class.java)
    }

    @Test
    fun `Get coin history - success`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setResponseFile(GET_COIN_HISTORY_SUCCESSFUL_RESPONSE)
        mockServer.enqueue(mockResponse)

        val uuid = UUID.randomUUID().toString()
        val history = dataSource.getCoinHistory(uuid)
        assertThat(history).isNotEmpty()
    }

    @Test
    fun `Get coin history - failure`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
            .setResponseFile(GET_COIN_HISTORY_FAILED_RESPONSE)
        mockServer.enqueue(mockResponse)

        val uuid = "invalid-uuid"
        val result = runCatching { dataSource.getCoinHistory(uuid) }
        val exception = result.exceptionOrNull()
        assertThat(exception).isInstanceOf(UnsuccessfulResponseException::class.java)
    }

    @Test
    fun `Get coin history - invalid response`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setResponseFile(GET_COIN_HISTORY_INVALID_RESPONSE)
        mockServer.enqueue(mockResponse)

        val uuid = UUID.randomUUID().toString()
        val result = runCatching { dataSource.getCoinHistory(uuid) }
        val exception = result.exceptionOrNull()
        assertThat(exception).isInstanceOf(ResponseDeserializationException::class.java)
    }

    @Test
    fun `Get coin history - network connection error`() = runTest {
        val mockResponse = MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START)
        mockServer.enqueue(mockResponse)

        val uuid = UUID.randomUUID().toString()
        val result = runCatching { dataSource.getCoinHistory(uuid) }
        val exception = result.exceptionOrNull()
        assertThat(exception).isInstanceOf(NetworkConnectionException::class.java)
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