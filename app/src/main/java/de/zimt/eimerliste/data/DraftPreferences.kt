package de.zimt.eimerliste.data

import android.content.Context
import android.util.Base64
import de.zimt.eimerliste.domain.PaperDrawingCodec

data class DraftSnapshot(
    val text: String = "",
    val drawing: String? = null,
    val sketchOpen: Boolean = false,
    val foldedText: String? = null,
    val noteId: String? = null
)

/** Private snapshot of completed input, separate from committed Room notes. */
object DraftPreferences {
    private const val PREFS = "eimer-draft-v1"

    // Android's XML serializer may indent a value ending in a newline. Encode
    // strings without line breaks so a process restart preserves every byte.
    private fun encode(value: String?): String? =
        value?.let { Base64.encodeToString(it.toByteArray(Charsets.UTF_8), Base64.NO_WRAP) }

    private fun decode(value: String?): String? =
        value?.let { Base64.decode(it, Base64.NO_WRAP).toString(Charsets.UTF_8) }

    fun load(context: Context, namespace: String = PREFS): DraftSnapshot {
        val p = context.getSharedPreferences(namespace, Context.MODE_PRIVATE)
        if (p.getInt("format", 1) == 2) {
            return DraftSnapshot(
                text = decode(p.getString("text64", null)) ?: "",
                drawing = decode(p.getString("drawing64", null)),
                sketchOpen = p.getBoolean("sketchOpen", false),
                foldedText = decode(p.getString("foldedText64", null)),
                noteId = p.getString("noteId", null)
            )
        }
        // Read the earlier raw XML format without deleting it. Canonicalize only
        // the known sketch format so legacy indentation cannot break retry equality.
        val legacyDrawing = p.getString("drawing", null)
        val strokes = PaperDrawingCodec.decode(legacyDrawing)
        return DraftSnapshot(
            text = p.getString("text", "") ?: "",
            drawing = PaperDrawingCodec.encode(strokes) ?: legacyDrawing,
            sketchOpen = p.getBoolean("sketchOpen", false),
            foldedText = p.getString("foldedText", null),
            noteId = p.getString("noteId", null)
        )
    }

    /** Wait for queued preference writes at the explicit fold boundary. */
    fun flush(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().commit()

    fun saveIfChanged(context: Context, draft: DraftSnapshot, namespace: String = PREFS) {
        val p = context.getSharedPreferences(namespace, Context.MODE_PRIVATE)
        val text64 = encode(draft.text)
        val drawing64 = encode(draft.drawing)
        val folded64 = encode(draft.foldedText)
        if (p.getInt("format", 1) == 2 &&
            p.getString("text64", null) == text64 &&
            p.getString("drawing64", null) == drawing64 &&
            p.getBoolean("sketchOpen", false) == draft.sketchOpen &&
            p.getString("foldedText64", null) == folded64 &&
            p.getString("noteId", null) == draft.noteId) return
        // The single atomic edit is asynchronously persisted by Android.
        p.edit().putInt("format", 2).putString("text64", text64)
            .putString("drawing64", drawing64).putString("foldedText64", folded64)
            .putBoolean("sketchOpen", draft.sketchOpen).putString("noteId", draft.noteId)
            .remove("text").remove("drawing").remove("foldedText").apply()
    }
}

/** UI-thread session: animation-only recompositions must not re-encode a large sketch. */
internal class DraftWriteSession(context: Context, private val namespace: String = "eimer-draft-v1") {
    private val appContext = context.applicationContext
    private var lastWritten: DraftSnapshot? = null

    fun write(snapshot: DraftSnapshot) {
        if (lastWritten == snapshot) return
        DraftPreferences.saveIfChanged(appContext, snapshot, namespace)
        lastWritten = snapshot
    }
}
