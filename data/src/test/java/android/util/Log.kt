@file:JvmName("Log")
@file:Suppress("unused")

package android.util

fun i(tag: String, msg: String): Int {
    println("[$tag] $msg")
    return 0
}

fun w(tag: String, msg: String, tr: Throwable): Int {
    println("[$tag] $msg")
    println(tr.message)
    return 0
}

fun e(tag: String, msg: String, tr: Throwable): Int {
    println("[$tag] $msg")
    println(tr.message)
    return 0
}