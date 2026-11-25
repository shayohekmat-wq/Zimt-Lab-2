package de.zimt.eimerliste.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class EimerPersistenceTest {
    @Test fun retryMoveBurnRestoreAndBackupSurviveDatabaseReopen() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dbName = "eimer-test-" + UUID.randomUUID()
        var database = Room.databaseBuilder(context, EimerDatabase::class.java, dbName).build()
        try {
            val a = BucketEntity("a", "A", "wood", 1, 1)
            val b = BucketEntity("b", "B", "zinc", 1, 1)
            database.dao().addBucket(a)
            database.dao().addBucket(b)
            val repo = EimerRepository(database.dao())
            val sketch = "ZIMT-SKETCH/1\nff000000,2:0,0|10000,10000\n"
            val id = "stable-draft"
            assertEquals(id, repo.createNote(a.id, "Text\nmit Absatz", sketch, id))
            assertEquals(id, repo.createNote(a.id, "Text\nmit Absatz", sketch, id))
            assertEquals(1, database.dao().snapshotNotes().size)
            repo.moveNote(id, a.id, b.id)
            val deletedAt = repo.burnNote(id)
            database.close()
            database = Room.databaseBuilder(context, EimerDatabase::class.java, dbName).build()
            val persisted = database.dao().noteById(id)!!
            assertEquals(id, persisted.id)
            assertEquals(b.id, persisted.bucketId)
            assertEquals(sketch, persisted.drawingPayload)
            assertEquals(deletedAt, persisted.deletedAt)
            EimerRepository(database.dao()).undoBurn(id, deletedAt)
            val restored = database.dao().noteById(id)!!
            assertNull(restored.deletedAt)
            assertEquals("Text\nmit Absatz", restored.text)
            val editRepo = EimerRepository(database.dao())
            editRepo.placeEditedNote(id, b.id, b.id, "Bearbeitet\nAbsatz", sketch)
            val edited = database.dao().noteById(id)!!
            assertEquals(id, edited.id)
            assertEquals(b.id, edited.bucketId)
            assertEquals(restored.createdAt, edited.createdAt)
            assertEquals("Bearbeitet\nAbsatz", edited.text)
            assertEquals(sketch, edited.drawingPayload)
            assertEquals(1, database.dao().snapshotNotes().size)
            val store = EimerBackupStore(database)
            val snapshot = store.snapshot()
            val merged = store.merge(snapshot)
            assertEquals(EimerBackupMergeResult(0, 0, 0), merged)
            val stale = snapshot.copy(notes = listOf(restored.copy(deletedAt = 999)))
            assertEquals(1, store.merge(stale).conflicts)
            assertNull(database.dao().noteById(id)!!.deletedAt)
            val targetDbName = "eimer-restore-" + UUID.randomUUID()
            val targetDb = Room.databaseBuilder(context, EimerDatabase::class.java, targetDbName).build()
            try {
                val targetStore = EimerBackupStore(targetDb)
                val export = EimerBackupCodec.encode(snapshot)
                try {
                    targetStore.merge(EimerBackupCodec.decode((export + export).inputStream()))
                    fail("Concatenated backups must be rejected before any rows are inserted")
                } catch (expected: IllegalArgumentException) {
                    assertTrue(targetStore.snapshot().buckets.isEmpty())
                    assertTrue(targetStore.snapshot().notes.isEmpty())
                }
                val padded = export + "\r\n".toByteArray()
                assertEquals(EimerBackupMergeResult(2, 1, 0),
                    targetStore.merge(EimerBackupCodec.decode(padded.inputStream())))
                assertEquals(snapshot, targetStore.snapshot())
                assertEquals(EimerBackupMergeResult(0, 0, 0), targetStore.merge(snapshot))
                // Backups may contain a newer sketch format even though the Room schema is unchanged.
                val future = edited.copy(id = "future-sketch", drawingPayloadVersion = 2,
                    drawingPayload = "ZIMT-SKETCH/2\nopaque")
                targetStore.merge(snapshot.copy(notes = listOf(future)))
                val targetRepo = EimerRepository(targetDb.dao())
                assertFalse(future.supportsDrawingEdits())
                targetRepo.placeEditedNote(future.id, b.id, a.id, "Neuer Text", future.drawingPayload)
                val withText = targetDb.dao().noteById(future.id)!!
                assertEquals("Neuer Text", withText.text)
                assertEquals(a.id, withText.bucketId)
                assertEquals(future.drawingPayload, withText.drawingPayload)
                assertEquals(future.drawingPayloadVersion, withText.drawingPayloadVersion)
                for (replacement in listOf(null, sketch)) {
                    try {
                        targetRepo.placeEditedNote(future.id, a.id, b.id, "Anderer Text", replacement)
                        fail("Unsupported original sketch must survive an edit")
                    } catch (expected: IllegalStateException) {
                        assertEquals(withText, targetDb.dao().noteById(future.id))
                    }
                }
            } finally {
                targetDb.close()
                context.deleteDatabase(targetDbName)
            }
        } finally {
            database.close()
            context.deleteDatabase(dbName)
        }
    }
}
