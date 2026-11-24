package de.zimt.eimerliste.ui

import org.junit.Assert.*
import org.junit.Test

class PaperRollPoseTest {
    @Test fun paperStartsAtBottomAndExpandsUpwardsAlongReversiblePath() {
        val collapsed = paperRollPose(0f, 500f, 70f, 760f, 95f, 14f)
        val midway = paperRollPose(.5f, 500f, 70f, 760f, 95f, 14f)
        val expanded = paperRollPose(1f, 500f, 70f, 760f, 95f, 14f)
        assertEquals(.19f, collapsed.scaleY, .001f)
        assertEquals(581f, collapsed.translationY, .001f)
        assertEquals(1f, expanded.scaleY, .001f)
        assertEquals(0f, expanded.translationY, .001f)
        assertTrue(midway.scaleY > collapsed.scaleY && midway.scaleY < expanded.scaleY)
        assertTrue(midway.translationY < collapsed.translationY && midway.translationY > expanded.translationY)
    }
}
