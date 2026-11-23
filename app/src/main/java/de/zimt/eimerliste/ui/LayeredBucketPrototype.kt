package de.zimt.eimerliste.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Three distinct material studies sharing the same real paper-occlusion geometry. */
enum class LayeredMaterialStudy(val displayName: String) {
    WALNUT("Walnuss"), ZINC("Zink"), ENAMEL("Emaille")
}

/**
 * Working rear → paper → front → label compositing proof.
 * Not a finished premium asset: this hand-constructed test bucket verifies
 * that paper is actually covered by the front wall, not drawn over the bucket.
 */
@Composable
fun LayeredBucketPrototype(
    title: String,
    paperProgress: Float,
    material: LayeredMaterialStudy = LayeredMaterialStudy.WALNUT,
    modifier: Modifier = Modifier
) {
    val t = paperProgress.coerceIn(0f, 1f)
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val outer = Path().apply {
                moveTo(w * .18f, h * .29f)
                lineTo(w * .82f, h * .29f)
                lineTo(w * .73f, h * .88f)
                quadraticBezierTo(w * .5f, h * .95f, w * .27f, h * .88f)
                close()
            }
            val palette = when (material) {
                LayeredMaterialStudy.WALNUT -> listOf(
                    Color(0xFF665037), Color(0xFFC9A777), Color(0xFF765A3B),
                    Color(0xFF3B2B20), Color(0xFFE0C291)
                )
                LayeredMaterialStudy.ZINC -> listOf(
                    Color(0xFF53676C), Color(0xFFC6D1D0), Color(0xFF6D8286),
                    Color(0xFF394A4D), Color(0xFFE0E8E3)
                )
                LayeredMaterialStudy.ENAMEL -> listOf(
                    Color(0xFFAAAFA3), Color(0xFFF2EAD7), Color(0xFFC0C2B6),
                    Color(0xFF455D64), Color(0xFFFFF5E5)
                )
            }
            // REAR: inner well and back rim.
            drawPath(outer, palette[3])
            drawOval(palette[3], Offset(w * .19f, h * .255f),
                Size(w * .62f, h * .105f))
            drawArc(palette[4], 180f, 180f, false,
                Offset(w * .19f, h * .235f), Size(w * .62f, h * .115f),
                style = Stroke(w * .019f))
            // PAPER: a narrow, folded note enters the opening.
            // At full depth it is completely hidden by the opaque front wall.
            val y = h * (.14f + .43f * t)
            val paper = Path().apply {
                moveTo(w * .405f, y)
                lineTo(w * .595f, y + h * .015f)
                lineTo(w * .58f, y + h * .15f)
                lineTo(w * .42f, y + h * .14f)
                close()
            }
            drawPath(paper, Brush.verticalGradient(
                listOf(Color(0xFFFFF5D9), Color(0xFFDDC49A)),
                startY = y, endY = y + h * .15f))
            drawLine(Color(0xFF8D7357), Offset(w * .42f, y + h * .07f),
                Offset(w * .57f, y + h * .075f), w * .004f)
            // FRONT: actual opaque wall, occludes paper once it enters.
            val front = Path().apply {
                moveTo(w * .18f, h * .34f)
                quadraticBezierTo(w * .5f, h * .41f, w * .82f, h * .34f)
                lineTo(w * .73f, h * .88f)
                quadraticBezierTo(w * .5f, h * .95f, w * .27f, h * .88f)
                close()
            }
            drawPath(front, Brush.horizontalGradient(listOf(
                palette[0], palette[1], palette[2]
            )))
            if (material == LayeredMaterialStudy.WALNUT) for (i in 0..7) {
                val x = w * (.27f + i * .064f)
                drawLine(Color(0x44724B2A), Offset(x, h * .42f),
                    Offset(x - w * .018f, h * .85f), w * .007f)
            }
            if (material == LayeredMaterialStudy.ZINC) for (i in 0..9) {
                val x = w * (.24f + i * .058f)
                drawLine(Color(0x22FFFFFF), Offset(x, h * .43f),
                    Offset(x - w * .02f, h * .84f), w * .003f)
            }
            if (material == LayeredMaterialStudy.ENAMEL) {
                drawLine(Color(0xAAFFFFFF), Offset(w * .28f, h * .43f),
                    Offset(w * .26f, h * .78f), w * .02f)
                drawLine(Color(0x66455D64), Offset(w * .73f, h * .43f),
                    Offset(w * .71f, h * .81f), w * .012f)
            }
            drawPath(front, palette[3], style = Stroke(w * .012f))
            drawArc(palette[4], 2f, 176f, false,
                Offset(w * .18f, h * .275f), Size(w * .64f, h * .12f),
                style = Stroke(w * .02f))
            drawCircle(palette[4], w * .013f, Offset(w * .2f, h * .39f))
            drawCircle(palette[4], w * .013f, Offset(w * .8f, h * .39f))
        }
        // LABEL: rendered last, never occluded by paper.
        Box(Modifier.fillMaxWidth(.49f).offset(y = 26.dp)
            .background(Color(0xFFF0DEB9), RoundedCornerShape(3.dp))
            .border(1.dp, Color(0xFF856943), RoundedCornerShape(3.dp))
            .padding(horizontal = 5.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF342719), maxLines = 2, textAlign = TextAlign.Center)
        }
    }
}
