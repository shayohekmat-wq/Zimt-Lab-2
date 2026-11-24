package de.zimt.eimerliste.data

import android.content.Context
import de.zimt.eimerliste.domain.PaperDrawingCodec

data class DraftSnapshot(
    val text: String = "",
    val drawing: String? = null,
    val sketchOpen: Boolean = false,
    val foldedText: String? = null,
    val noteId: String? = null
)

/** A single private snapshot, separate from persisted notes. Only completed input
 * is saved; in-progress pointer samples are deliberately not written to disk.
 */
object DraftPreferences {
    private const val PREFS = "eimer-draft-v1"

    fun load(context: Context): DraftSnapshot {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val drawing = p.getString("drawing", null)
        return DraftSnapshot(
            text = p.getString("text", "") ?: "",
            drawing = drawing?.takeIf { PaperDrawingCodec.hasInk(it) },
            sketchOpen = p.getBoolean("sketchOpen", false),
            foldedText = p.getString("foldedText", null),
            noteId = p.getString("noteId", null)
        )
    }

    // One atomic preference edit; Android persists asynchronously and flushes
    // outstanding writes during normal lifecycle transitions.
    /** Wait for outstanding asynchronous preference writes without changing the draft. */
    fun flush(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().commit()

    fun saveIfChanged(context: Context, draft: DraftSnapshot) {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (p.getString("text", "") == draft.text &&
            p.getString("drawing", null) == draft.drawing &&
            p.getBoolean("sketchOpen", false) == draft.sketchOpen &&
            p.getString("foldedText", null) == draft.foldedText &&
            p.getString("noteId", null) == draft.noteId) return
        p.edit().putString("text", draft.text).putString("drawing", draft.drawing)
            .putBoolean("sketchOpen", draft.sketchOpen).putString("foldedText", draft.foldedText)
            .putString("noteId", draft.noteId).apply()
    }
}
