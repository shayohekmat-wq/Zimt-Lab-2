package de.zimt.eimerliste.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

/**
 * V1 schema. Never introduce a destructive fallback. Published schema updates require
 * explicit migrations and a tested upgrade path.
 */
@Entity(tableName = "buckets")
data class BucketEntity(
    @PrimaryKey val id: String,
    val name: String,
    val material: String,
    val createdAt: Long,
    val updatedAt: Long,
    val ownerId: String = "local",
    val deletedAt: Long? = null
)

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = BucketEntity::class,
            parentColumns = ["id"],
            childColumns = ["bucketId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index("bucketId")]
)
data class NoteEntity(
    @PrimaryKey val id: String,
    val bucketId: String,
    val text: String,
    val authorId: String = "local",
    val drawingPayloadVersion: Int? = null,
    val drawingPayload: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long? = null
)

data class BucketCount(val bucketId: String, val noteCount: Int)

@Dao
interface EimerDao {
    @Query("SELECT * FROM buckets WHERE deletedAt IS NULL ORDER BY createdAt ASC")
    fun observeBuckets(): Flow<List<BucketEntity>>

    @Query("SELECT * FROM notes WHERE bucketId = :bucketId AND deletedAt IS NULL ORDER BY createdAt DESC")
    fun observeNotes(bucketId: String): Flow<List<NoteEntity>>

    // Tombstones stay recoverable after process death, rotation and snackbar expiry.
    @Query("SELECT * FROM notes WHERE bucketId = :bucketId AND deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun observeDeletedNotes(bucketId: String): Flow<List<NoteEntity>>

    @Query("SELECT bucketId, COUNT(*) AS noteCount FROM notes WHERE deletedAt IS NULL GROUP BY bucketId")
    fun observeCounts(): Flow<List<BucketCount>>

    @Query("SELECT * FROM notes WHERE bucketId = :bucketId AND deletedAt IS NULL ORDER BY RANDOM() LIMIT 1")
    suspend fun randomNote(bucketId: String): NoteEntity?

    @Insert
    suspend fun addBucket(bucket: BucketEntity)

    // Stable draft IDs make a retry after a process interruption idempotent.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNote(note: NoteEntity): Long

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun noteById(id: String): NoteEntity?

    // Backup includes inactive rows: restoring a snapshot must not resurrect a tombstone.
    @Query("SELECT * FROM buckets ORDER BY createdAt ASC")
    suspend fun snapshotBuckets(): List<BucketEntity>

    @Query("SELECT * FROM notes ORDER BY createdAt ASC")
    suspend fun snapshotNotes(): List<NoteEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun importBucket(bucket: BucketEntity): Long

    /**
     * One atomic SQL UPDATE preserves note ID, content, sketch and creation time.
     * The EXISTS guard prevents moving a note into a removed bucket.
     */
    @Query("""
        UPDATE notes SET bucketId = :destinationBucketId, updatedAt = :updatedAt
        WHERE id = :noteId AND bucketId = :sourceBucketId AND deletedAt IS NULL
          AND :sourceBucketId != :destinationBucketId
          AND EXISTS (
            SELECT 1 FROM buckets
            WHERE id = :destinationBucketId AND deletedAt IS NULL
          )
    """)
    suspend fun moveNote(
        noteId: String,
        sourceBucketId: String,
        destinationBucketId: String,
        updatedAt: Long
    ): Int

    // Edit/return in one atomic operation, preserving the existing note identity.
    @Query("""
        UPDATE notes SET bucketId = :destination, text = :text,
            drawingPayload = :drawing, drawingPayloadVersion = :drawingVersion, updatedAt = :now
        WHERE id = :id AND bucketId = :source AND deletedAt IS NULL
          AND EXISTS (SELECT 1 FROM buckets WHERE id = :destination AND deletedAt IS NULL)
    """)
    suspend fun placeEditedNote(id: String, source: String, destination: String, text: String,
                                drawing: String?, drawingVersion: Int?, now: Long): Int

    // Reversible soft deletion: the row and drawing remain intact for Undo.
    @Query("UPDATE notes SET deletedAt = :deletedAt, updatedAt = :deletedAt WHERE id = :noteId AND deletedAt IS NULL")
    suspend fun softDeleteNote(noteId: String, deletedAt: Long): Int

    // The matching deletion timestamp prevents undoing a different later operation.
    @Query("UPDATE notes SET deletedAt = NULL, updatedAt = :restoredAt WHERE id = :noteId AND deletedAt = :deletedAt")
    suspend fun restoreNote(noteId: String, deletedAt: Long, restoredAt: Long): Int

    @Query("UPDATE buckets SET name = :name, updatedAt = :updatedAt WHERE id = :id AND deletedAt IS NULL")
    suspend fun renameBucket(id: String, name: String, updatedAt: Long): Int
}

@Database(entities = [BucketEntity::class, NoteEntity::class], version = 1, exportSchema = true)
abstract class EimerDatabase : RoomDatabase() {
    abstract fun dao(): EimerDao

    companion object {
        @Volatile private var instance: EimerDatabase? = null

        fun get(context: Context): EimerDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    EimerDatabase::class.java,
                    "eimerliste.db"
                ).build().also { instance = it }
            }
    }
}
