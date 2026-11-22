package de.zimt.eimerliste.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import de.zimt.eimerliste.domain.InkPoint
import de.zimt.eimerliste.domain.InkStroke
import de.zimt.eimerliste.domain.PaperDrawingCodec

private val sketchColors = listOf(
    0xff35271e.toInt(), // warm black ink
    0xff84432f.toInt(), // red-brown pencil
    0xff365965.toInt(), // blue-grey ink
    0xff64804d.toInt()  // forest green
)

private fun DrawScope.drawStroke(stroke: InkStroke) {
    val pts = stroke.points
    if (pts.isEmpty()) return
    val color = Color(stroke.argb)
    val width = stroke.widthDp.dp.toPx()
    if (pts.size == 1) {
        drawCircle(color = color, radius = width / 2f, center = Offset(pts[0].x * size.width, pts[0].y * size.height))
        return
    }
    val path = Path().apply {
        moveTo(pts.first().x * size.width, pts.first().y * size.height)
        for (p in pts.drop(1)) lineTo(p.x * size.width, p.y * size.height)
    }
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

@Composable
fun SketchPreview(raw: String?, modifier: Modifier = Modifier) {
    val ink = remember(raw) { PaperDrawingCodec.decode(raw) }
    Canvas(modifier.semantics { contentDescription = "Skizze auf dem Papier" }) {
        ink.forEach { drawStroke(it) }
    }
}

@Composable
private fun SketchCanvas(
    strokes: List<InkStroke>,
    active: InkStroke?,
    color: Int,
    widthDp: Float,
    onStroke: (InkStroke) -> Unit,
    onActive: (InkStroke?) -> Unit,
    modifier: Modifier = Modifier
) {
    val latestStrokes by rememberUpdatedState(strokes)
    val latestStrokeCallback by rememberUpdatedState(onStroke)
    val latestActiveCallback by rememberUpdatedState(onActive)
    Canvas(
        modifier.pointerInput(color, widthDp) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                val canvasW = size.width.toFloat().coerceAtLeast(1f)
                val canvasH = size.height.toFloat().coerceAtLeast(1f)
                fun point(at: Offset) = InkPoint(
                    (at.x / canvasW).coerceIn(0f, 1f),
                    (at.y / canvasH).coerceIn(0f, 1f)
                )
                val pts = arrayListOf(point(down.position))
                latestActiveCallback(InkStroke(color, widthDp, pts.toList()))
                try {
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) break
                        val p = point(change.position)
                        if (pts.size < PaperDrawingCodec.MAX_POINTS_PER_STROKE &&
                            (p.x != pts.last().x || p.y != pts.last().y)) {
                            pts.add(p)
                            latestActiveCallback(InkStroke(color, widthDp, pts.toList()))
                        }
                        change.consume()
                    }
                    if (latestStrokes.size < PaperDrawingCodec.MAX_STROKES) {
                        latestStrokeCallback(InkStroke(color, widthDp, pts.toList()))
                    }
                } finally {
                    latestActiveCallback(null)
                }
            }
        }
    ) {
        strokes.forEach { drawStroke(it) }
        active?.let { drawStroke(it) }
    }
}

@Composable
fun PaperSketchEditor(
    initialPayload: String?,
    onDismiss: () -> Unit,
    onApply: (String?) -> Unit
) {
    // Persist the in-progress sketch across activity recreation, e.g. rotation.
    var workingPayload by rememberSaveable(initialPayload) { mutableStateOf(initialPayload) }
    val strokes = remember(workingPayload) { PaperDrawingCodec.decode(workingPayload) }
    var redo by remember(initialPayload) { mutableStateOf(emptyList<InkStroke>()) }
    var active by remember { mutableStateOf<InkStroke?>(null) }
    var color by remember { mutableIntStateOf(sketchColors[0]) }
    var widthDp by remember { mutableFloatStateOf(3f) }
    var status by remember { mutableStateOf<String?>(null) }
    val inkLabel = if (widthDp < 6f) "Feiner Stift" else "Weicher Pinsel"

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            Modifier.fillMaxWidth(.95f).fillMaxHeight(.88f)
                .background(Ink.Walnut, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Text(
                "Dein Zeichenpapier",
                color = Ink.Paper, fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold, fontSize = 25.sp
            )
            Text(
                "Text und Skizze dürfen zusammen auf einem Zettel wohnen.",
                color = Ink.Muted, fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            PaperSheet(Modifier.fillMaxWidth().weight(1f)) {
                SketchCanvas(
                    strokes = strokes,
                    active = active,
                    color = color,
                    widthDp = widthDp,
                    onStroke = { stroke ->
                        val next = strokes + stroke
                        val encoded = PaperDrawingCodec.encode(next)
                        if (encoded != null) {
                            workingPayload = encoded
                            redo = emptyList()
                            status = null
                        } else {
                            status = "Das Papier ist voll. Nutze Rückgängig oder Übernehmen."
                        }
                    },
                    onActive = { active = it },
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                )
            }
            Spacer(Modifier.height(7.dp))
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sketchColors.forEachIndexed { i, paint ->
                    Box(
                        modifier = Modifier.size(38.dp)
                            .background(Color(paint), CircleShape)
                            .border(
                                if (color == paint) 3.dp else 1.dp,
                                if (color == paint) Ink.Brass else Ink.Muted,
                                CircleShape
                            )
                            .semantics { contentDescription = "Stiftfarbe ${i + 1}" }
                            .clickable { color = paint }
                    )
                }
                TextButton(onClick = { widthDp = if (widthDp == 3f) 9f else 3f }) {
                    Text(inkLabel, color = Ink.Brass, fontSize = 12.sp)
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        if (strokes.isNotEmpty()) {
                            redo = redo + strokes.last()
                            workingPayload = PaperDrawingCodec.encode(strokes.dropLast(1))
                        }
                    },
                    enabled = strokes.isNotEmpty()
                ) { Text("↶ Zurück", color = Ink.Paper) }
                TextButton(
                    onClick = {
                        if (redo.isNotEmpty()) {
                            workingPayload = PaperDrawingCodec.encode(strokes + redo.last())
                            redo = redo.dropLast(1)
                        }
                    },
                    enabled = redo.isNotEmpty()
                ) { Text("↷ Vor", color = Ink.Paper) }
            }
            if (status != null) {
                Text(status.orEmpty(), fontSize = 12.sp, color = Ink.Brass)
            }
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) { Text("Abbrechen", color = Ink.Muted) }
                Spacer(Modifier.width(4.dp))
                Button(
                    onClick = {
                        val encoded = PaperDrawingCodec.encode(strokes)
                        if (strokes.isNotEmpty() && encoded == null) {
                            status = "Die Zeichnung ist zu groß, bitte einen Strich zurücknehmen."
                        } else {
                            onApply(encoded)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Ink.Brass, contentColor = Ink.InkBlack)
                ) { Text("Übernehmen") }
            }
        }
    }
}
