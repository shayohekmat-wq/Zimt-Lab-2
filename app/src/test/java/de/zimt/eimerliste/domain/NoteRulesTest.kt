package de.zimt.eimerliste.domain

import org.junit.Assert.*
import org.junit.Test

class NoteRulesTest {
    @Test fun emptyPaperCannotBeSubmitted() {
        assertFalse(NoteRules.canSubmit("   \n"))
    }

    @Test fun regularTextCanBeSubmitted() {
        assertTrue(NoteRules.canSubmit("Mit Freunden einen Filmabend machen"))
    }

    @Test fun maximumLengthIsEnforced() {
        assertTrue(NoteRules.canSubmit("x".repeat(600)))
        assertFalse(NoteRules.canSubmit("x".repeat(601)))
    }

    @Test fun bucketNameMustBeReadable() {
        assertNull(NoteRules.validBucketName("  "))
        assertEquals("Reisen", NoteRules.validBucketName(" Reisen "))
    }

    @Test fun sketchOnlyIsSupported() {
        val encoded = PaperDrawingCodec.encode(
            listOf(InkStroke(0xff000000.toInt(), 3f, listOf(InkPoint(0.3f, 0.4f))))
        )
        assertTrue(NoteRules.canSubmit("", encoded))
    }

    @Test fun materialKeysRemainStable() {
        assertEquals(BucketMaterial.WICKER, BucketMaterial.fromKey("wicker"))
        assertEquals(BucketMaterial.ZINC, BucketMaterial.fromKey("unknown-future-key"))
    }
}
