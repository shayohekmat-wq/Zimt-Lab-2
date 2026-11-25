package de.zimt.eimerliste.data

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/** Separate instrumentation invocations verify that data is not merely held in RAM. */
@RunWith(AndroidJUnit4::class)
class DraftPersistenceTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val sketch = "ZIMT-SKETCH/1\nff000000,2.75:0,0|10000,10000\n"
    private val first = DraftSnapshot("Text\nAbsatz\n  Äöß 😀\n", sketch, true)
    private val folded = first.copy(sketchOpen = false, foldedText = first.text, noteId = "stable-draft")

    @Test fun seedDraft() {
        check(context.packageName.endsWith(".debug"))
        // Seed the raw preference format used before drawings were stored as Base64.
        val legacy = context.getSharedPreferences("eimer-draft-v1", Context.MODE_PRIVATE)
        assertTrue(legacy.edit().clear().putString("drawing", sketch)
            .putBoolean("sketchOpen", true).commit())
        val recovered = DraftPreferences.load(context)
        assertEquals(DraftSnapshot(drawing = sketch, sketchOpen = true), recovered)
        DraftPreferences.saveIfChanged(context, recovered.copy(text = first.text))
        assertEquals(first, DraftPreferences.load(context))
        DraftPreferences.saveIfChanged(context, folded)
        assertEquals(folded, DraftPreferences.load(context))
        assertTrue(DraftPreferences.flush(context))
    }

    @Test fun verifyDraftAfterRestart() {
        check(context.packageName.endsWith(".debug"))
        try {
            assertEquals(folded, DraftPreferences.load(context))
        } finally {
            DraftPreferences.saveIfChanged(context, DraftSnapshot())
        }
        assertEquals(DraftSnapshot(), DraftPreferences.load(context))
    }
}
