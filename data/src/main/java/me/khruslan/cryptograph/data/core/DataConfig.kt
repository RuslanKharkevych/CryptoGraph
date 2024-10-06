package me.khruslan.cryptograph.data.core

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import me.khruslan.cryptograph.base.BuildConfig
import me.khruslan.cryptograph.base.Logger
import me.khruslan.cryptograph.data.R

private const val LOG_TAG = "DataConfig"

private const val DEBUG_FETCH_INTERVAL_SECONDS = 30L * 60L
private const val RELEASE_FETCH_INTERVAL_SECONDS = 6L * 60L * 60L

private const val KEY_CACHE_MAX_STALE_SECONDS = "cache_max_stale_seconds"
private const val KEY_PUSH_NOTIFICATIONS_INTERVAL_MINUTES = "push_notifications_interval_minutes"
private const val KEY_RATE_LIMITING_MODE_ENABLED = "rate_limiting_mode_enabled"

interface DataConfig {
    val cacheMaxStaleSeconds: Int
    val postNotificationsIntervalMinutes: Long
    val rateLimitingModeEnabled: Boolean
}

internal class DataConfigImpl : DataConfig {

    private val remoteConfig = Firebase.remoteConfig

    init {
        val settings = getRemoteConfigSettings()
        remoteConfig.setConfigSettingsAsync(settings)
        remoteConfig.setDefaultsAsync(R.xml.data_remote_config_defaults)
        fetchAndActivateRemoteConfig()
    }

    override val cacheMaxStaleSeconds
        get() = remoteConfig.getLong(KEY_CACHE_MAX_STALE_SECONDS).toInt()

    override val postNotificationsIntervalMinutes
        get() = remoteConfig.getLong(KEY_PUSH_NOTIFICATIONS_INTERVAL_MINUTES)

    override val rateLimitingModeEnabled: Boolean
        get() = remoteConfig.getBoolean(KEY_RATE_LIMITING_MODE_ENABLED)

    private fun getRemoteConfigSettings(): FirebaseRemoteConfigSettings {
        return remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) {
                DEBUG_FETCH_INTERVAL_SECONDS
            } else {
                RELEASE_FETCH_INTERVAL_SECONDS
            }
        }
    }

    private fun fetchAndActivateRemoteConfig() {
        remoteConfig.fetchAndActivate().addOnSuccessListener { updated ->
            val config = remoteConfig.all.mapValues { it.value.asString() }
            Logger.info(LOG_TAG, "Successfully fetched config: $config. Updated: $updated")
        }.addOnFailureListener { exception ->
            Logger.info(LOG_TAG, "Failed to fetch config. Error message: ${exception.message}")
        }
    }
}