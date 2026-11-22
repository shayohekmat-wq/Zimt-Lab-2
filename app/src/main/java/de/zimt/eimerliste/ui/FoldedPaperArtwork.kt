package de.zimt.eimerliste.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Three distinct paper hinges: right flap → lower flap → triangular corner.
 * Projected 2.5D facets deliberately replace whole-object shrinking.
 * Rendering does not change the large, accessible drag hit-box.
 */
data class FoldPose(val right: Float, val bottom: Float, val corner: Float) {
    companion object {
        fun at(progress: Float): FoldPose = FoldPose(
            right = progress.coerceIn(0f, 1f),
            bottom = (progress - 1f).coerceIn(0f, 1f),
            corner = (progress - 2f).coerceIn(0f, 1f)
        )
    }
}

private fun polygon(a: Offset, b: Offset, c: Offset, d: Offset? = null): Path = Path().apply {
    moveTo(a.x, a.y)
    lineTo(b.x, b.y)
    lineTo(c.x, c.y)
    if (d != null) lineTo(d.x, d.y)
    close()
}

@Composable
fun FoldedPaperArtwork(
    progress: Float,
    message: String,
    drawing: String?,
    modifier: Modifier = Modifier
) {
    val pose = FoldPose.at(progress)
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val left = w * .07f
            val right = w * .93f
            val top = h * .09f
            val bottom = h * .91f
            val hingeX = (left + right) / 2f
            val hingeY = (top + bottom) / 2f
            val material = Brush.verticalGradient(
                0f to Color(0xFFFFF3D8),
                .55f to Ink.Paper,
                1f to Color(0xFFDDC39B)
            )
            val foldedSide = Brush.horizontalGradient(
                listOf(Color(0xFFE7CCA0), Color(0xFFFFEFD0), Color(0xFFD7B78C))
            )
            val line = Color(0x846D4C2C)

            // Resting left half, on which the right half is physically folded.
            if (pose.bottom == 0f && pose.corner == 0f) {
                val leftPanel = polygon(
                    Offset(left, top), Offset(hingeX, top),
                    Offset(hingeX, bottom), Offset(left, bottom)
                )
                drawPath(leftPanel, material)
                drawPath(leftPanel, Color(0x505E3E22), style = Stroke(1.dp.toPx()))

                // Cosine projection makes the right flap go edge-on halfway through.
                val angle = PI * pose.right
                val projectedX = hingeX + (right - hingeX) * cos(angle).toFloat()
                val lift = sin(angle).toFloat().coerceAtLeast(0f) * (bottom - top) * .038f
                val flap = polygon(
                    Offset(hingeX, top), Offset(projectedX, top + lift),
                    Offset(projectedX, bottom - lift), Offset(hingeX, bottom)
                )
                drawPath(flap, if (pose.right > .48f) foldedSide else material)
                drawPath(flap, Color(0x714D3621), style = Stroke(1.dp.toPx()))
                drawLine(
                    color = line.copy(alpha = .35f + pose.right * .55f),
                    start = Offset(hingeX, top),
                    end = Offset(hingeX, bottom),
                    strokeWidth = 1.4.dp.toPx()
                )
                if (pose.right > .45f) {
                    drawLine(
                        color = Color(0x8CFFFFFF),
                        start = Offset(hingeX - 3.dp.toPx(), top + 2.dp.toPx()),
                        end = Offset(hingeX - 3.dp.toPx(), bottom - 2.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            } else if (pose.corner == 0f) {
                val topPanel = polygon(
                    Offset(left, top), Offset(hingeX, top),
                    Offset(hingeX, hingeY), Offset(left, hingeY)
                )
                drawPath(topPanel, material)
                val angle = PI * pose.bottom
                val projectedY = hingeY + (bottom - hingeY) * cos(angle).toFloat()
                val lift = sin(angle).toFloat().coerceAtLeast(0f) * (hingeX - left) * .08f
                val flap = polygon(
                    Offset(left + lift, hingeY), Offset(hingeX - lift, hingeY),
                    Offset(hingeX, projectedY), Offset(left, projectedY)
                )
                drawPath(flap, if (pose.bottom > .48f) foldedSide else material)
                drawPath(flap, Color(0x714D3621), style = Stroke(1.dp.toPx()))
                drawLine(
                    line, Offset(left, hingeY), Offset(hingeX, hingeY),
                    strokeWidth = 1.6.dp.toPx()
                )
                drawLine(
                    Color(0x77FFFFFF),
                    Offset(left + 2.dp.toPx(), hingeY - 2.dp.toPx()),
                    Offset(hingeX - 2.dp.toPx(), hingeY - 2.dp.toPx()),
                    strokeWidth = 1.dp.toPx()
                )
            } else {
                // Third step: a corner folds diagonally across the already compact packet.
                val small = polygon(
                    Offset(left, top), Offset(hingeX, top),
                    Offset(hingeX, hingeY), Offset(left, hingeY)
                )
                drawPath(small, material)
                val t = pose.corner
                val diag = (hingeX - left) * .60f * t
                val triangularFlap = polygon(
                    Offset(hingeX, top),
                    Offset(hingeX - diag, top + diag * .18f),
                    Offset(hingeX - diag * .20f, hingeY - diag * .18f)
                )
                drawPath(triangularFlap, foldedSide)
                drawPath(triangularFlap, Color(0x755A4029), style = Stroke(1.dp.toPx()))
                drawLine(
                    color = Color(0xAEFFF7E7),
                    start = Offset(hingeX - diag, top + diag * .18f),
                    end = Offset(hingeX - diag * .20f, hingeY - diag * .18f),
                    strokeWidth = 1.6.dp.toPx()
                )
                if (t > .82f) {
                    drawLine(
                        color = Color(0x887C5A37),
                        start = Offset(left + 2.dp.toPx(), hingeY - 2.dp.toPx()),
                        end = Offset(hingeX - 2.dp.toPx(), hingeY - 2.dp.toPx()),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
        }

        // Sketch and text are independent ink layers: mixed notes must retain both.
        // Both layers fade together as the first flap covers the sheet.
        if (pose.right < .70f && drawing != null) {
            SketchPreview(
                raw = drawing,
                modifier = Modifier.fillMaxSize()
                    .padding(8.dp)
                    .graphicsLayer { alpha = (1f - pose.right / .70f).coerceIn(0f, 1f) }
            )
        }
        if (pose.right < .70f && message.isNotBlank()) {
            Text(
                message,
                modifier = Modifier.fillMaxSize()
                    .padding(horizontal = 17.dp, vertical = 10.dp)
                    .graphicsLayer { alpha = (1f - pose.right / .70f).coerceIn(0f, 1f) },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontFamily = FontFamily.Serif,
                fontSize = 11.sp,
                color = Ink.InkBlack
            )
        }
    }
}
