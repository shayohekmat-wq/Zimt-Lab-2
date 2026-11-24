package de.zimt.eimerliste.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import android.animation.ValueAnimator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Icon
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
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
import kotlinx.coroutines.launch

private val sketchColors = listOf(
    0xff35271e.toInt(), // existing warm black ink
    0xff75283F.toInt(), // burgundy / wine red
    0xff285A88.toInt(), // deep blue
    0xffA28CC6.toInt(), // lilac
    0xff285A42.toInt()  // dark forest green
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
                val down = awaitFirstDown(requireUnconsumed = true)
                val canvasW = size.width.toFloat().coerceAtLeast(1f)
                val canvasH = size.height.toFloat().coerceAtLeast(1f)
                fun point(at: Offset) = InkPoint(
                    (at.x / canvasW).coerceIn(0f, 1f),
                    (at.y / canvasH).coerceIn(0f, 1f)
                )
                val pts = arrayListOf(point(down.position))
                latestActiveCallback(InkStroke(color, widthDp, pts.toList()))
                var completed = false
                try {
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (event.changes.size != 1 || change.isConsumed) break
                        if (!change.pressed) {
                            completed = change.previousPressed
                            break
                        }
                        val p = point(change.position)
                        if (pts.size < PaperDrawingCodec.MAX_POINTS_PER_STROKE &&
                            (p.x != pts.last().x || p.y != pts.last().y)) {
                            pts.add(p)
                            latestActiveCallback(InkStroke(color, widthDp, pts.toList()))
                        }
                        change.consume()
                    }
                    if (completed && latestStrokes.size < PaperDrawingCodec.MAX_STROKES) {
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
    text: String,
    onTextEdit: (String?) -> Unit,
    onApply: (String?) -> Unit,
    onFold: (String?) -> Unit
) {
    // Persist the in-progress sketch across activity recreation, e.g. rotation.
    var workingPayload by rememberSaveable(initialPayload) { mutableStateOf(initialPayload) }
    val strokes = remember(workingPayload) { PaperDrawingCodec.decode(workingPayload) }
    var redo by remember(initialPayload) { mutableStateOf(emptyList<InkStroke>()) }
    var active by remember { mutableStateOf<InkStroke?>(null) }
    var color by remember { mutableIntStateOf(sketchColors[0]) }
    var widthDp by remember { mutableFloatStateOf(3f) }
    var status by remember { mutableStateOf<String?>(null) }
    val paperZoom = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    var leaving by remember { mutableStateOf(false) }
    var returnToText by remember { mutableStateOf(false) }
    var paperHeightPx by remember { mutableFloatStateOf(1f) }
    var paperTopPx by remember { mutableFloatStateOf(0f) }
    var editorHeightPx by remember { mutableFloatStateOf(1f) }
    val density = LocalDensity.current

    // The enlarged sheet emerges from the small paper composer.
    LaunchedEffect(Unit) {
        paperZoom.animateTo(1f, tween(460, easing = FastOutSlowInEasing))
        haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
    }

    fun leaveAndKeepSketch(toText: Boolean = false, onFinished: () -> Unit) {
        if (leaving) return
        leaving = true
        returnToText = toText
        scope.launch {
            paperZoom.animateTo(0f, tween(if (toText) 490 else 330, easing = FastOutSlowInEasing))
            haptics.performHapticFeedback(HapticFeedbackType.GestureEnd)
            onFinished()
        }
    }

    // Back also animates and preserves the sketch. No cancellation loses strokes.
    Dialog(
        onDismissRequest = { leaveAndKeepSketch { onApply(workingPayload) } },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            Modifier.fillMaxWidth(.95f).fillMaxHeight(.88f)
                .onGloballyPositioned { editorHeightPx = it.size.height.toFloat() }
                .graphicsLayer { alpha = 1f }
                .background(Ink.Walnut, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Text(
                "Dein Zeichenpapier",
                color = Ink.Paper, fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold, fontSize = 25.sp
            )
            Spacer(Modifier.height(8.dp))
            PaperSheet(Modifier.fillMaxWidth().weight(1f)
                .onGloballyPositioned {
                    paperHeightPx = it.size.height.toFloat()
                    paperTopPx = it.positionInParent().y
                }
                .graphicsLayer {
                    transformOrigin = TransformOrigin(0.5f, 0f)
                    val t = if (ValueAnimator.areAnimatorsEnabled()) paperZoom.value else 1f
                    if (returnToText) {
                        // The destination is the real-size composer near the bottom,
                        // not a collapsed strip stuck at the top of this dialog.
                        val targetHeight = with(density) { 95.dp.toPx() }
                        val targetScale = (targetHeight / paperHeightPx).coerceIn(.08f, 1f)
                        scaleY = targetScale + (1f - targetScale) * t
                        translationY = (editorHeightPx - paperTopPx - targetHeight -
                            with(density) { 14.dp.toPx() }).coerceAtLeast(0f) * (1f - t)
                    } else {
                        scaleY = if (ValueAnimator.areAnimatorsEnabled()) 0.02f + .98f * t else 1f
                    }
                }) {
                // Text and ink are parts of the SAME sheet, not separate modes.
                // Draw over the entire sheet including its visible edges; no hidden 16dp margins.
                if (text.isNotBlank()) {
                    Text(
                        text = text,
                        color = Ink.InkBlack,
                        fontFamily = FontFamily.Serif,
                        fontSize = 17.sp,
                        lineHeight = 23.sp,
                        maxLines = 12,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.align(Alignment.TopStart)
                            .fillMaxWidth()
                            .padding(start = 17.dp, end = 12.dp, top = 32.dp)
                    )
                }
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
                    modifier = Modifier.fillMaxSize()
                )
            }
            // Keep text-mode access permanently visible, even on narrow Android screens.
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
            Row(
                Modifier.weight(1f).horizontalScroll(rememberScrollState()),
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
                Spacer(Modifier.width(6.dp))
                listOf(3f, 9f).forEach { brushWidth ->
                    val selected = widthDp == brushWidth
                    Box(
                        modifier = Modifier.size(38.dp)
                            .border(if (selected) 2.dp else 1.dp,
                                if (selected) Ink.Brass else Ink.Muted, CircleShape)
                            .semantics { contentDescription = if (brushWidth < 6f) "Feiner Stift" else "Weicher Pinsel" }
                            .clickable { widthDp = brushWidth },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(Modifier.size(if (brushWidth < 6f) 5.dp else 15.dp)
                            .background(Ink.Paper, CircleShape))
                    }
                }
            }
                Spacer(Modifier.width(8.dp))
                // Same warm paper circle as the pencil button in the text editor.
                // Keep this beside the ink and width controls, not on a separate toolbar row.
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape)
                        .background(Ink.PaperEdge)
                        .semantics { contentDescription = "Zur Textbearbeitung wechseln und Skizze behalten" }
                        .clickable(enabled = !leaving) {
                            leaveAndKeepSketch(toText = true) { onTextEdit(workingPayload) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.TextFields, contentDescription = null,
                        tint = Ink.InkBlack, modifier = Modifier.size(25.dp))
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (strokes.isNotEmpty()) {
                            redo = redo + strokes.last()
                            workingPayload = PaperDrawingCodec.encode(strokes.dropLast(1))
                        }
                    },
                    enabled = strokes.isNotEmpty()
                ) { Icon(Icons.Default.Undo, contentDescription = "Rückgängig", tint = Ink.Paper) }
                IconButton(
                    onClick = {
                        if (redo.isNotEmpty()) {
                            workingPayload = PaperDrawingCodec.encode(strokes + redo.last())
                            redo = redo.dropLast(1)
                        }
                    },
                    enabled = redo.isNotEmpty()
                ) { Icon(Icons.Default.Redo, contentDescription = "Wiederholen", tint = Ink.Paper) }
            }
            if (status != null) {
                Text(status.orEmpty(), fontSize = 12.sp, color = Ink.Brass)
            }
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(4.dp))
                Button(
                    onClick = {
                        val encoded = PaperDrawingCodec.encode(strokes)
                        if (strokes.isNotEmpty() && encoded == null) {
                            status = "Die Zeichnung ist zu groß, bitte einen Strich zurücknehmen."
                        } else {
                            leaveAndKeepSketch { onFold(encoded) }
                        }
                    },
                    enabled = !leaving,
                    colors = ButtonDefaults.buttonColors(containerColor = Ink.Brass, contentColor = Ink.InkBlack)
                ) { PaperSubmitGlyph() }
            }
        }
    }
}
