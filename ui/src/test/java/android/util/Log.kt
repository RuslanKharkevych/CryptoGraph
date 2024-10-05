@file:JvmName("Log")
@file:Suppress("unused")

package android.util

fun d(tag: String, msg: String): Int {
    println("[$tag] $msg")
    return 0
}

fun i(tag: String, msg: String): Int {
    println("[$tag] $msg")
    return 0
}