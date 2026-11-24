package de.zimt.eimerliste.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.zimt.eimerliste.ui.BucketDesignPreferences
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/** Invoked explicitly by CI before and after adb install -r, never on a user installation. */
@RunWith(AndroidJUnit4::class)
class UpgradeFixtureTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val a = BucketEntity("upgrade-a", "Upgrade A", "wood", 11, 12)
    private val b = BucketEntity("upgrade-b", "Upgrade B", "zinc", 13, 14)
    private val sketch = "ZIMT-SKETCH/1\nff000000,2:0,0|10000,10000\n"
    private val active = NoteEntity("upgrade-note", a.id, "Text\nmit Absatz", "local", 1,
        sketch, 15, 16)
    private val burned = NoteEntity("upgrade-deleted", b.id, "Verbrannt", "local", 1,
        sketch, 17, 18, 18)

    @Test fun seedOldInstallation() = runBlocking {
        check(context.packageName.endsWith(".debug"))
        val dao = EimerDatabase.get(context).dao()
        assertTrue(dao.snapshotBuckets().isEmpty())
        dao.addBucket(a)
        dao.addBucket(b)
        dao.addNote(active)
        dao.addNote(burned)
        BucketDesignPreferences.set(context, a.id, "user-wicker-01")
        BucketDesignPreferences.set(context, b.id, "illustrated-copper-01")
        assertEquals(active, dao.noteById(active.id))
    }

    @Test fun verifyAfterUpgrade() = runBlocking {
        check(context.packageName.endsWith(".debug"))
        val dao = EimerDatabase.get(context).dao()
        assertEquals(setOf(a, b), dao.snapshotBuckets().toSet())
        assertEquals(setOf(active, burned), dao.snapshotNotes().toSet())
        assertEquals("user-wicker-01", BucketDesignPreferences.get(context, a.id))
        assertEquals("illustrated-copper-01", BucketDesignPreferences.get(context, b.id))
    }
}
