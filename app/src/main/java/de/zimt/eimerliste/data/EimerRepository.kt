package de.zimt.eimerliste.data

import de.zimt.eimerliste.domain.BucketMaterial
import de.zimt.eimerliste.domain.NoteRules
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EimerRepository(private val dao: EimerDao) {
    val buckets = dao.observeBuckets()
    val counts = dao.observeCounts()

    fun notes(bucketId: String) = dao.observeNotes(bucketId)

    suspend fun createBucket(name: String, material: BucketMaterial): String = withContext(Dispatchers.IO) {
        val validName = requireNotNull(NoteRules.validBucketName(name)) { "Bitte einen Eimernamen mit höchstens 40 Zeichen eingeben." }
        val now = System.currentTimeMillis()
        val id = UUID.randomUUID().toString()
        dao.addBucket(
            BucketEntity(id = id, name = validName, material = material.key, createdAt = now, updatedAt = now)
        )
        id
    }

    suspend fun renameBucket(bucketId: String, name: String) = withContext(Dispatchers.IO) {
        val validName = requireNotNull(NoteRules.validBucketName(name)) { "Ungültiger Eimername." }
        check(dao.renameBucket(bucketId, validName, System.currentTimeMillis()) == 1) { "Eimer nicht gefunden." }
    }

    suspend fun createNote(bucketId: String, text: String): String = withContext(Dispatchers.IO) {
        require(NoteRules.canSubmit(text)) { "Der Zettel ist leer oder zu lang." }
        val now = System.currentTimeMillis()
        val id = UUID.randomUUID().toString()
        // The database transaction completes BEFORE the paper animation is triggered.
        dao.addNote(
            NoteEntity(
                id = id,
                bucketId = bucketId,
                text = NoteRules.normalize(text),
                createdAt = now,
                updatedAt = now
            )
        )
        id
    }
}
