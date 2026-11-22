package de.zimt.eimerliste.domain

import kotlin.math.roundToInt

data class InkPoint(val x: Float, val y: Float)
data class InkStroke(val argb: Int, val widthDp: Float, val points: List<InkPoint>)

/**
 * Stable drawing payload V1: ZIMT-SKETCH/1 followed by ARGB,width:x,y|x,y.
 * Coordinates are integer units in [0,10000], not physical pixels.
 * Does not change the Room v1 schema. Invalid or unknown versions fail closed.
 */
object PaperDrawingCodec {
    const val HEADER = "ZIMT-SKETCH/1"
    const val MAX_STROKES = 120
    const val MAX_POINTS_PER_STROKE = 512
    const val MAX_TOTAL_POINTS = 16000
    const val MAX_BYTES = 240000

    fun encode(strokes: List<InkStroke>): String? {
        if (strokes.isEmpty()) return null
        if (strokes.size > MAX_STROKES || strokes.sumOf { it.points.size } > MAX_TOTAL_POINTS) return null
        val out = StringBuilder(HEADER).append('\n')
        strokes.forEach { stroke ->
            if (stroke.points.isEmpty() || stroke.points.size > MAX_POINTS_PER_STROKE) return null
            if (stroke.widthDp !in 1f..24f) return null
            out.append(stroke.argb.toUInt().toString(16).padStart(8,'0')).append(',')
                .append(stroke.widthDp.toInt()).append(':')
            stroke.points.forEachIndexed { i,p ->
                if (!p.x.isFinite() || !p.y.isFinite()) return null
                if (i > 0) out.append('|')
                out.append((p.x.coerceIn(0f,1f)*10000f).roundToInt())
                    .append(',').append((p.y.coerceIn(0f,1f)*10000f).roundToInt())
            }
            out.append('\n')
        }
        return out.toString().takeIf { it.length <= MAX_BYTES }
    }

    fun decode(raw: String?): List<InkStroke> {
        if (raw.isNullOrBlank() || raw.length > MAX_BYTES || !raw.startsWith("$HEADER\n")) return emptyList()
        val lines = raw.split('\n')
        if (lines.size > MAX_STROKES + 2) return emptyList()
        val result = ArrayList<InkStroke>()
        var pointCount = 0
        for (line in lines.drop(1)) {
            if (line.isBlank()) continue
            val colon = line.indexOf(':')
            if (colon < 0) return emptyList()
            val meta = line.substring(0, colon).split(',')
            if (meta.size != 2) return emptyList()
            val color = meta[0].toLongOrNull(16)?.takeIf { it in 0..0xffffffffL }?.toInt()
                ?: return emptyList()
            val width = meta[1].toFloatOrNull()?.takeIf { it.isFinite() && it in 1f..24f }
                ?: return emptyList()
            val rawPoints = line.substring(colon + 1).split('|')
            if (rawPoints.isEmpty() || rawPoints.size > MAX_POINTS_PER_STROKE) return emptyList()
            pointCount += rawPoints.size
            if (pointCount > MAX_TOTAL_POINTS) return emptyList()
            val points = ArrayList<InkPoint>(rawPoints.size)
            for (str in rawPoints) {
                val xy = str.split(',')
                if (xy.size != 2) return emptyList()
                val x = xy[0].toIntOrNull()?.takeIf { it in 0..10000 } ?: return emptyList()
                val y = xy[1].toIntOrNull()?.takeIf { it in 0..10000 } ?: return emptyList()
                points.add(InkPoint(x/10000f,y/10000f))
            }
            result.add(InkStroke(color,width,points))
        }
        return result
    }

    fun hasInk(raw: String?): Boolean = decode(raw).isNotEmpty()
}
