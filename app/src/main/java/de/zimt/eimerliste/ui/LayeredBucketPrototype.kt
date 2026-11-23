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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Working rear → paper → front → label compositing proof.
 * Not a finished premium asset: this hand-constructed test bucket verifies
 * that paper is actually covered by the front wall, not drawn over the bucket.
 */
@Composable
fun LayeredBucketPrototype(
    title: String,
    paperProgress: Float,
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
            // REAR: inner well and back rim.
            drawPath(outer, Color(0xFF493524))
            drawOval(Color(0xFF211C19), Offset(w * .19f, h * .255f),
                Size(w * .62f, h * .105f))
            drawArc(Color(0xFFB99A67), 180f, 180f, false,
                Offset(w * .19f, h * .235f), Size(w * .62f, h * .115f),
                style = Stroke(w * .019f))
            // PAPER: moves down into the mouth, always behind the front wall.
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
                Color(0xFF665037), Color(0xFFC9A777), Color(0xFF765A3B)
            )))
            for (i in 0..7) {
                val x = w * (.27f + i * .064f)
                drawLine(Color(0x44724B2A), Offset(x, h * .42f),
                    Offset(x - w * .018f, h * .85f), w * .007f)
            }
            drawPath(front, Color(0xFF3B2B20), style = Stroke(w * .012f))
            drawArc(Color(0xFFE0C291), 2f, 176f, false,
                Offset(w * .18f, h * .275f), Size(w * .64f, h * .12f),
                style = Stroke(w * .02f))
            drawCircle(Color(0xFFE2C58D), w * .013f, Offset(w * .2f, h * .39f))
            drawCircle(Color(0xFFE2C58D), w * .013f, Offset(w * .8f, h * .39f))
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
