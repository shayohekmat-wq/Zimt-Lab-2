package de.zimt.eimerliste.ui

import org.junit.Assert.*
import org.junit.Test

class NoteHoldTimingTest {
    @Test fun boundariesAreExact() {
        assertEquals(NoteHoldTiming.Stage.PRESSED, NoteHoldTiming.stage(699, false, false))
        assertEquals(NoteHoldTiming.Stage.LIFTED, NoteHoldTiming.stage(700, false, false))
        assertEquals(NoteHoldTiming.Stage.LIFTED, NoteHoldTiming.stage(1499, false, false))
        assertEquals(NoteHoldTiming.Stage.WARNING, NoteHoldTiming.stage(1500, false, false))
        assertEquals(NoteHoldTiming.Stage.WARNING, NoteHoldTiming.stage(1999, false, false))
        assertEquals(NoteHoldTiming.Stage.IGNITED, NoteHoldTiming.stage(2000, false, false))
    }

    @Test fun movementAndReleaseDisarmIgnition() {
        assertEquals(NoteHoldTiming.Stage.DRAGGING, NoteHoldTiming.stage(5000, true, false))
        assertEquals(NoteHoldTiming.Stage.RELEASED, NoteHoldTiming.stage(5000, false, true))
        assertFalse(NoteHoldTiming.mayIgnite(2000, true, true))
        assertFalse(NoteHoldTiming.mayIgnite(2000, false, false))
        assertFalse(NoteHoldTiming.mayIgnite(1999, false, true))
        assertTrue(NoteHoldTiming.mayIgnite(2000, false, true))
    }
}
