@file:JvmName("Log")
@file:Suppress("unused", "UNUSED_PARAMETER")

package android.util

fun d(tag: String, msg: String): Int {
    println(msg)
    return 0
}

fun i(tag: String, msg: String): Int {
    println(msg)
    return 0
}

fun w(tag: String, msg: String, tr: Throwable): Int {
    println(msg)
    println(tr.message)
    return 0
}

fun e(tag: String, msg: String, tr: Throwable): Int {
    println(msg)
    println(tr.message)
    return 0
}