package de.zimt.eimerliste.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BucketBitmapDeviceTest {
    @Test fun threeOriginalPairsShareGeometryAlphaAndBoundedDecodedMemory() {
        val resources = InstrumentationRegistry.getInstrumentation().targetContext.resources
        assertEquals(3, illustratedBucketAssets.size)
        var bytes = 0L
        illustratedBucketAssets.forEach { asset ->
            val key = BucketImageKey(asset.rear, asset.front, 512)
            val pair = checkNotNull(BucketBitmapCache.load(resources, key))
            assertEquals(pair.rear.width, pair.front.width)
            assertEquals(pair.rear.height, pair.front.height)
            assertTrue(pair.rear.hasAlpha())
            assertTrue(pair.front.hasAlpha())
            assertTrue(maxOf(pair.rear.width, pair.rear.height) in 512..1023)
            assertSame(pair, BucketBitmapCache.load(resources, key))
            bytes += pair.bytes
        }
        assertTrue("Decoded original pairs: $bytes bytes", bytes <= 10L * 1024 * 1024)
    }
}
