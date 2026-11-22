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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.zimt.eimerliste.domain.BucketMaterial

private fun tornPath(w: Float, h: Float): Path = Path().apply {
    moveTo(w * 0.015f, h * 0.025f)
    for (i in 1..16) {
        val x = w * i / 16f
        val y = if (i % 3 == 0) h * 0.005f else if (i % 3 == 1) h * 0.03f else h * 0.014f
        lineTo(x, y)
    }
    lineTo(w * 0.985f, h * 0.98f)
    for (i in 16 downTo 0) {
        val x = w * i / 16f
        val y = if (i % 3 == 0) h * 0.98f else if (i % 3 == 1) h * 0.995f else h * 0.966f
        lineTo(x, y)
    }
    close()
}

@Composable
fun PaperSheet(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(modifier = modifier.drawWithContent {
        // Same torn outline for ink, text and background; no side inset is needed.
        clipPath(tornPath(size.width, size.height)) { drawContent() }
    }) {
        Canvas(Modifier.matchParentSize()) {
            val paper = tornPath(size.width, size.height)
            drawPath(paper, color = Color(0x44000000))
            drawPath(
                paper,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFFF3D9), Ink.Paper, Color(0xFFE7D2AF))
                )
            )
            drawLine(
                color = Ink.PaperEdge.copy(alpha = 0.35f),
                start = Offset(size.width * .06f, size.height * .82f),
                end = Offset(size.width * .94f, size.height * .82f),
                strokeWidth = 1.5f
            )
        }
        content()
    }
}

@Composable
fun WoodBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier.background(Brush.verticalGradient(listOf(Ink.Night, Ink.Walnut, Ink.Night)))) {
        val w = size.width
        val h = size.height
        // Fine, deterministic wood grain: no bitmap, no animated GPU noise.
        for (i in 1..90) {
            val y = h * i / 90f
            val slightWave = ((i * 17) % 19 - 9) * 0.55f
            drawLine(
                color = if (i % 4 == 0) Color(0x1FBE9968) else Color(0x12321C13),
                start = Offset(0f, y),
                end = Offset(w, y + slightWave),
                strokeWidth = if (i % 7 == 0) 2f else 1f
            )
        }
        drawRect(
            brush = Brush.verticalGradient(listOf(Color(0x7F000000), Color.Transparent, Color(0x7F000000)))
        )
    }
}

@Composable
fun WoodenShelf(modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxWidth().height(19.dp)) {
        drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF94704A), Color(0xFF55341F), Color(0xFF2B170F))))
        drawLine(Ink.BrassDark.copy(alpha = .5f), Offset.Zero, Offset(size.width, 0f), strokeWidth = 2f)
    }
}

