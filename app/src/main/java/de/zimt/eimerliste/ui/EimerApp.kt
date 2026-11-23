package de.zimt.eimerliste.ui

import android.animation.ValueAnimator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import de.zimt.eimerliste.EimerViewModel
import de.zimt.eimerliste.data.BucketEntity
import de.zimt.eimerliste.data.NoteEntity
import de.zimt.eimerliste.domain.BucketMaterial
import de.zimt.eimerliste.domain.NoteRules
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EimerApp(vm: EimerViewModel = viewModel()) {
    EimerTheme {
        val buckets by vm.buckets.collectAsStateWithLifecycle()
        val counts by vm.counts.collectAsStateWithLifecycle()
        val selectedId by vm.selectedBucketId.collectAsStateWithLifecycle()
        val notes by vm.selectedNotes.collectAsStateWithLifecycle()

        var openBucket by rememberSaveable { mutableStateOf<String?>(null) }
        var openNote by remember { mutableStateOf<NoteEntity?>(null) }
        var createDialog by remember { mutableStateOf(false) }

        // A draft and its folded version are never deleted until the destination write succeeds.
        var draft by rememberSaveable { mutableStateOf("") }
        var drawing by rememberSaveable { mutableStateOf<String?>(null) }
        var sketchOpen by remember { mutableStateOf(false) }
        var focusTextOnReturn by remember { mutableStateOf(false) }
        var heldBucketId by remember { mutableStateOf<String?>(null) }
        var pendingPaper by rememberSaveable { mutableStateOf<String?>(null) }
        var paperReady by remember { mutableStateOf(false) }
        var saving by remember { mutableStateOf(false) }
        val fold = remember { Animatable(0f) }
        var dragOffset by remember { mutableStateOf(Offset.Zero) }
        var pointerStart by remember { mutableStateOf(Offset.Zero) }
        var paperBounds by remember { mutableStateOf<Rect?>(null) }
        val bucketBounds = remember { mutableStateMapOf<String, Rect>() }
        var hoveredBucket by remember { mutableStateOf<String?>(null) }
        var ejectingBucketId by remember { mutableStateOf<String?>(null) }
        val bucketImpactEvents = remember { mutableStateMapOf<String, Int>() }

        val scope = rememberCoroutineScope()
        val snackbar = remember { SnackbarHostState() }
        val keyboard = LocalSoftwareKeyboardController.current
        val focus = LocalFocusManager.current
        val haptics = LocalHapticFeedback.current
        val density = LocalDensity.current
        val appContext = LocalContext.current
        val designSelections = remember { mutableStateMapOf<String, String>() }
        LaunchedEffect(buckets) {
            val existingIds = buckets.mapTo(mutableSetOf()) { it.id }
            designSelections.keys.toList().filterNot { it in existingIds }
                .forEach { designSelections.remove(it) }
            buckets.forEach { bucket ->
                val savedId = BucketDesignPreferences.get(appContext, bucket.id)
                if (savedId != null && BucketArtworkCatalog.isKnownDesignId(savedId)) {
                    designSelections[bucket.id] = savedId!!
                } else {
                    // Missing or obsolete designs fall back to the original artwork.
                    designSelections.remove(bucket.id)
                }
            }
        }
        val imeVisible = WindowInsets.ime.getBottom(density) > 0

        val countsByBucket = remember(counts) { counts.associate { it.bucketId to it.noteCount } }

        // Repeatable folding if the activity is recreated halfway through an animation.
        LaunchedEffect(pendingPaper) {
            dragOffset = Offset.Zero
            hoveredBucket = null
            paperReady = false
            fold.snapTo(0f)
            if (pendingPaper != null) {
                for (step in 1..3) {
                    fold.animateTo(
                        step.toFloat(),
                        animationSpec = tween(durationMillis = 290, easing = FastOutSlowInEasing)
                    )
                    haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                }
                paperReady = true
            }
        }

        val foldPaper: () -> Unit = {
            if (pendingPaper == null && buckets.isNotEmpty() && NoteRules.canSubmit(draft, drawing)) {
                // Hide the IME before showing the movable, folded slip.
                keyboard?.hide()
                focus.clearFocus()
                pendingPaper = draft
            }
        }

        val deliver: (String) -> Unit = { targetId ->
            val textToSave = pendingPaper
            val destination = buckets.firstOrNull { it.id == targetId }
            if (textToSave != null && destination != null && paperReady && !saving) {
                saving = true
                scope.launch {
                    try {
                        vm.addNote(targetId, textToSave, drawing)
                        // A bucket reacts only to confirmed persistence, never an optimistic drop.
                        bucketImpactEvents[targetId] = (bucketImpactEvents[targetId] ?: 0) + 1
                        haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                        draft = ""
                        drawing = null
                        pendingPaper = null
                        dragOffset = Offset.Zero
                        hoveredBucket = null
                        snackbar.showSnackbar("Zettel in „" + destination.name + "“ gelandet.")
                    } catch (_: Exception) {
                        dragOffset = Offset.Zero
                        hoveredBucket = null
                        haptics.performHapticFeedback(HapticFeedbackType.Reject)
                        snackbar.showSnackbar("Nicht gespeichert. Dein gefalteter Zettel bleibt erhalten.")
                    } finally {
                        saving = false
                    }
                }
            }
        }

        ObserveBucketShake(heldBucketId) { bucketId ->
            if (pendingPaper == null) {
                heldBucketId = null
                scope.launch {
                    try {
                        val note = vm.randomNote(bucketId)
                        if (note == null) {
                            haptics.performHapticFeedback(HapticFeedbackType.Reject)
                            snackbar.showSnackbar("Dieser Eimer ist noch leer.")
                        } else {
                            ejectingBucketId = bucketId
                            haptics.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                            if (ValueAnimator.areAnimatorsEnabled()) delay(510L)
                            ejectingBucketId = null
                            haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                            openBucket = null
                            openNote = note
                        }
                    } catch (_: Exception) {
                        ejectingBucketId = null
                        snackbar.showSnackbar("Zufallszettel konnte nicht geöffnet werden.")
                    }
                }
            }
        }

        Box(Modifier.fillMaxSize().safeDrawingPadding().imePadding()) {
            WoodBackdrop(Modifier.fillMaxSize())
            Column(Modifier.fillMaxSize()) {
                if (!imeVisible) {
                    Header(onAdd = { createDialog = true }, bucketCount = buckets.size)
                } else {
                    Row(
                        Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Eimer-Liste",
                            fontFamily = FontFamily.Serif,
                            color = Ink.Paper,
                            fontSize = 19.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text("✎", color = Ink.Brass, fontSize = 22.sp)
                    }
                }
                if (buckets.isEmpty()) {
                    EmptyStage(
                        modifier = Modifier.weight(1f),
                        onCreate = { createDialog = true }
                    )
                } else {
                    Text(
                        when {
                            pendingPaper != null -> "ZIEH DEN ZETTEL IN EINEN EIMER"
                            imeVisible -> "DEINE EIMER"
                            else -> "DEINE EIMER · ANTIPPEN ZUM ÖFFNEN"
                        },
                        modifier = Modifier.padding(start = 22.dp, top = 7.dp, bottom = 4.dp),
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        color = if (pendingPaper != null) Ink.Brass else Ink.Muted
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 13.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalArrangement = Arrangement.spacedBy(13.dp)
                    ) {
                        items(buckets, key = { it.id }) { bucket ->
                            DisposableEffect(bucket.id) {
                                onDispose { bucketBounds.remove(bucket.id) }
                            }
                            BucketTile(
                                bucket = bucket,
                                design = findLabDesign(designSelections[bucket.id]),
                                layeredStudy = BucketArtworkCatalog.studyMaterial(designSelections[bucket.id]),
                                isWickerReference = designSelections[bucket.id] == WICKER_REFERENCE_ID,
                                count = countsByBucket[bucket.id] ?: 0,
                                highlighted = hoveredBucket == bucket.id ||
                                    heldBucketId == bucket.id || ejectingBucketId == bucket.id,
                                isHeld = heldBucketId == bucket.id,
                                isEjecting = ejectingBucketId == bucket.id,
                                impactToken = bucketImpactEvents[bucket.id] ?: 0,
                                onBoundsChanged = { rect -> bucketBounds[bucket.id] = rect },
                                onHold = {
                                    if (pendingPaper == null) {
                                        heldBucketId = bucket.id
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        scope.launch { snackbar.showSnackbar("Eimer halten und zweimal schütteln …") }
                                    }
                                },
                                onRelease = {
                                    if (heldBucketId == bucket.id) heldBucketId = null
                                },
                                paperPending = pendingPaper != null,
                                onOpen = {
                                    if (pendingPaper != null) {
                                        deliver(bucket.id)
                                    } else {
                                        vm.selectBucket(bucket.id)
                                        openBucket = bucket.id
                                    }
                                }
                            )
                        }
                        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                            DesignLab(buckets = buckets, onApplied = { bucketId, designId ->
                                BucketDesignPreferences.set(appContext, bucketId, designId)
                                designSelections[bucketId] = designId
                                haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                            }, onReset = { bucketId ->
                                BucketDesignPreferences.clear(appContext, bucketId)
                                designSelections.remove(bucketId)
                                haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                            })
                        }
                    }
                }
                WoodenShelf()
                if (pendingPaper == null) {
                    PaperComposer(
                        draft = draft,
                        drawing = drawing,
                        focusTextOnReturn = focusTextOnReturn,
                        onTextFocusHandled = { focusTextOnReturn = false },
                        onEditDrawing = {
                            keyboard?.hide()
                            focus.clearFocus()
                            sketchOpen = true
                        },
                        onDraftChange = { draft = it.take(NoteRules.MAX_NOTE_LENGTH) },
                        onFold = foldPaper,
                        enabled = buckets.isNotEmpty() && !saving,
                        compact = imeVisible,
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 13.dp, vertical = if (imeVisible) 7.dp else 13.dp)
                    )
                } else {
                    PaperDropTray(
                        message = pendingPaper.orEmpty(),
                        drawing = drawing,
                        progress = fold.value,
                        ready = paperReady && !saving,
                        dragOffset = dragOffset,
                        onPaperBounds = { paperBounds = it },
                        onDragStart = { point ->
                            pointerStart = (paperBounds?.topLeft ?: Offset.Zero) + point
                            dragOffset = Offset.Zero
                            hoveredBucket = null
                        },
                        onDrag = { delta ->
                            dragOffset += delta
                            val pointer = pointerStart + dragOffset
                            val newlyHovered = bucketBounds.entries.firstOrNull { (_, rect) ->
                                rect.contains(pointer)
                            }?.key
                            if (newlyHovered != hoveredBucket && newlyHovered != null) {
                                haptics.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                            }
                            hoveredBucket = newlyHovered
                        },
                        onDragEnd = {
                            val pointer = pointerStart + dragOffset
                            val hit = bucketBounds.entries.firstOrNull { (_, rect) ->
                                rect.contains(pointer)
                            }?.key
                            if (hit != null) {
                                // Keep the paper at the bucket while the database write is pending.
                                hoveredBucket = hit
                                deliver(hit)
                            } else {
                                // Missed drops never consume a note; explain how to retry.
                                hoveredBucket = null
                                dragOffset = Offset.Zero
                                haptics.performHapticFeedback(HapticFeedbackType.Reject)
                                scope.launch {
                                    snackbar.showSnackbar(
                                        "Keinen Eimer getroffen. Dein Zettel bleibt erhalten – ziehe ihn erneut oder tippe auf einen Eimer."
                                    )
                                }
                            }
                        },
                        onDragCancel = {
                            hoveredBucket = null
                            dragOffset = Offset.Zero
                        },
                        onCancel = {
                            if (!saving) {
                                pendingPaper = null
                                hoveredBucket = null
                                dragOffset = Offset.Zero
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 12.dp)
                    )
                }
            }
            SnackbarHost(
                hostState = snackbar,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp)
            )
        }

        if (createDialog) {
            CreateBucketDialog(
                onClose = { createDialog = false },
                onCreate = { name, material ->
                    scope.launch {
                        try {
                            vm.addBucket(name, material)
                            createDialog = false
                        } catch (error: Exception) {
                            snackbar.showSnackbar(error.message ?: "Der Eimer konnte nicht angelegt werden.")
                        }
                    }
                }
            )
        }

        val visibleBucket = buckets.firstOrNull { it.id == openBucket }
        if (visibleBucket != null) {
            ModalBottomSheet(
                onDismissRequest = { openBucket = null },
                containerColor = Ink.Paper,
                contentColor = Ink.InkBlack,
                dragHandle = { BottomSheetDefaults.DragHandle(color = Ink.BrassDark) }
            ) {
                BucketContents(
                    bucket = visibleBucket,
                    notes = if (visibleBucket.id == selectedId) notes else emptyList(),
                    onNote = { openNote = it },
                    onRandom = {
                        scope.launch {
                            try {
                                val note = vm.randomNote(visibleBucket.id)
                                if (note != null) {
                                    openBucket = null
                                    openNote = note
                                } else {
                                    snackbar.showSnackbar("Dieser Eimer ist noch leer.")
                                }
                            } catch (_: Exception) {
                                snackbar.showSnackbar("Konnte keinen Zufallszettel ziehen.")
                            }
                        }
                    }
                )
            }
        }
        openNote?.let { note ->
            DialogPaper(note = note, onDismiss = { openNote = null })
        }
        if (sketchOpen) {
            PaperSketchEditor(
                initialPayload = drawing,
                text = draft,
                onTextEdit = {
                    drawing = it
                    sketchOpen = false
                    focusTextOnReturn = true
                },
                onApply = {
                    drawing = it
                    sketchOpen = false
                }
            )
        }
    }
}

