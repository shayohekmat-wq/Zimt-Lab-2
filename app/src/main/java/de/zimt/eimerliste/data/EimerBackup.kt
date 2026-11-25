package de.zimt.eimerliste.data

import androidx.room.withTransaction
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.ByteBuffer
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

/** Portable, app-ID-independent format. Existing Room schema and stable IDs stay unchanged. */
data class EimerBackupSnapshot(
    val buckets: List<BucketEntity>,
    val notes: List<NoteEntity>,
    val designs: Map<String, String> = emptyMap()
)

data class EimerBackupMergeResult(val bucketsAdded: Int, val notesAdded: Int, val conflicts: Int)

object EimerBackupCodec {
    const val FORMAT = "zimt-eimerliste"
    const val VERSION = 1
    private const val MAX_BYTES = 32 * 1024 * 1024
    private const val MAX_ROWS = 100_000

    fun encode(snapshot: EimerBackupSnapshot): ByteArray {
        validate(snapshot)
        val root = JSONObject()
            .put("format", FORMAT)
            .put("version", VERSION)
            .put("buckets", JSONArray().apply {
                snapshot.buckets.forEach { b ->
                    put(JSONObject().put("id", b.id).put("name", b.name)
                        .put("material", b.material).put("createdAt", b.createdAt)
                        .put("updatedAt", b.updatedAt).put("ownerId", b.ownerId)
                        .put("deletedAt", b.deletedAt ?: JSONObject.NULL))
                }
            })
            .put("notes", JSONArray().apply {
                snapshot.notes.forEach { n ->
                    put(JSONObject().put("id", n.id).put("bucketId", n.bucketId)
                        .put("text", n.text).put("authorId", n.authorId)
                        .put("drawingPayloadVersion", n.drawingPayloadVersion ?: JSONObject.NULL)
                        .put("drawingPayload", n.drawingPayload ?: JSONObject.NULL)
                        .put("createdAt", n.createdAt).put("updatedAt", n.updatedAt)
                        .put("deletedAt", n.deletedAt ?: JSONObject.NULL))
                }
            })
            .put("designs", JSONObject().apply { snapshot.designs.forEach { (id, d) -> put(id, d) } })
        return root.toString().toByteArray(Charsets.UTF_8).also {
            require(it.size <= MAX_BYTES) { "Die Sicherung ist zu groß." }
        }
    }

