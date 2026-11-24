package de.zimt.eimerliste.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BurnEdgeTest {
    @Test fun maskStartsFullAndEndsEmptyWithoutResurrectingPaper() {
        for (column in 0..32) {
            val x = column / 32f
            assertEquals(1f, burnEdgeFraction(0f, x), 0.00001f)
            assertEquals(0f, burnEdgeFraction(1f, x), 0.00001f)
            var previous = 1f
            for (step in 1..100) {
                val edge = burnEdgeFraction(step / 100f, x)
                assertTrue(edge <= previous + .00001f)
                assertTrue(edge in 0f..1f)
                previous = edge
            }
        }
    }
}
