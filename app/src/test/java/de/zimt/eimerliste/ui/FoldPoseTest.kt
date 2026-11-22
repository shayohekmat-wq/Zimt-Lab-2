package de.zimt.eimerliste.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class FoldPoseTest {
    @Test fun eachFoldHasItsOwnNonOverlappingPhase() {
        val start = FoldPose.at(0f)
        assertEquals(0f, start.right, 0f)
        assertEquals(0f, start.bottom, 0f)
        assertEquals(0f, start.corner, 0f)

        val first = FoldPose.at(0.5f)
        assertEquals(.5f, first.right, 0f)
        assertEquals(0f, first.bottom, 0f)
        assertEquals(0f, first.corner, 0f)

        val second = FoldPose.at(1.5f)
        assertEquals(1f, second.right, 0f)
        assertEquals(.5f, second.bottom, 0f)
        assertEquals(0f, second.corner, 0f)

        val third = FoldPose.at(2.5f)
        assertEquals(1f, third.right, 0f)
        assertEquals(1f, third.bottom, 0f)
        assertEquals(.5f, third.corner, 0f)

        val done = FoldPose.at(3f)
        assertEquals(1f, done.right, 0f)
        assertEquals(1f, done.bottom, 0f)
        assertEquals(1f, done.corner, 0f)
    }

    @Test fun unusualProgressValuesRemainClamped() {
        assertEquals(0f, FoldPose.at(-8f).right, 0f)
        assertEquals(1f, FoldPose.at(99f).corner, 0f)
    }
    @Test fun allFoldPhasesAreMonotonicAndBounded() {
        var previous = FoldPose.at(0f)
        for (step in 1..300) {
            val current = FoldPose.at(step / 100f)
            for (value in listOf(current.right, current.bottom, current.corner)) {
                org.junit.Assert.assertTrue(value in 0f..1f)
            }
            org.junit.Assert.assertTrue(current.right >= previous.right)
            org.junit.Assert.assertTrue(current.bottom >= previous.bottom)
            org.junit.Assert.assertTrue(current.corner >= previous.corner)
            previous = current
        }
    }

    @Test fun outOfRangeProgressDoesNotChangeFinishedOrUnfoldedPose() {
        assertEquals(FoldPose.at(0f), FoldPose.at(-100f))
        assertEquals(FoldPose.at(3f), FoldPose.at(100f))
    }

}