    fun decode(input: InputStream): EimerBackupSnapshot {
        val bytes = ByteArrayOutputStream()
        val buffer = ByteArray(8192)
        while (true) {
            val count = input.read(buffer)
            if (count < 0) break
            require(bytes.size() <= MAX_BYTES - count) { "Die Sicherungsdatei ist zu groß." }
            bytes.write(buffer, 0, count)
        }
        val json = Charsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(bytes.toByteArray())).toString()
        // Some JSONTokener versions treat a literal NUL as end-of-input.
        require('\u0000' !in json) { "Die Sicherungsdatei enthält ein ungültiges Nullzeichen." }
        val tokens = JSONTokener(json)
        val root = JSONObject(tokens)
        while (tokens.more()) {
            require(tokens.next() in " \t\r\n") {
                "Die Datei enthält weitere Daten nach der Sicherung. Bitte eine einzelne Sicherung auswählen."
            }
        }
        require(root.string("format") == FORMAT && root.long("version") == VERSION.toLong()) {
            "Unbekanntes Sicherungsformat oder Version."
        }
        val bucketArray = root.getJSONArray("buckets")
        val noteArray = root.getJSONArray("notes")
        val designObject = root.getJSONObject("designs")
        require(bucketArray.length() <= MAX_ROWS && noteArray.length() <= MAX_ROWS &&
            designObject.length() <= MAX_ROWS) { "Zu viele Einträge in der Sicherung." }
        val buckets = List(bucketArray.length()) { index ->
            val b = bucketArray.getJSONObject(index)
            BucketEntity(id = b.string("id"), name = b.string("name"),
                material = b.string("material"), createdAt = b.long("createdAt"),
                updatedAt = b.long("updatedAt"), ownerId = b.string("ownerId"),
                deletedAt = b.nullableLong("deletedAt"))
        }
        val notes = List(noteArray.length()) { index ->
            val n = noteArray.getJSONObject(index)
            val payloadVersion = n.nullableLong("drawingPayloadVersion")
            require(payloadVersion == null || payloadVersion in 1..Int.MAX_VALUE.toLong())
            NoteEntity(id = n.string("id"), bucketId = n.string("bucketId"),
                text = n.string("text"), authorId = n.string("authorId"),
                drawingPayloadVersion = payloadVersion?.toInt(),
                drawingPayload = n.nullableString("drawingPayload"),
                createdAt = n.long("createdAt"), updatedAt = n.long("updatedAt"),
                deletedAt = n.nullableLong("deletedAt"))
        }
        val designs = designObject.keys().asSequence().associateWith { designObject.string(it) }
        return EimerBackupSnapshot(buckets, notes, designs).also(::validate)
    }

    internal fun validate(snapshot: EimerBackupSnapshot) {
        require(snapshot.buckets.size <= MAX_ROWS && snapshot.notes.size <= MAX_ROWS &&
            snapshot.designs.size <= MAX_ROWS) { "Zu viele Einträge in der Sicherung." }
        val bucketIds = snapshot.buckets.map { it.id }.toSet()
        require(bucketIds.size == snapshot.buckets.size) { "Doppelte Eimer-ID." }
        require(snapshot.notes.map { it.id }.toSet().size == snapshot.notes.size) { "Doppelte Zettel-ID." }
        fun validId(id: String) = id.isNotBlank() && id.length <= 512
        snapshot.buckets.forEach { b ->
            require(validId(b.id) && validId(b.ownerId) && b.name.isNotBlank() &&
                b.name.length <= 1024 && b.material.isNotBlank() && b.material.length <= 512) {
                "Ungültige Eimerdaten."
            }
        }
        snapshot.notes.forEach { n ->
            require(validId(n.id) && validId(n.authorId) && n.bucketId in bucketIds &&
                n.text.length <= 65_536 && (n.drawingPayload?.length ?: 0) <= 1_048_576 &&
                (n.drawingPayloadVersion == null || n.drawingPayloadVersion > 0)) {
                "Ungültige Zetteldaten oder fehlender Eimer."
            }
        }
        require(snapshot.designs.all { (id, design) -> id in bucketIds && validId(design) }) {
            "Ungültige Designzuordnung."
        }
    }

    private fun JSONObject.string(key: String): String =
        get(key) as? String ?: throw IllegalArgumentException("Ungültiger Textwert: $key")
    private fun JSONObject.long(key: String): Long {
        val number = get(key) as? Number ?: throw IllegalArgumentException("Ungültige Zahl: $key")
        val value = number.toLong()
        require(number.toDouble() == value.toDouble() && value >= 0) { "Ungültige Zahl: $key" }
        return value
    }
    private fun JSONObject.nullableLong(key: String): Long? =
        if (get(key) == JSONObject.NULL) null else long(key)
    private fun JSONObject.nullableString(key: String): String? =
        if (get(key) == JSONObject.NULL) null else string(key)
}

/** All Room changes are transactional; importing the same file twice is harmless.
 * Existing rows always win, even when their deletion timestamp differs.
 */
class EimerBackupStore(private val database: EimerDatabase) {
    private val dao = database.dao()

    suspend fun snapshot(): EimerBackupSnapshot = database.withTransaction {
        EimerBackupSnapshot(dao.snapshotBuckets(), dao.snapshotNotes())
    }

    suspend fun merge(snapshot: EimerBackupSnapshot): EimerBackupMergeResult {
        EimerBackupCodec.validate(snapshot)
        return database.withTransaction {
            val existingBuckets = dao.snapshotBuckets().associateBy { it.id }
            val existingNotes = dao.snapshotNotes().associateBy { it.id }
            var bucketsAdded = 0
            var notesAdded = 0
            var conflicts = 0
            snapshot.buckets.forEach { b ->
                val current = existingBuckets[b.id]
                if (current == null) {
                    check(dao.importBucket(b) != -1L) { "Eimer konnte nicht importiert werden." }
                    bucketsAdded++
                } else if (current != b) conflicts++
            }
            snapshot.notes.forEach { n ->
                val current = existingNotes[n.id]
                if (current == null) {
                    check(dao.addNote(n) != -1L) { "Zettel konnte nicht importiert werden." }
                    notesAdded++
                } else if (current != n) conflicts++
            }
            EimerBackupMergeResult(bucketsAdded, notesAdded, conflicts)
        }
    }
}