@Composable
fun BucketArtwork(
    material: BucketMaterial,
    title: String,
    modifier: Modifier = Modifier
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val outer = Path().apply {
                moveTo(.18f * w, .30f * h)
                lineTo(.82f * w, .30f * h)
                lineTo(.72f * w, .83f * h)
                quadraticBezierTo(.50f * w, .92f * h, .28f * w, .83f * h)
                close()
            }
            val colors = when(material) {
                BucketMaterial.ZINC -> listOf(Color(0xFF667477), Color(0xFFC1C8C4), Color(0xFF626C6E))
                BucketMaterial.WICKER -> listOf(Color(0xFF71502C), Color(0xFFBD9762), Color(0xFF77502C))
                BucketMaterial.PAPER -> listOf(Color(0xFF9B7350), Color(0xFFE0BC8A), Color(0xFF967049))
                BucketMaterial.WOOD -> listOf(Color(0xFF634023), Color(0xFFBD8951), Color(0xFF65401F))
                BucketMaterial.ENAMEL -> listOf(Color(0xFFBFB4A0), Color(0xFFF3E5CD), Color(0xFFA99C86))
                BucketMaterial.BRASS -> listOf(Color(0xFF775126), Color(0xFFE0B565), Color(0xFF8F642E))
            }
            // Small contact shadow grounds the 3D object without a bitmap asset.
            drawOval(
                brush = Brush.horizontalGradient(
                    listOf(Color.Transparent, Color(0x66000000), Color.Transparent)
                ),
                topLeft = Offset(.19f * w, .845f * h),
                size = Size(.62f * w, .09f * h)
            )
            // A handle behind the rim, giving the object its silhouette.
            drawArc(
                color = if (material == BucketMaterial.ZINC) Color(0xFFD5D2C2) else Ink.BrassDark,
                startAngle = 178f,
                sweepAngle = 184f,
                useCenter = false,
                topLeft = Offset(.13f * w, .11f * h),
                size = Size(.74f * w, .51f * h),
                style = Stroke(width = w * .018f)
            )
            drawPath(outer, brush = Brush.horizontalGradient(colors))
            clipPath(outer) {
                when(material) {
                    BucketMaterial.WICKER -> {
                        // Original, resolution-independent woven willow. Alternating light/dark
                        // strokes create over-under weaving without licensed photo assets.
                        for (i in 0..13) {
                            val y = h * (.315f + .042f * i)
                            drawLine(Color(0xBB4E311D), Offset(.14f*w, y + w*.013f),
                                Offset(.86f*w, y + w*.013f), w*.019f)
                            drawLine(Color(0xAEE5BA7C), Offset(.14f*w, y),
                                Offset(.86f*w, y), w*.017f)
                        }
                        for (j in 0..13) {
                            val x = w * (.18f + j*.050f)
                            drawLine(Color(0xAD51301A), Offset(x,.30f*h),
                                Offset(x-.025f*w,.9f*h), w*.018f)
                            for (i in 0..6) {
                                val y = h*(.33f+i*.084f+(j%2)*.042f)
                                drawLine(Color(0xAADDAC6F),Offset(x,y),
                                    Offset(x-.006f*w,y+h*.029f),w*.014f)
                            }
                        }
                    }
                    BucketMaterial.WOOD -> {
                        for (i in 0..6) {
                            val x = w*(.22f + i*.095f)
                            drawLine(Color(0x70552C15),Offset(x,.28f*h),Offset(x-.02f*w,.86f*h),w*.007f)
                        }
                    }
                    BucketMaterial.PAPER -> {
                        // Cardboard fold lines with gentle wear, rather than a flat brown fill.
                        drawLine(Color(0x55FFF3D2), Offset(.42f*w,.3f*h),Offset(.5f*w,.84f*h),w*.025f)
                        drawLine(Color(0x684B321E), Offset(.28f*w,.32f*h),Offset(.39f*w,.82f*h),w*.006f)
                        drawLine(Color(0x66F8DBB5), Offset(.31f*w,.33f*h),Offset(.42f*w,.82f*h),w*.009f)
                    }
                    else -> {
                        drawLine(Color.White.copy(alpha = .18f),Offset(.27f*w,.33f*h),Offset(.32f*w,.83f*h),w*.022f)
                        drawLine(Color.Black.copy(alpha = .13f),Offset(.75f*w,.33f*h),Offset(.69f*w,.83f*h),w*.025f)
                        // Slight vertical rolling and uneven hand-worn patina.
                        for (i in 0..10) {
                            val x = w*(.21f+i*.05f)
                            drawLine(
                                if(i%3==0) Color(0x22FFFFFF) else Color(0x16000000),
                                Offset(x,.35f*h),Offset(x-.025f*w,.82f*h),w*.0025f
                            )
                        }
                    }
                }
            }
            drawPath(outer, color = Color(0xAA33291F), style = Stroke(w * .015f))
            drawOval(
                brush = Brush.verticalGradient(listOf(colors[1],colors[0])),
                topLeft = Offset(.155f*w,.25f*h),
                size = Size(.69f*w,.13f*h)
            )
            drawOval(
                color = Color(0xFF3F3025),
                topLeft = Offset(.185f*w,.282f*h),
                size = Size(.63f*w,.073f*h)
            )
            drawArc(
                color = colors[1], startAngle = 0f, sweepAngle = 180f, useCenter = false,
                topLeft = Offset(.155f*w,.25f*h),
                size = Size(.69f*w,.13f*h),
                style = Stroke(w*.015f)
            )
            drawCircle(Ink.BrassDark, radius = w*.017f, center=Offset(.18f*w,.34f*h))
            drawCircle(Ink.BrassDark, radius = w*.017f, center=Offset(.82f*w,.34f*h))
        }
        // Sturdy, readable paper label — never render text as tiny texture.
        Box(
            Modifier.fillMaxWidth(.57f)
                .offset(y = 20.dp)
                .shadow(3.dp, RoundedCornerShape(4.dp))
                .background(
                    Brush.verticalGradient(listOf(Color(0xFFFFF1D3), Ink.Paper, Color(0xFFE7D0A7))),
                    RoundedCornerShape(4.dp)
                )
                .border(1.dp, Ink.PaperEdge, RoundedCornerShape(4.dp))
                .padding(horizontal=5.dp, vertical=7.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text=title,
                fontFamily=FontFamily.Serif,
                fontWeight=FontWeight.Bold,
                color=Ink.InkBlack,
                fontSize=14.sp,
                maxLines=2,
                overflow=TextOverflow.Ellipsis,
                textAlign=TextAlign.Center,
                lineHeight=17.sp
            )
        }
    }
}
