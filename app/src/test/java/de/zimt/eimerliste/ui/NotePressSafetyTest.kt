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
        g.advance(2000)
        assertTrue(g.mayCommitBurn())
        g.down(3000)
        assertFalse(g.mayCommitBurn())
        assertEquals(NotePressSafety.Phase.PRESSED, g.advance(3000))
    }
}
