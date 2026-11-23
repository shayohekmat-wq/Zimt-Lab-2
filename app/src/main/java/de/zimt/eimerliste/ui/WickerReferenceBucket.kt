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

/**
 * First bespoke illustrated vessel: a hand-constructed, open-top wicker picnic basket.
 * Real separated rear/inner well, animated paper, opaque woven front, brass label.
 * Not an imported human-designed or AI-generated asset; original in-app vector artwork.
 */
const val WICKER_REFERENCE_ID = "original-wicker-picnic-01"

@Composable
fun WickerReferenceBucket(
    title: String,
    paperProgress: Float,
    modifier: Modifier = Modifier
) {
    val t = paperProgress.coerceIn(0f, 1f)
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val dark = Color(0xFF3C2518)
            val straw = Color(0xFFBE9051)
            val light = Color(0xFFF1C980)
            val shadow = Color(0xFF77502B)
            val front = Path().apply {
                moveTo(w * .12f, h * .43f)
                quadraticBezierTo(w * .5f, h * .50f, w * .88f, h * .43f)
                lineTo(w * .80f, h * .79f)
                quadraticBezierTo(w * .73f, h * .91f, w * .50f, h * .92f)
                quadraticBezierTo(w * .27f, h * .91f, w * .20f, h * .79f)
                close()
            }
            // Raised handle: asymmetrical, thick double arch, visible behind the basket.
            val handle = Path().apply {
                moveTo(w * .26f, h * .44f)
                cubicTo(w * .19f, h * .03f, w * .78f, h * .01f, w * .76f, h * .44f)
            }
            drawPath(handle, dark, style = Stroke(w * .075f))
            drawPath(handle, straw, style = Stroke(w * .050f))
            drawPath(handle, light.copy(alpha = .8f), style = Stroke(w * .012f))
            for (i in 0..16) {
                val a = i / 16f
                val x = w * (.26f + .5f * a)
                val y = h * (.24f - .13f * kotlin.math.sin(Math.PI.toFloat() * a))
                drawLine(shadow.copy(alpha = .48f), Offset(x, y), Offset(x + w * .012f, y + h * .035f), w * .008f)
            }
            // REAR: thick lip and deep interior.
            drawOval(dark, Offset(w * .105f, h * .365f), Size(w * .79f, h * .17f))
            drawOval(Color(0xFF21180F), Offset(w * .16f, h * .392f), Size(w * .68f, h * .10f))
            drawArc(light, 178f, 184f, false, Offset(w * .11f, h * .36f),
                Size(w * .78f, h * .18f), style = Stroke(w * .024f))
            // PAPER: travels behind the front wall, never through the basket.
            val y = h * (.18f + .43f * t)
            val note = Path().apply {
                moveTo(w * .42f, y)
                lineTo(w * .59f, y + h * .02f)
                lineTo(w * .57f, y + h * .15f)
                lineTo(w * .44f, y + h * .13f)
                close()
            }
            drawPath(note, Brush.verticalGradient(listOf(Color(0xFFFFF4D5),
                Color(0xFFD5B98A)), startY = y, endY = y + h * .15f))
            drawLine(shadow, Offset(w * .44f, y + h * .06f),
                Offset(w * .57f, y + h * .075f), w * .003f)
            // FRONT: woven convex wall with clipped crossing fibers.
            drawPath(front, Brush.horizontalGradient(listOf(shadow, light, straw, shadow)))
            clipPath(front) {
                for (i in 0..16) {
                    val yy = h * (.45f + i * .031f)
                    drawLine(if (i % 2 == 0) dark.copy(alpha = .50f) else light.copy(alpha = .75f),
                        Offset(w * .08f, yy), Offset(w * .92f, yy + h * .013f), w * .013f)
                    drawLine(shadow.copy(alpha = .5f), Offset(w * .08f, yy + h * .011f),
                        Offset(w * .92f, yy + h * .023f), w * .004f)
                }
                for (i in 0..23) {
                    val xx = w * (.14f + i * .032f)
                    val bend = if (i % 2 == 0) .012f else -.012f
                    val reed = Path().apply {
                        moveTo(xx, h * .43f)
                        cubicTo(xx + w * bend, h * .58f,
                            xx - w * bend, h * .75f, xx, h * .94f)
                    }
                    drawPath(reed, if (i % 3 == 0) light else Color(0xFF8C5C30),
                        style = Stroke(w * .012f))
                }
            }
            drawPath(front, dark, style = Stroke(w * .013f))
            // Thick braided lip, plus handle sockets.
            drawArc(shadow, 0f, 180f, false, Offset(w * .105f, h * .36f),
                Size(w * .79f, h * .17f), style = Stroke(w * .035f))
            drawArc(light, 0f, 180f, false, Offset(w * .105f, h * .36f),
                Size(w * .79f, h * .17f), style = Stroke(w * .018f))
            for (x in listOf(.24f, .76f)) {
                drawCircle(dark, w * .032f, Offset(w * x, h * .45f))
                drawCircle(Color(0xFFCA9A4B), w * .021f, Offset(w * x, h * .45f))
                drawCircle(Color(0xFFFFD993), w * .006f,
                    Offset(w * (x - .005f), h * .443f))
            }
        }
        Box(
            Modifier.fillMaxWidth(.45f).offset(y = 43.dp)
                .background(Color(0xFFB88748), RoundedCornerShape(3.dp))
                .border(2.dp, Color(0xFF51341D), RoundedCornerShape(3.dp))
                .padding(horizontal = 5.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF291C13), maxLines = 2, textAlign = TextAlign.Center)
        }
    }
}
