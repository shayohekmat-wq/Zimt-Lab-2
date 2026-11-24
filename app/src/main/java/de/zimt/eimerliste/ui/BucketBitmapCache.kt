package de.zimt.eimerliste.ui

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Bucket pairs are decoded for their displayed resolution, not their full 2048px source.
 * Both layers use the same sample size and viewport, preserving original registration.
 */
internal fun bucketDecodeTarget(viewportPixels: Int): Int {
    val desired = viewportPixels.coerceIn(128, 2048)
    var size = 128
    while (size < desired) size *= 2
    return size
}

internal fun bucketDecodeSample(width: Int, height: Int, target: Int): Int {
    require(width > 0 && height > 0 && target > 0)
    val longEdge = maxOf(width, height)
    var sample = 1
    while (longEdge / sample / 2 >= target) sample *= 2
    return sample
}

internal data class BucketImageKey(val rear: Int, val front: Int, val target: Int)

internal class BucketImagePair(val rear: Bitmap, val front: Bitmap) {
    val rearImage = rear.asImageBitmap()
    val frontImage = front.asImageBitmap()
    val bytes: Int get() = rear.allocationByteCount + front.allocationByteCount
}

internal object BucketBitmapCache {
    private const val CACHE_BYTES = 20 * 1024 * 1024
    private val cache = object : LruCache<BucketImageKey, BucketImagePair>(CACHE_BYTES) {
        override fun sizeOf(key: BucketImageKey, value: BucketImagePair): Int = value.bytes
    }
    // Loading is serialized on IO to prevent several visible cards decoding the
    // same original pair in parallel. Main-thread cache reads remain cheap.
    private val loadLock = Any()

    fun peek(key: BucketImageKey): BucketImagePair? = cache.get(key)

    fun load(resources: Resources, key: BucketImageKey): BucketImagePair? = synchronized(loadLock) {
        cache.get(key)?.let { return@synchronized it }
        fun bounds(id: Int): Pair<Int, Int> {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true; inScaled = false }
            BitmapFactory.decodeResource(resources, id, options)
            return options.outWidth to options.outHeight
        }
        val dimensions = bounds(key.rear)
        if (dimensions.first <= 0 || dimensions.second <= 0 || bounds(key.front) != dimensions)
            return@synchronized null
        val sample = bucketDecodeSample(dimensions.first, dimensions.second, key.target)
        fun decode(id: Int): Bitmap? = BitmapFactory.decodeResource(resources, id,
            BitmapFactory.Options().apply {
                inScaled = false
                inSampleSize = sample
                inPreferredConfig = Bitmap.Config.ARGB_8888
            })
        val rear = decode(key.rear) ?: return@synchronized null
        val front = decode(key.front) ?: return@synchronized null
        if (rear.width != front.width || rear.height != front.height) return@synchronized null
        BucketImagePair(rear, front).also { cache.put(key, it) }
    }
}

internal data class BucketImageLoad(val images: BucketImagePair?, val loading: Boolean)

@Composable
internal fun rememberBucketImagePair(asset: IllustratedBucketAsset, viewportPixels: Int): BucketImageLoad {
    val resources = LocalContext.current.applicationContext.resources
    val key = BucketImageKey(asset.rear, asset.front, bucketDecodeTarget(viewportPixels))
    val state = remember(key) {
        val cached = BucketBitmapCache.peek(key)
        mutableStateOf(BucketImageLoad(cached, cached == null))
    }
    LaunchedEffect(key) {
        if (state.value.loading) {
            val pair = withContext(Dispatchers.IO) { BucketBitmapCache.load(resources, key) }
            state.value = BucketImageLoad(pair, false)
        }
    }
    return state.value
}
