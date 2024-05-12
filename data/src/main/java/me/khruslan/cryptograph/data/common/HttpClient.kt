package me.khruslan.cryptograph.data.common

import android.content.Context
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.BuildConfig
import okhttp3.Cache
import okhttp3.Call
import okhttp3.Connection
import okhttp3.EventListener
import okhttp3.Handshake
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import java.io.File
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy

private const val HTTP_CACHE_PATH = "http_cache"
private const val HTTP_CACHE_MAX_SIZE = 50L * 1024L * 1024L
private const val LOG_TAG = "OkHttpClient"

internal fun buildHttpClient(context: Context): OkHttpClient {
    val cache = Cache(
        directory = File(context.cacheDir, HTTP_CACHE_PATH),
        maxSize = HTTP_CACHE_MAX_SIZE
    )

    val loggingInterceptor = HttpLoggingInterceptor(HttpLogger()).apply {
        level = if (BuildConfig.DEBUG) Level.BODY else Level.BASIC
    }

    val eventListener = if (BuildConfig.DEBUG) {
        HttpEventsListener()
    } else {
        EventListener.NONE
    }

    return OkHttpClient.Builder()
        .cache(cache)
        .addInterceptor(loggingInterceptor)
        .eventListener(eventListener)
        .build()
}

private class HttpLogger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        Logger.info(LOG_TAG, message)
    }
}

private class HttpEventsListener : EventListener() {
    private var callStartNanos = 0L

    private fun printEvent(name: String) {
        val nowNanos = System.nanoTime()
        if (name == "callStart") callStartNanos = nowNanos
        val elapsedNanos = nowNanos - callStartNanos
        val message = "%.3f %s%n".format(elapsedNanos / 1000000000.0, name)
        Logger.debug(LOG_TAG, message)
    }

    override fun callStart(call: Call) {
        printEvent("callStart")
    }

    override fun proxySelectStart(call: Call, url: HttpUrl) {
        printEvent("proxySelectStart")
    }

    override fun proxySelectEnd(call: Call, url: HttpUrl, proxies: List<Proxy>) {
        printEvent("proxySelectEnd")
    }

    override fun dnsStart(call: Call, domainName: String) {
        printEvent("dnsStart")
    }

    override fun dnsEnd(call: Call, domainName: String, inetAddressList: List<InetAddress>) {
        printEvent("dnsEnd")
    }

    override fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {
        printEvent("connectStart")
    }

    override fun secureConnectStart(call: Call) {
        printEvent("secureConnectStart")
    }

    override fun secureConnectEnd(call: Call, handshake: Handshake?) {
        printEvent("secureConnectEnd")
    }

    override fun connectEnd(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?,
    ) {
        printEvent("connectEnd")
    }

    override fun connectFailed(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?,
        ioe: IOException
    ) {
        printEvent("connectFailed")
    }

    override fun connectionAcquired(call: Call, connection: Connection) {
        printEvent("connectionAcquired")
    }

    override fun connectionReleased(call: Call, connection: Connection) {
        printEvent("connectionReleased")
    }

    override fun requestHeadersStart(call: Call) {
        printEvent("requestHeadersStart")
    }

    override fun requestHeadersEnd(call: Call, request: Request) {
        printEvent("requestHeadersEnd")
    }

    override fun requestBodyStart(call: Call) {
        printEvent("requestBodyStart")
    }

    override fun requestBodyEnd(call: Call, byteCount: Long) {
        printEvent("requestBodyEnd")
    }

    override fun requestFailed(call: Call, ioe: IOException) {
        printEvent("requestFailed")
    }

    override fun responseHeadersStart(call: Call) {
        printEvent("responseHeadersStart")
    }

    override fun responseHeadersEnd(call: Call, response: Response) {
        printEvent("responseHeadersEnd")
    }

    override fun responseBodyStart(call: Call) {
        printEvent("responseBodyStart")
    }

    override fun responseBodyEnd(call: Call, byteCount: Long) {
        printEvent("responseBodyEnd")
    }

    override fun responseFailed(call: Call, ioe: IOException) {
        printEvent("responseFailed")
    }

    override fun callEnd(call: Call) {
        printEvent("callEnd")
    }

    override fun callFailed(call: Call, ioe: IOException) {
        printEvent("callFailed")
    }

    override fun canceled(call: Call) {
        printEvent("canceled")
    }

    override fun cacheHit(call: Call, response: Response) {
        printEvent("cacheHit")
    }

    override fun cacheMiss(call: Call) {
        printEvent("cacheMiss")
    }

    override fun cacheConditionalHit(call: Call, cachedResponse: Response) {
        printEvent("cacheConditionalHit")
    }

    override fun satisfactionFailure(call: Call, response: Response) {
        printEvent("satisfactionFailure")
    }
}