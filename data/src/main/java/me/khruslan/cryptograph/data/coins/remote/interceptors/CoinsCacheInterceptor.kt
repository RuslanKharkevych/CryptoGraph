package me.khruslan.cryptograph.data.coins.remote.interceptors

import android.content.Context
import androidx.core.content.edit
import me.khruslan.cryptograph.base.Logger
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

private const val LOG_TAG = "CoinsCacheInterceptor"

private const val CACHE_MAX_STALE_SECONDS = 60 * 60
private const val CACHE_PREFERENCES_NAME = "coins_cache"
private const val CACHE_PREFERENCES_RESET_TIMESTAMP_KEY = "reset_timestamp"

private const val HEADER_RATE_LIMIT = "RateLimit-Limit"
private const val HEADER_RATE_LIMIT_RESET = "RateLimit-Reset"

internal class CoinsCacheInterceptor(context: Context): Interceptor {

    private val cachePreferences = context.getSharedPreferences(
        CACHE_PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    private var cacheResetTimestamp: Long
        get() = cachePreferences.getLong(CACHE_PREFERENCES_RESET_TIMESTAMP_KEY, 0)
        set(value) = cachePreferences.edit {
            putLong(CACHE_PREFERENCES_RESET_TIMESTAMP_KEY, value)
        }

    private val rateLimitReached: Boolean
        get() = cacheResetTimestamp >= System.currentTimeMillis()

    private val cacheControl
        get() = if (rateLimitReached) {
            CacheControl.FORCE_CACHE
        } else {
            CacheControl.Builder()
                .maxStale(CACHE_MAX_STALE_SECONDS, TimeUnit.SECONDS)
                .build()
        }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .cacheControl(cacheControl)
            .build()

        val response = chain.proceed(request)
        if (response.networkResponse != null) {
            updateCacheResetTimestamp(response)
        }

        return response
    }

    private fun updateCacheResetTimestamp(response: Response) {
        val rateLimit = response.getLongHeader(HEADER_RATE_LIMIT) ?: return

        if (rateLimit > 0L) {
            cacheResetTimestamp = 0L
        } else {
            val resetSeconds = response.getLongHeader(HEADER_RATE_LIMIT_RESET) ?: return
            val resetMillis = TimeUnit.SECONDS.toMillis(resetSeconds)
            cacheResetTimestamp = System.currentTimeMillis() + resetMillis
        }
    }

    private fun Response.getLongHeader(key: String): Long? {
        return try {
            checkNotNull(header(key)).toLong()
        } catch (e: IllegalStateException) {
            Logger.error(LOG_TAG, "Header $key is missing", e)
            return null
        } catch (e: NumberFormatException) {
            Logger.error(LOG_TAG, "Header $key has invalid format", e)
            return null
        }
    }
}