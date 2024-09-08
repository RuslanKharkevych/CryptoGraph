package me.khruslan.cryptograph.ui.core

import android.content.Context
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import me.khruslan.cryptograph.ui.BuildConfig

private const val IMAGE_CACHE_PATH = "image_cache"
private const val MEMORY_CACHE_MAX_SIZE_PERCENT = 0.25
private const val DISK_CACHE_MAX_SIZE_PERCENT = 0.02

fun buildImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .logger(if (BuildConfig.DEBUG) DebugLogger() else null)
        .components {
            add(SvgDecoder.Factory())
        }
        .memoryCache {
            MemoryCache.Builder(context)
                .maxSizePercent(MEMORY_CACHE_MAX_SIZE_PERCENT)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve(IMAGE_CACHE_PATH))
                .maxSizePercent(DISK_CACHE_MAX_SIZE_PERCENT)
                .build()
        }
        .build()
}