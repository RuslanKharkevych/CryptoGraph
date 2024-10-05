package me.khruslan.cryptograph.base

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

private const val LOG_TAG = "CryptoGraphLogger"

object Logger : AbstractLogger by loggerImpl()

private interface AbstractLogger {
    fun debug(tag: String, message: String)
    fun info(tag: String, message: String)
    fun warning(tag: String, message: String, throwable: Throwable)
    fun error(tag: String, message: String, throwable: Throwable)
}

private fun loggerImpl() = if (BuildConfig.DEBUG) ConsoleLogger() else FirebaseCrashlyticsLogger()

private class ConsoleLogger : AbstractLogger {
    override fun debug(tag: String, message: String) {
        Log.d(LOG_TAG, taggedMessage(tag, message))
    }

    override fun info(tag: String, message: String) {
        Log.i(LOG_TAG, taggedMessage(tag, message))
    }

    override fun warning(tag: String, message: String, throwable: Throwable) {
        Log.w(LOG_TAG, taggedMessage(tag, message), throwable)
    }

    override fun error(tag: String, message: String, throwable: Throwable) {
        Log.e(LOG_TAG, taggedMessage(tag, message), throwable)
    }
}

private class FirebaseCrashlyticsLogger : AbstractLogger {
    override fun debug(tag: String, message: String) {}

    override fun info(tag: String, message: String) {
        log(tag, message)
    }

    override fun warning(tag: String, message: String, throwable: Throwable) {
        log(tag, message, throwable)
    }

    override fun error(tag: String, message: String, throwable: Throwable) {
        log(tag, message, throwable)
    }

    private fun log(tag: String, message: String, throwable: Throwable? = null) {
        Firebase.crashlytics.log(taggedMessage(tag, message))

        if (throwable != null) {
            Firebase.crashlytics.recordException(throwable)
        }
    }
}

private fun taggedMessage(tag: String, message: String): String {
    return "[$tag] $message"
}