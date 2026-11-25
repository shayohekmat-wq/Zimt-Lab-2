package de.zimt.eimerliste.data

import de.zimt.eimerliste.domain.BucketMaterial
import de.zimt.eimerliste.domain.NoteRules
import de.zimt.eimerliste.domain.PaperDrawingCodec
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal fun NoteEntity.supportsDrawingEdits(): Boolean =
    (drawingPayloadVersion == null || drawingPayloadVersion == 1) &&
        (drawingPayload == null || PaperDrawingCodec.hasInk(drawingPayload))

class EimerRepository(private val dao: EimerDao) {
    val buckets = dao.observeBuckets()
    val counts = dao.observeCounts()

    suspend fun savedNote(id: String): NoteEntity? = withContext(Dispatchers.IO) { dao.noteById(id) }

    fun notes(bucketId: String) = dao.observeNotes(bucketId)
    fun deletedNotes(bucketId: String) = dao.observeDeletedNotes(bucketId)

    suspend fun randomNote(bucketId: String): NoteEntity? =
        withContext(Dispatchers.IO) { dao.randomNote(bucketId) }

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

    /**
     * Preserve the original note row, drawing and stable ID.
     * The DAO rejects stale source IDs, deleted notes and missing destinations.
     */
    suspend fun moveNote(noteId: String, sourceBucketId: String, destinationBucketId: String) =
        withContext(Dispatchers.IO) {
            require(noteId.isNotBlank() && sourceBucketId.isNotBlank() && destinationBucketId.isNotBlank()) {
                "Ungültiger Zettel oder Eimer."
            }
            require(sourceBucketId != destinationBucketId) { "Der Zettel liegt bereits in diesem Eimer." }
            check(dao.moveNote(noteId, sourceBucketId, destinationBucketId, System.currentTimeMillis()) == 1) {
                "Zettel oder Zieleimer nicht mehr verfügbar."
            }
        }

    suspend fun placeEditedNote(id: String, source: String, destination: String, text: String, drawing: String?) =
        withContext(Dispatchers.IO) {
            require(NoteRules.canSubmit(text, drawing)) { "Der Zettel ist leer oder ungültig." }
            val original = checkNotNull(dao.noteById(id)) { "Originalzettel nicht mehr verfügbar." }
            val drawingVersion = when {
                drawing == original.drawingPayload -> original.drawingPayloadVersion
                !original.supportsDrawingEdits() -> error("Das vorhandene Skizzenformat kann hier nicht bearbeitet werden.")
                drawing == null -> null
                PaperDrawingCodec.hasInk(drawing) -> 1
                else -> error("Ungültige Skizze. Der Originalzettel bleibt erhalten.")
            }
            check(dao.placeEditedNote(id, source, destination, NoteRules.normalize(text), drawing,
                drawingVersion, original.drawingPayload, original.drawingPayloadVersion,
                System.currentTimeMillis()) == 1) {
                "Originalzettel geändert oder Zieleimer nicht mehr verfügbar."
            }
        }

    /** Soft-delete only; no hard deletion is performed by this workflow. */
    suspend fun burnNote(noteId: String): Long = withContext(Dispatchers.IO) {
        require(noteId.isNotBlank()) { "Ungültige Notiz." }
        val deletedAt = System.currentTimeMillis()
        check(dao.softDeleteNote(noteId, deletedAt) == 1) { "Notiz nicht mehr verfügbar." }
        deletedAt
    }

    suspend fun undoBurn(noteId: String, deletedAt: Long) = withContext(Dispatchers.IO) {
        check(dao.restoreNote(noteId, deletedAt, System.currentTimeMillis()) == 1) {
            "Die Notiz konnte nicht wiederhergestellt werden."
        }
    }

    suspend fun createNote(bucketId: String, text: String, drawingPayload: String? = null,
                           draftId: String = UUID.randomUUID().toString()): String = withContext(Dispatchers.IO) {
        require(NoteRules.canSubmit(text, drawingPayload)) { "Der Zettel ist leer, die Skizze ungültig oder der Text zu lang." }
        val now = System.currentTimeMillis()
        require(draftId.isNotBlank()) { "Ungültige Zettel-ID." }
        val normalizedText = NoteRules.normalize(text)
        require(drawingPayload == null || PaperDrawingCodec.hasInk(drawingPayload)) {
            "Die Skizze kann nicht gelesen werden. Der Entwurf bleibt erhalten."
        }
        // The stable ID belongs to the folded draft. A retry after a committed insert
        // must acknowledge the original row, not duplicate or overwrite it.
        val inserted = dao.addNote(
            NoteEntity(
                id = draftId,
                bucketId = bucketId,
                text = normalizedText,
                drawingPayload = drawingPayload,
                drawingPayloadVersion = if (drawingPayload != null) 1 else null,
                createdAt = now,
                updatedAt = now
            )
        )
        if (inserted == -1L) {
            val original = dao.noteById(draftId)
            check(original != null && original.text == normalizedText &&
                original.drawingPayload == drawingPayload) {
                "Die Zettel-ID ist bereits mit einem anderen Inhalt belegt."
            }
        }
        draftId
    }
}
