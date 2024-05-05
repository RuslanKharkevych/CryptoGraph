package me.khruslan.cryptograph.base

import android.util.Log

object Logger : AbstractLogger by loggerImpl()

private interface AbstractLogger {
    fun debug(tag: String, message: String)
    fun info(tag: String, message: String)
    fun warning(tag: String, message: String, throwable: Throwable)
    fun error(tag: String, message: String, throwable: Throwable)
}

private fun loggerImpl() = if (BuildConfig.DEBUG) ConsoleLogger() else NoOpLogger()

private class ConsoleLogger : AbstractLogger {
    override fun debug(tag: String, message: String) {
        Log.d(tag, message)
    }

    override fun info(tag: String, message: String) {
        Log.i(tag, message)
    }

    override fun warning(tag: String, message: String, throwable: Throwable) {
        Log.w(tag, message, throwable)
    }

    override fun error(tag: String, message: String, throwable: Throwable) {
        Log.e(tag, message, throwable)
    }
}

private class NoOpLogger : AbstractLogger {
    override fun debug(tag: String, message: String) {}
    override fun info(tag: String, message: String) {}
    override fun warning(tag: String, message: String, throwable: Throwable) {}
    override fun error(tag: String, message: String, throwable: Throwable) {}
}