@Composable
private fun Header(onAdd: () -> Unit, bucketCount: Int) {
    Column(Modifier.fillMaxWidth().padding(start = 22.dp, end = 20.dp, top = 24.dp, bottom = 9.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Eimer-Liste",
                    fontFamily = FontFamily.Serif,
                    fontSize = 32.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ink.Paper
                )
            }
            Spacer(Modifier.width(12.dp))
            Box(
                Modifier.size(48.dp).clip(CircleShape)
                    .background(Brush.verticalGradient(listOf(Ink.Brass, Ink.BrassDark)))
                    .clickable(onClick = onAdd)
                    .semantics { contentDescription = "Neuen Eimer erstellen" },
                contentAlignment = Alignment.Center
            ) {
                Text("+", fontSize = 29.sp, color = Ink.InkBlack)
            }
        }
    }
}

@Composable
private fun EmptyStage(modifier: Modifier, onCreate: () -> Unit) {
    Column(
        modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BucketArtwork(
            material = BucketMaterial.WICKER,
            title = "Dein erster Eimer",
            modifier = Modifier.size(width = 210.dp, height = 224.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Noch keine Eimer.",
            fontFamily = FontFamily.Serif,
            fontSize = 21.sp,
            color = Ink.Paper,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onCreate,
            colors = ButtonDefaults.buttonColors(containerColor = Ink.Brass, contentColor = Ink.InkBlack)
        ) { Text("Ersten Eimer erstellen") }
    }
}

@Composable
private fun BucketTile(
    bucket: BucketEntity,
    design: LabDesign?,
    layeredStudy: LayeredMaterialStudy?,
    isWickerReference: Boolean,
    count: Int,
    highlighted: Boolean,
    isHeld: Boolean,
    isEjecting: Boolean,
    impactToken: Int,
    onBoundsChanged: (Rect) -> Unit,
    onHold: () -> Unit,
    onRelease: () -> Unit,
    onOpen: () -> Unit,
    paperPending: Boolean
) {
    // Short spring after an actual database commit.
    val impact = remember { Animatable(0f) }
    val wobble = remember { Animatable(0f) }
    val ejectFlight = remember { Animatable(0f) }
    val wickerPaper = remember { Animatable(1f) }
    LaunchedEffect(impactToken, isWickerReference) {
        if (isWickerReference && impactToken > 0 && ValueAnimator.areAnimatorsEnabled()) {
            wickerPaper.snapTo(0f)
            wickerPaper.animateTo(1f, tween(480, easing = FastOutSlowInEasing))
        } else {
            wickerPaper.snapTo(1f)
        }
    }
    LaunchedEffect(impactToken) {
        if (impactToken > 0 && ValueAnimator.areAnimatorsEnabled()) {
            impact.snapTo(0f)
            impact.animateTo(1f, tween(95, easing = FastOutSlowInEasing))
            impact.animateTo(0f, spring(dampingRatio = .34f, stiffness = Spring.StiffnessMedium))
        } else {
            impact.snapTo(0f)
        }
    }
    LaunchedEffect(isHeld, isEjecting) {
        if (!ValueAnimator.areAnimatorsEnabled()) {
            wobble.snapTo(0f)
        } else if (isEjecting) {
            repeat(3) {
                wobble.animateTo(-3.2f, tween(80))
                wobble.animateTo(3.2f, tween(90))
            }
            wobble.animateTo(0f, tween(110))
        } else if (isHeld) {
            // Never run a permanent animation outside a real long-press.
            while (true) {
                wobble.animateTo(-1.7f, tween(125))
                wobble.animateTo(1.7f, tween(125))
                delay(8L)
            }
        } else {
            wobble.animateTo(0f, tween(130))
        }
    }
    LaunchedEffect(isEjecting) {
        ejectFlight.snapTo(0f)
        if (isEjecting && ValueAnimator.areAnimatorsEnabled()) {
            ejectFlight.animateTo(1f, tween(475, easing = FastOutSlowInEasing))
        }
    }
    val statusText = when {
        isEjecting -> "✦ ZETTEL KOMMT!"
        isHeld -> "✦ JETZT SCHÜTTELN"
        highlighted -> "HIER EINWERFEN"
        count == 1 -> "1 Zettel"
        else -> "$count Zettel"
    }

    Column(
        Modifier.fillMaxWidth()
            .onGloballyPositioned { onBoundsChanged(it.boundsInRoot()) }
            .semantics {
                role = Role.Button
                contentDescription = "${bucket.name}, ${if (count == 1) "1 Zettel" else "$count Zettel"}" +
                    if (isHeld) ", zum Schütteln ausgewählt" else ""
                onClick(label = if (paperPending) "Zettel einwerfen" else "Eimer öffnen") {
                    onOpen()
                    true
                }
            }
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (highlighted) Color(0x774F3D29) else Color(0x26120A07)
            )
            .border(
                if (highlighted) 2.dp else 1.dp,
                if (highlighted) Ink.Brass else Color(0x355E452C),
                RoundedCornerShape(12.dp)
            )
            .pointerInput(bucket.id) {
                detectTapGestures(
                    onTap = { onOpen() },
                    onLongPress = { onHold() },
                    onPress = {
                        tryAwaitRelease()
                        onRelease()
                    }
                )
            }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.fillMaxWidth().aspectRatio(1.08f),
            contentAlignment = Alignment.Center
        ) {
            val artworkModifier = Modifier.fillMaxSize().graphicsLayer {
                    rotationZ = wobble.value + impact.value * 1.1f
                    translationY = impact.value * 5.dp.toPx()
                    scaleX = 1f + .022f * impact.value
                    scaleY = 1f - .042f * impact.value
                    transformOrigin = TransformOrigin(.5f, .84f)
                }
            if (isWickerReference) {
                WickerReferenceBucket(
                    title = bucket.name,
                    paperProgress = if (isEjecting) 1f - ejectFlight.value else wickerPaper.value,
                    modifier = artworkModifier
                )
            } else if (layeredStudy != null) {
                LayeredBucketPrototype(
                    title = bucket.name,
                    paperProgress = 1f,
                    material = layeredStudy,
                    modifier = artworkModifier
                )
            } else if (design != null) {
                LabBucketArtwork(design, bucket.name, artworkModifier)
            } else {
                BucketArtwork(
                    material = BucketMaterial.fromKey(bucket.material),
                    title = bucket.name,
                    modifier = artworkModifier
                )
            }
            if (isEjecting && !isWickerReference) {
                FoldedPaperArtwork(
                    progress = 3f,
                    message = "",
                    drawing = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(width = 85.dp, height = 60.dp)
                        .graphicsLayer {
                            translationY = -(87.dp.toPx() * ejectFlight.value)
                            translationX = 14.dp.toPx() * ejectFlight.value
                            rotationZ = -16f * ejectFlight.value
                            scaleX = .75f + .25f * ejectFlight.value
                            scaleY = .75f + .25f * ejectFlight.value
                            alpha = 1f - .55f * ejectFlight.value
                        }
                )
            }
        }
        Text(
            statusText,
            color = if (highlighted) Ink.Brass else Ink.Muted,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
private fun PaperComposer(
    draft: String,
    drawing: String?,
    focusTextOnReturn: Boolean,
    onTextFocusHandled: () -> Unit,
    onEditDrawing: () -> Unit,
    onDraftChange: (String) -> Unit,
    onFold: () -> Unit,
    enabled: Boolean,
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(focusTextOnReturn) {
        if (focusTextOnReturn) {
            focusRequester.requestFocus()
            keyboardController?.show()
            onTextFocusHandled()
        }
    }
    Column(modifier) {
        PaperSheet(Modifier.fillMaxWidth().heightIn(min = if (compact) 81.dp else 95.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(start = 17.dp, end = 12.dp, top = 14.dp, bottom = 15.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                BasicTextField(
                    value = draft,
                    onValueChange = onDraftChange,
                    enabled = enabled,
                    singleLine = false,
                    modifier = Modifier.weight(1f).focusRequester(focusRequester)
                        .heightIn(min = 53.dp, max = if (compact) 78.dp else 134.dp),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Serif,
                        color = Ink.InkBlack,
                        fontSize = 17.sp,
                        lineHeight = 23.sp
                    ),
                    // IME Enter inserts a real newline. Folding is only an explicit button action.
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Default
                    ),
                    decorationBox = { inner ->
                        Box {
                            if (draft.isEmpty()) {
                                Text(
                                    if (drawing == null) "Schreiben …"
                                    else "Text hinzufügen …",
                                    fontFamily = FontFamily.Serif,
                                    color = Ink.BrassDark.copy(alpha = .85f),
                                    fontSize = 16.sp
                                )
                            }
                            inner()
                        }
                    }
                )
                Spacer(Modifier.width(5.dp))
                Box(
                    Modifier.size(44.dp).clip(CircleShape)
                        .background(if (drawing != null) Ink.BrassDark else Ink.PaperEdge)
                        .clickable(enabled = enabled, onClick = onEditDrawing)
                        .semantics { contentDescription = "Skizze auf dem Papier zeichnen oder bearbeiten" },
                    contentAlignment = Alignment.Center
                ) {
                    Text("✎", color = Ink.InkBlack, fontSize = 25.sp)
                }
                Spacer(Modifier.width(5.dp))
                Box(
                    Modifier.size(48.dp).clip(CircleShape)
                        .background(
                            if (enabled && NoteRules.canSubmit(draft, drawing)) Ink.Brass
                            else Color(0xFFCCBA98)
                        )
                        .clickable(enabled = enabled && NoteRules.canSubmit(draft, drawing), onClick = onFold)
                        .semantics { contentDescription = "Zettel dreimal falten" },
                    contentAlignment = Alignment.Center
                ) {
                    PaperSubmitGlyph()
                }
            }
        }
    }
}

@Composable
private fun PaperDropTray(
    message: String,
    drawing: String?,
    progress: Float,
    ready: Boolean,
    dragOffset: Offset,
    onPaperBounds: (Rect) -> Unit,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                if (ready) "ZIEH DEINEN ZETTEL IN EINEN EIMER" else "DEIN ZETTEL FALTET SICH ...",
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = Ink.Brass,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onCancel, enabled = ready) {
                Text("Zurück", color = Ink.Muted, fontSize = 12.sp)
            }
        }
        Box(Modifier.fillMaxWidth().height(107.dp), contentAlignment = Alignment.Center) {
            Box(
                Modifier.size(width = 174.dp, height = 86.dp)
                    .onGloballyPositioned { onPaperBounds(it.boundsInRoot()) }
                    .graphicsLayer {
                        translationX = dragOffset.x
                        translationY = dragOffset.y
                    }
                    .pointerInput(ready, message) {
                        if (ready) {
                            detectDragGestures(
                                onDragStart = onDragStart,
                                onDragEnd = onDragEnd,
                                onDragCancel = onDragCancel,
                                onDrag = { change, delta ->
                                    change.consume()
                                    onDrag(delta)
                                }
                            )
                        }
                    }
                    .semantics {
                        contentDescription =
                            "Gefalteter Zettel. Ziehe ihn auf einen Eimer oder tippe den Zieleimer an."
                    },
                contentAlignment = Alignment.Center
            ) {
                FoldedPaperArtwork(
                    progress = progress,
                    message = message,
                    drawing = drawing,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Text(
            if (ready) "Daneben loslassen? Kein Problem – der Zettel bleibt hier." else "Eins · zwei · drei",
            fontSize = 11.sp,
            color = Ink.Muted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CreateBucketDialog(onClose:()->Unit,onCreate:(String,BucketMaterial)->Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var material by rememberSaveable { mutableStateOf(BucketMaterial.ZINC) }
    AlertDialog(
        onDismissRequest=onClose,
        title={
            Text("Ein neuer Eimer",fontFamily=FontFamily.Serif,fontSize=26.sp,color=Ink.Paper)
        },
        text={
            Column {
                Text("Wie soll er heißen?",color=Ink.Muted)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value=name,onValueChange={ name=it.take(40) },
                    singleLine=true,
                    placeholder={Text("z. B. Unsere Filmabende")},
                    colors=OutlinedTextFieldDefaults.colors(
                        focusedTextColor=Ink.Paper,unfocusedTextColor=Ink.Paper,
                        focusedBorderColor=Ink.Brass,unfocusedBorderColor=Ink.BrassDark
                    )
                )
                Spacer(Modifier.height(17.dp))
                Text("Wähle sein Material",color=Ink.Muted)
                Spacer(Modifier.height(6.dp))
                androidx.compose.foundation.layout.FlowRow(
                    horizontalArrangement=Arrangement.spacedBy(6.dp)
                ) {
                    BucketMaterial.entries.forEach { option ->
                        FilterChip(
                            selected=material==option,
                            onClick={material=option},
                            label={Text(option.title)},
                            colors=FilterChipDefaults.filterChipColors(
                                selectedContainerColor=Ink.Brass,
                                selectedLabelColor=Ink.InkBlack
                            )
                        )
                    }
                }
            }
        },
        confirmButton={
            TextButton(
                onClick={onCreate(name,material)},
                enabled=NoteRules.validBucketName(name)!=null
            ) {Text("Eimer erstellen",color=Ink.Brass)}
        },
        dismissButton={TextButton(onClick=onClose){Text("Abbrechen",color=Ink.Muted)}},
        containerColor=Ink.Walnut
    )
}

@Composable
private fun BucketContents(
    bucket: BucketEntity,
    notes: List<NoteEntity>,
    onNote: (NoteEntity) -> Unit,
    onRandom: () -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(horizontal=20.dp).heightIn(min=260.dp,max=650.dp)) {
        Text(bucket.name,fontFamily=FontFamily.Serif,fontSize=30.sp,fontWeight=FontWeight.Bold)
        Text(
            if(notes.isEmpty()) "Noch kein Zettel drin." else
                notes.size.toString()+" gefaltete "+if(notes.size==1) "Notiz" else "Notizen",
            color=Ink.BrassDark,fontSize=12.sp
        )
        TextButton(
            onClick = onRandom,
            enabled = notes.isNotEmpty(),
            modifier = Modifier.align(Alignment.End)
        ) { Text("✦ Zufallszettel ziehen", color = Ink.BrassDark) }
        Spacer(Modifier.height(7.dp))
        if(notes.isEmpty()) {
            Text("Noch leer.", color=Ink.InkBlack, modifier=Modifier.padding(vertical=16.dp))
        } else {
            LazyColumn(
                modifier=Modifier.fillMaxWidth().heightIn(max=510.dp),
                verticalArrangement=Arrangement.spacedBy(10.dp),
                contentPadding=PaddingValues(bottom=35.dp)
            ) {
                items(notes,key={it.id}) { note ->
                    PaperSheet(
                        Modifier.fillMaxWidth().heightIn(min=68.dp)
                            .clickable { onNote(note) }
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(18.dp),
                            verticalAlignment=Alignment.CenterVertically
                        ) {
                            Text("✉",fontSize=22.sp,color=Ink.BrassDark)
                            Spacer(Modifier.width(13.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    note.text.ifBlank { "✎ Eine Skizze" },
                                    maxLines=2,
                                    overflow=TextOverflow.Ellipsis,
                                    color=Ink.InkBlack,fontSize=16.sp,fontFamily=FontFamily.Serif
                                )
                                if (note.drawingPayload != null) {
                                    Text("Mit Zeichnung", fontSize=10.sp, color=Ink.BrassDark)
                                }
                            }
                            if (note.drawingPayload != null) {
                                SketchPreview(
                                    note.drawingPayload,
                                    Modifier.size(width=62.dp,height=45.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogPaper(note:NoteEntity,onDismiss:()->Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dein Wunsch", fontFamily = FontFamily.Serif, color = Ink.InkBlack) },
        text = {
            // Reuse the same normalized ink plane as the sketch editor:
            // the text and drawing remain layered, not stacked in separate boxes.
            PaperSheet(Modifier.fillMaxWidth().height(360.dp)) {
                if (note.text.isNotBlank()) {
                    Text(
                        note.text,
                        color = Ink.InkBlack,
                        fontFamily = FontFamily.Serif,
                        fontSize = 17.sp,
                        lineHeight = 23.sp,
                        modifier = Modifier.fillMaxWidth()
                            .padding(start = 17.dp, end = 12.dp, top = 21.dp)
                    )
                }
                if (note.drawingPayload != null) {
                    SketchPreview(
                        note.drawingPayload,
                        Modifier.fillMaxSize()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Wieder falten", color = Ink.BrassDark) }
        },
        containerColor = Ink.Paper
    )
}
