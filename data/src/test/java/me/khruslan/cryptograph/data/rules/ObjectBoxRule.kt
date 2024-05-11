package me.khruslan.cryptograph.data.rules

import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import me.khruslan.cryptograph.data.coins.local.MyObjectBox
import org.junit.rules.TestWatcher
import org.junit.runner.Description

private const val DB_NAME = "test-db"

internal class ObjectBoxRule : TestWatcher() {

    private lateinit var boxStore: BoxStore

    override fun starting(description: Description?) {
        boxStore = MyObjectBox.builder()
            .inMemory(DB_NAME)
            .build()
    }

    override fun finished(description: Description?) {
        boxStore.close()
        boxStore.deleteAllFiles()
    }

    inline fun <reified T> getBox(): Box<T> {
        return boxStore.boxFor()
    }
}