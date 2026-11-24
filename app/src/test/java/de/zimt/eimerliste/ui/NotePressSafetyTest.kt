package de.zimt.eimerliste.ui

import org.junit.Assert.*
import org.junit.Test

class NotePressSafetyTest {
    @Test fun exactTimingAndNoEarlyIgnition() {
        val g = NotePressSafety()
        g.down(100)
        assertEquals(NotePressSafety.Phase.PRESSED, g.advance(799))
        assertEquals(NotePressSafety.Phase.LIFTED, g.advance(800))
        assertEquals(NotePressSafety.Phase.WARNING, g.advance(1600))
        assertFalse(g.mayCommitBurn())
        assertEquals(NotePressSafety.Phase.IGNITED, g.advance(2100))
        assertTrue(g.mayCommitBurn())
    }

    @Test fun dragPermanentlyDisarmsEvenIfFingerStops() {
        val g = NotePressSafety()
        g.down(0)
        g.advance(800)
        g.move(beyondSlop = true)
        assertEquals(NotePressSafety.Phase.DRAGGING, g.advance(3000))
        assertFalse(g.mayCommitBurn())
        g.release()
        assertFalse(g.mayCommitBurn())
    }

    @Test fun secondFingerCancelsWithoutIgnition() {
        val g = NotePressSafety()
        g.down(0)
        g.move(beyondSlop = false, multiplePointers = true)
        assertEquals(NotePressSafety.Phase.CANCELLED, g.advance(5000))
        assertFalse(g.mayCommitBurn())
    }

    @Test fun releaseAndCancellationAreTerminal() {
        val a = NotePressSafety()
        a.down(0)
        a.release()
        assertEquals(NotePressSafety.Phase.RELEASED, a.advance(10000))
        assertFalse(a.mayCommitBurn())
        val b = NotePressSafety()
        b.down(0)
        b.cancel()
        assertEquals(NotePressSafety.Phase.CANCELLED, b.advance(10000))
        assertFalse(b.mayCommitBurn())
    }

    @Test fun restartClearsPriorGestureState() {
        val g = NotePressSafety()
        g.down(0)
        g.advance(700)
        g.advance(1500)
        g.advance(2000)
        assertTrue(g.mayCommitBurn())
        g.down(3000)
        assertFalse(g.mayCommitBurn())
        assertEquals(NotePressSafety.Phase.PRESSED, g.advance(3000))
    }

    @Test fun dragAfterWarningStillDisarms() {
        val g = NotePressSafety()
        g.down(0)
        g.advance(700)
        assertEquals(NotePressSafety.Phase.WARNING, g.advance(1500))
        g.move(beyondSlop = true)
        assertEquals(NotePressSafety.Phase.DRAGGING, g.advance(5000))
        assertFalse(g.mayCommitBurn())
    }

    @Test fun cancelAfterWarningNeverIgnites() {
        val g = NotePressSafety()
        g.down(0)
        g.advance(700)
        g.advance(1500)
        g.advance(1999)
        g.cancel()
        assertEquals(NotePressSafety.Phase.CANCELLED, g.advance(10000))
        assertFalse(g.mayCommitBurn())
    }

    @Test fun releaseRemembersAuthoritativePhase() {
        val g = NotePressSafety()
        g.down(100)
        assertEquals(700L, g.nextDeadlineMs())
        g.advance(800)
        assertEquals(1500L, g.nextDeadlineMs())
        g.release()
        assertEquals(NotePressSafety.Phase.RELEASED, g.phase)
        assertEquals(NotePressSafety.Phase.LIFTED, g.releasedFrom)
        assertFalse(g.isTracking())
    }

    @Test fun dragCanNeverBecomeIgnitionOrReenterHold() {
        val g = NotePressSafety()
        g.down(0)
        g.advance(700)
        g.advance(1500)
        g.move(true)
        assertNull(g.nextDeadlineMs())
        assertEquals(NotePressSafety.Phase.DRAGGING, g.advance(10000))
        g.release()
        assertEquals(NotePressSafety.Phase.DRAGGING, g.releasedFrom)
        assertFalse(g.mayCommitBurn())
    }

    @Test fun ignitionIsExactlyOnceAndCannotReenterTracking() {
        val g = NotePressSafety()
        g.down(0)
        g.advance(700)
        g.advance(1500)
        g.advance(2000)
        assertTrue(g.mayCommitBurn())
        assertFalse(g.isTracking())
        g.release()
        g.move(true)
        assertEquals(NotePressSafety.Phase.IGNITED, g.phase)
        assertTrue(g.mayCommitBurn())
    }

    @Test fun delayedFrameNeverSkipsWarningOrIgnitesImmediately() {
        val g = NotePressSafety()
        g.down(0)
        assertEquals(NotePressSafety.Phase.LIFTED, g.advance(2100))
        assertFalse(g.mayCommitBurn())
        assertEquals(NotePressSafety.Phase.WARNING, g.advance(2101))
        assertEquals(2601L, g.nextDeadlineMs())
        assertEquals(NotePressSafety.Phase.WARNING, g.advance(2600))
        assertEquals(NotePressSafety.Phase.IGNITED, g.advance(2601))
    }

    @Test fun releasingDuringLateWarningNeverBurns() {
        val g = NotePressSafety()
        g.down(0)
        g.advance(700)
        g.advance(2400)
        g.release()
        assertEquals(NotePressSafety.Phase.RELEASED, g.advance(6000))
        assertFalse(g.mayCommitBurn())
    }

    @Test fun scrollBeforeLiftCancelsAndDoesNotOpenNote() {
        val g = NotePressSafety()
        g.down(0)
        g.advance(200)
        g.move(beyondSlop = true)
        g.release()
        assertEquals(NotePressSafety.Phase.CANCELLED, g.phase)
        assertEquals(NotePressSafety.Phase.IDLE, g.releasedFrom)
    }
}
