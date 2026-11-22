package de.zimt.eimerliste.domain

object NoteRules {
    const val MAX_NOTE_LENGTH = 600

    fun normalize(text: String): String = text.trim()

    fun canSubmit(text: String): Boolean {
        val clean = normalize(text)
        return clean.isNotEmpty() && clean.length <= MAX_NOTE_LENGTH
    }

    fun validBucketName(text: String): String? =
        text.trim().takeIf { it.isNotEmpty() && it.length <= 40 }
}

/** Stable material keys live in persistence. Never localize or rename them in storage. */
enum class BucketMaterial(val key: String, val title: String) {
    ZINC("zinc", "Zink"),
    WICKER("wicker", "Geflecht"),
    PAPER("paper", "Papier"),
    WOOD("wood", "Holz"),
    ENAMEL("enamel", "Emaille"),
    BRASS("brass", "Messing");

    companion object {
        fun fromKey(key: String): BucketMaterial =
            entries.firstOrNull { it.key == key } ?: ZINC
    }
}
