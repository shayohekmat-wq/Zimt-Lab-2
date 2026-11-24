package de.zimt.eimerliste.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DraftPersistenceTest {
    @Test fun textSketchAndStableFoldIdentityRoundTrip() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        check(context.packageName.endsWith(".debug"))
        val sketch = "ZIMT-SKETCH/1\nff000000,2:0,0|10000,10000\n"
        val first = DraftSnapshot("Text\nAbsatz", sketch, true)
        try {
            DraftPreferences.saveIfChanged(context, first)
            assertEquals(first, DraftPreferences.load(context))
            val folded = first.copy(sketchOpen = false, foldedText = first.text, noteId = "stable-draft")
            DraftPreferences.saveIfChanged(context, folded)
            assertEquals(folded, DraftPreferences.load(context))
            DraftPreferences.saveIfChanged(context, DraftSnapshot())
            assertEquals(DraftSnapshot(), DraftPreferences.load(context))
        } finally {
            DraftPreferences.saveIfChanged(context, DraftSnapshot())
        }
    }
}
