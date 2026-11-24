package de.zimt.eimerliste.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

internal const val NoteFireDurationMs = 2200L

private class FlameArtwork {
    // Reusable unit-space paths; all flames are transformed copies, not
    // independently allocated particle objects or animated bitmaps.
    val tongue = Path().apply {
        moveTo(-.48f, 0f)
        cubicTo(-.65f, -.34f, -.12f, -.58f, -.16f, -1f)
        cubicTo(.18f, -.83f, .51f, -.5f, .43f, -.2f)
        cubicTo(.38f, -.04f, .19f, .04f, -.48f, 0f)
        close()
    }
    val core = Path().apply {
        moveTo(-.24f, 0f)
        cubicTo(-.32f, -.21f, .08f, -.32f, -.02f, -.67f)
        cubicTo(.25f, -.41f, .32f, -.12f, .18f, 0f)
        close()
    }
    val edge = Path()
    val outerBrush = Brush.verticalGradient(
        0f to Color(0x00FF4716), .24f to Color(0xBFFF5B13),
        .62f to Color(0xFFFF9728), 1f to Color(0xFFFFD474),
        startY = -1f, endY = 0f
    )
    val innerBrush = Brush.verticalGradient(
        0f to Color(0x00FFD35D), .45f to Color(0xFFFFD568),
        1f to Color(0xFFFFF6CF), startY = -.67f, endY = 0f
    )
    val emberGlow = Brush.radialGradient(listOf(Color(0xFFFFE6A3), Color(0xFFFF731B), Color(0x00FF4A09)),
        center = Offset.Zero, radius = 1f)
}

@Composable
internal fun NoteFire(progress: Float, warning: Boolean, modifier: Modifier = Modifier) {
    val art = remember { FlameArtwork() }
    Canvas(modifier.semantics { contentDescription = if (warning) "Warnung: Zettel beginnt zu glimmen" else "Zettel verbrennt" }) {
        if (size.width <= 0f || size.height <= 0f) return@Canvas
        val p = progress.coerceIn(0f, 1f)
        if (warning && p == 0f) {
            withTransform({
                translate(size.width * .89f, size.height * .9f)
                scale(9.dp.toPx(), 9.dp.toPx(), Offset.Zero)
            }) { drawCircle(art.emberGlow, radius = 1f, center = Offset.Zero) }
            return@Canvas
        }
        // A narrow traveling scorch line, followed by twelve curved flame tongues.
        val edgeProgress = (p / .88f).coerceIn(0f, 1f)
        val strength = (sin(PI * (p * .93f + .04f)).toFloat() * 1.4f).coerceIn(0f, 1f)
        art.edge.reset()
        for (i in 0..32) {
            val x = i / 32f
            val y = size.height * burnEdgeFraction(edgeProgress, x)
            if (i == 0) art.edge.moveTo(0f, y) else art.edge.lineTo(size.width * x, y)
        }
        if (p < .94f) {
            drawPath(art.edge, Color(0xFF392017), alpha = strength, style = Stroke(5.dp.toPx()))
            drawPath(art.edge, Color(0xFFFFA13B), alpha = strength, style = Stroke(2.dp.toPx()))
        }
        for (i in 0 until 12) {
            val f = (i + .5f) / 12f
            val beat = .68f + .32f * sin(p * 42f + i * 2.17f)
            val x = f * size.width + sin(p * 23f + i) * 3.dp.toPx()
            val y = size.height * burnEdgeFraction(edgeProgress, f)
            val w = size.width / 10f * (.75f + .25f * beat)
            val height = (16.dp.toPx() + (i % 4) * 7.dp.toPx()) * (.4f + strength * beat)
            withTransform({
                translate(x, y + 2.dp.toPx())
                rotate(sin(p * 18f + i) * 13f, Offset.Zero)
                scale(w, height, Offset.Zero)
            }) {
                drawPath(art.tongue, art.outerBrush, alpha = strength)
                drawPath(art.core, art.innerBrush, alpha = strength)
            }
        }
        // Deterministic sparks with staggered lifetimes, plus a few translucent
        // smoke/ash wisps. No blur, shaders, per-frame random or infinite loop.
        for (i in 0 until 24) {
            val phase = (p * 3.2f + i * .137f) % 1f
            val f = ((i * 37 + 13) % 97) / 97f
            val x = f * size.width + sin(phase * 4f + i) * 13.dp.toPx() * phase
            val y = size.height * burnEdgeFraction(edgeProgress, f) -
                (14f + (i % 5) * 7f).dp.toPx() * phase
            val life = (1f - phase) * strength
            val radius = (1f + (i % 3) * .65f).dp.toPx() * (1f - phase * .6f)
            drawCircle(if (i % 3 == 0) Color(0xFFFFF0A9) else Color(0xFFFF8527),
                radius, Offset(x,y), alpha = life)
            if (i % 4 == 0) drawCircle(Color(0xFF80756B), (4f + phase * 6f).dp.toPx(),
                Offset(x + phase * 10.dp.toPx(), y - 8.dp.toPx()), alpha = life * .15f)
        }
    }
}
