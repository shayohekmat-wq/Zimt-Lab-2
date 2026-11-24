package de.zimt.eimerliste.ui

import org.junit.Assert.*
import org.junit.Test

class BucketBitmapSamplingTest {
    @Test fun viewportTargetsAvoidDecodingForEveryPixelOfAResize() {
        assertEquals(128, bucketDecodeTarget(0))
        assertEquals(256, bucketDecodeTarget(200))
        assertEquals(512, bucketDecodeTarget(450))
        assertEquals(1024, bucketDecodeTarget(700))
        assertEquals(2048, bucketDecodeTarget(Int.MAX_VALUE))
    }

    @Test fun sampleRetainsEnoughPixelsForDisplayWithoutFullSourceAllocation() {
        assertEquals(4, bucketDecodeSample(2048, 2048, 512))
        assertEquals(2, bucketDecodeSample(1254, 1254, 512))
        assertEquals(8, bucketDecodeSample(2048, 2048, 256))
        assertEquals(1, bucketDecodeSample(1254, 1254, 1024))
        assertEquals(1, bucketDecodeSample(128, 128, 512))
    }
}
