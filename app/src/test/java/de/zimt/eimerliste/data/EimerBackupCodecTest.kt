package de.zimt.eimerliste.data

import java.io.ByteArrayInputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class EimerBackupCodecTest {
    private val bucket = BucketEntity("bucket-1", "München & Toronto", "legacy-design", 10, 20)
    private val note = NoteEntity("note-1", bucket.id, "Hallo\nWelt", "local", 1,
        "ZIMT-SKETCH/1\nff000000,2:0,0|10000,10000\n", 11, 25, 25)
    private val snapshot = EimerBackupSnapshot(listOf(bucket), listOf(note),
        mapOf(bucket.id to "user-wicker-01"))

    @Test fun roundTripRetainsStableIdsSketchAndTombstone() {
        val decoded = EimerBackupCodec.decode(ByteArrayInputStream(EimerBackupCodec.encode(snapshot)))
        assertEquals(snapshot, decoded)
    }

    @Test fun duplicateBucketIsRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            EimerBackupCodec.encode(snapshot.copy(buckets = listOf(bucket, bucket)))
        }
    }

    @Test fun orphanNoteIsRejectedBeforeAnyRestore() {
        assertThrows(IllegalArgumentException::class.java) {
            EimerBackupCodec.encode(snapshot.copy(notes = listOf(note.copy(bucketId = "missing"))))
        }
    }

    @Test fun wrongVersionIsRejected() {
        val unsupported = """{"format":"zimt-eimerliste","version":999,"buckets":[],"notes":[],"designs":{}}"""
        assertThrows(IllegalArgumentException::class.java) {
            EimerBackupCodec.decode(ByteArrayInputStream(unsupported.toByteArray()))
        }
    }
}
