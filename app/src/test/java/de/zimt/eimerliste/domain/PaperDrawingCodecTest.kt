package de.zimt.eimerliste.domain

import org.junit.Assert.*
import org.junit.Test

class PaperDrawingCodecTest {
    private val sample = listOf(
        InkStroke(0xff35261a.toInt(), 3f, listOf(InkPoint(.13f,.31f),InkPoint(.4f,.77f))),
        InkStroke(0xffaa3456.toInt(), 9f, listOf(InkPoint(.5f,.5f)))
    )

    @Test fun roundTripsStableVectorFormat() {
        val encoded = PaperDrawingCodec.encode(sample)
        assertNotNull(encoded)
        val decoded = PaperDrawingCodec.decode(encoded)
        assertEquals(2,decoded.size)
        assertEquals(2,decoded[0].points.size)
        assertEquals(.13f,decoded[0].points[0].x,.00011f)
        assertEquals(.77f,decoded[0].points[1].y,.00011f)
    }

    @Test fun aDrawingOnlyNoteIsValid() {
        val drawing=PaperDrawingCodec.encode(sample)
        assertTrue(NoteRules.canSubmit("",drawing))
        assertTrue(NoteRules.canSubmit("Text",drawing))
        assertFalse(NoteRules.canSubmit(" ",null))
        assertFalse(NoteRules.canSubmit("", "malformed"))
    }

    @Test fun corruptedAndUnknownVersionsDoNotCrash() {
        assertTrue(PaperDrawingCodec.decode("ZIMT-SKETCH/7\n").isEmpty())
        assertTrue(PaperDrawingCodec.decode("ZIMT-SKETCH/1\n123,2:abc,55\n").isEmpty())
        assertTrue(PaperDrawingCodec.decode("ZIMT-SKETCH/1\nff000000,3:10001,0\n").isEmpty())
        assertNull(PaperDrawingCodec.encode(emptyList()))
    }

    @Test fun rejectsLargeStrokes() {
        val tooMany=InkStroke(0xff000000.toInt(),3f,List(513){InkPoint(.5f,.5f)})
        assertNull(PaperDrawingCodec.encode(listOf(tooMany)))
    }
}
