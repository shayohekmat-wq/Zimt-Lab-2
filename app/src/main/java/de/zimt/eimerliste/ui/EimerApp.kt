package de.zimt.eimerliste.ui

import android.animation.ValueAnimator
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.platform.LocalViewConfiguration
import kotlin.math.abs
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
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import de.zimt.eimerliste.EimerViewModel
import de.zimt.eimerliste.data.BucketEntity
import de.zimt.eimerliste.data.EimerBackupCodec
import de.zimt.eimerliste.data.DraftPreferences
import de.zimt.eimerliste.data.DraftSnapshot
import de.zimt.eimerliste.data.DraftWriteSession
import de.zimt.eimerliste.data.NoteEntity
import de.zimt.eimerliste.domain.BucketMaterial
import de.zimt.eimerliste.domain.NoteRules
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.launch
import java.util.UUID
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EimerApp(vm: EimerViewModel = viewModel()) {
    EimerTheme {
        val buckets by vm.buckets.collectAsStateWithLifecycle()
        val counts by vm.counts.collectAsStateWithLifecycle()
        val selectedId by vm.selectedBucketId.collectAsStateWithLifecycle()
        val notes by vm.selectedNotes.collectAsStateWithLifecycle()
        val deletedNotes by vm.selectedDeletedNotes.collectAsStateWithLifecycle()

        var openBucket by rememberSaveable { mutableStateOf<String?>(null) }
        var openNote by remember { mutableStateOf<NoteEntity?>(null) }
        var movingNote by remember { mutableStateOf<NoteEntity?>(null) }
        // One root owns the pointer across the content-to-overview transition.
        // A lifted note is only a visual reservation: Room keeps it in its source.
        var carriedPointer by remember { mutableStateOf<Offset?>(null) }
        var rootCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
        var burningNote by remember { mutableStateOf<NoteEntity?>(null) }
        var recoveryBucket by rememberSaveable { mutableStateOf<String?>(null) }
        var noteMoveSaving by remember { mutableStateOf(false) }
        var createDialog by remember { mutableStateOf(false) }
        var backupDialog by remember { mutableStateOf(false) }
        var backupBusy by remember { mutableStateOf(false) }

        // Committed strokes and writing also survive a full process restart.
        val appContext = LocalContext.current
        val initialDraft = remember { DraftPreferences.load(appContext) }
        var draft by rememberSaveable { mutableStateOf(initialDraft.text) }
        var drawing by rememberSaveable { mutableStateOf(initialDraft.drawing) }
        var sketchOpen by rememberSaveable { mutableStateOf(initialDraft.sketchOpen) }
        var focusTextOnReturn by remember { mutableStateOf(false) }
        var heldBucketId by remember { mutableStateOf<String?>(null) }
        var pendingPaper by rememberSaveable { mutableStateOf(initialDraft.foldedText) }
        var pendingNoteId by rememberSaveable { mutableStateOf(initialDraft.noteId) }
        val draftSnapshot = DraftSnapshot(draft, drawing, sketchOpen, pendingPaper, pendingNoteId)
        val draftWriter = remember(appContext) { DraftWriteSession(appContext) }
        SideEffect { draftWriter.write(draftSnapshot) }
        LaunchedEffect(pendingNoteId) {
            if (pendingNoteId != null) {
                // Explicit folding is a durability boundary. Flush the queued preference
                // write off the UI thread; normal typing remains non-blocking.
                withContext(Dispatchers.IO) { DraftPreferences.flush(appContext) }
            }
        }
        // A Room insert may have succeeded immediately before the previous process
        // stopped. Do not present that already-delivered draft as new work.
        LaunchedEffect(Unit) {
            val id = pendingNoteId
            val foldedText = pendingPaper
            val currentDrawing = drawing
            if (id != null && foldedText != null) {
                val saved = try {
                    vm.savedNote(id)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    // Keep the private draft intact if Room cannot be read yet.
                    null
                }
                if (saved != null && saved.text == NoteRules.normalize(foldedText) &&
                    saved.drawingPayload == currentDrawing && pendingNoteId == id &&
                    pendingPaper == foldedText && drawing == currentDrawing) {
                    draft = ""
                    drawing = null
                    pendingPaper = null
                    pendingNoteId = null
                    sketchOpen = false
                }
            }
        }
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
        val haptics = rememberEimerHapticFeedback()
        val density = LocalDensity.current
        val designSelections = remember { mutableStateMapOf<String, String>() }
        val designWriteMutex = remember { Mutex() }
        fun changeDesign(bucketId: String, designId: String?) {
            scope.launch {
                try {
                    designWriteMutex.withLock {
                        withContext(Dispatchers.IO) {
                            if (designId == null) BucketDesignPreferences.clear(appContext, bucketId)
                            else BucketDesignPreferences.set(appContext, bucketId, designId)
                        }
                        if (designId == null) designSelections.remove(bucketId)
                        else designSelections[bucketId] = designId
                    }
                    haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (error: Exception) {
                    snackbar.showSnackbar(error.message ?: "Design konnte nicht gespeichert werden.")
                }
            }
        }
        val exportDocument = rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/json")
        ) { uri ->
            if (uri != null && !backupBusy) {
                backupBusy = true
                scope.launch {
                    try {
                        val snapshot = vm.backupSnapshot()
                        withContext(Dispatchers.IO) {
                            val designs = snapshot.buckets.mapNotNull { bucket ->
                                BucketDesignPreferences.get(appContext, bucket.id)?.let { bucket.id to it }
                            }.toMap()
                            val bytes = EimerBackupCodec.encode(snapshot.copy(designs = designs))
                            val output = checkNotNull(appContext.contentResolver.openOutputStream(uri, "wt"))
                            output.use { it.write(bytes) }
                        }
                        snackbar.showSnackbar("Sicherung mit ${snapshot.notes.size} Zetteln gespeichert.")
                    } catch (error: Exception) {
                        snackbar.showSnackbar(error.message ?: "Sicherung fehlgeschlagen.")
                    } finally {
                        backupBusy = false
                    }
                }
            }
        }
        val importDocument = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null && !backupBusy) {
                backupBusy = true
                scope.launch {
                    try {
                        val snapshot = withContext(Dispatchers.IO) {
                            val input = checkNotNull(appContext.contentResolver.openInputStream(uri))
                            input.use { EimerBackupCodec.decode(it) }
                        }
                        val result = vm.restoreBackup(snapshot)
                        designWriteMutex.withLock {
                            val restoredDesigns = withContext(Dispatchers.IO) {
                                BucketDesignPreferences.restoreMissing(appContext, snapshot.designs)
                            }
                            restoredDesigns.forEach { (id, design) ->
                                if (BucketArtworkCatalog.isKnownDesignId(design)) designSelections[id] = design
                            }
                        }
                        haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                        snackbar.showSnackbar(
                            "Import: ${result.bucketsAdded} Eimer, ${result.notesAdded} Zettel. " +
                                "${result.conflicts} abweichende bestehende Einträge unverändert."
                        )
                    } catch (error: Exception) {
                        snackbar.showSnackbar(error.message ?: "Import fehlgeschlagen.")
                    } finally {
                        backupBusy = false
                    }
                }
            }
        }
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
        LaunchedEffect(openBucket) {
            openBucket?.let { if (selectedId != it) vm.selectBucket(it) }
        }

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

        fun cancelTransfer() {
            if (noteMoveSaving) return
            val source = movingNote?.bucketId
            movingNote = null
            carriedPointer = null
            hoveredBucket = null
            dragOffset = Offset.Zero
            if (source != null && buckets.any { it.id == source }) {
                vm.selectBucket(source)
                openBucket = source
            }
        }

        fun beginTransfer(note: NoteEntity, pointer: Offset?) {
            if (movingNote != null || pendingPaper != null || noteMoveSaving || burningNote != null) return
            movingNote = note
            carriedPointer = pointer
            dragOffset = Offset.Zero
            hoveredBucket = null
            heldBucketId = null
            openBucket = null
        }

        fun targetAt(point: Offset): String? = bucketBounds.entries.firstOrNull { (id, rect) ->
            id != movingNote?.bucketId && rect.contains(point)
        }?.key

        fun moveCarriedNote(targetId: String) {
            val note = movingNote ?: return
            if (noteMoveSaving || targetId == note.bucketId) return
            val target = buckets.firstOrNull { it.id == targetId } ?: return
            noteMoveSaving = true
            scope.launch {
                try {
                    vm.moveNote(note.id, note.bucketId, target.id)
                    bucketImpactEvents[target.id] = (bucketImpactEvents[target.id] ?: 0) + 1
                    haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                    movingNote = null
                    carriedPointer = null
                    hoveredBucket = null
                    dragOffset = Offset.Zero
                    scope.launch { snackbar.showSnackbar("Zettel in „" + target.name + "“ verschoben.") }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (error: Exception) {
                    carriedPointer = null
                    hoveredBucket = null
                    dragOffset = Offset.Zero
                    haptics.performHapticFeedback(HapticFeedbackType.Reject)
                    scope.launch { snackbar.showSnackbar(error.message ?: "Verschieben fehlgeschlagen. Zettel bleibt erhalten.") }
                } finally {
                    noteMoveSaving = false
                }
            }
        }

        val foldPaper: () -> Unit = {
            if (movingNote == null && pendingPaper == null && buckets.isNotEmpty() && NoteRules.canSubmit(draft, drawing)) {
                // Hide the IME before showing the movable, folded slip.
                keyboard?.hide()
                focus.clearFocus()
                pendingNoteId = UUID.randomUUID().toString()
                pendingPaper = draft
            }
        }

        val deliver: (String) -> Unit = deliver@{ targetId ->
            if (movingNote != null) {
                moveCarriedNote(targetId)
                return@deliver
            }
            val textToSave = pendingPaper
            val destination = buckets.firstOrNull { it.id == targetId }
            if (textToSave != null && destination != null && paperReady && !saving) {
                val draftId = pendingNoteId ?: UUID.randomUUID().toString().also { pendingNoteId = it }
                saving = true
                scope.launch {
                    try {
                        vm.addNote(targetId, textToSave, drawing, draftId)
                        // A bucket reacts only to confirmed persistence, never an optimistic drop.
                        bucketImpactEvents[targetId] = (bucketImpactEvents[targetId] ?: 0) + 1
                        haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                        draft = ""
                        drawing = null
                        pendingPaper = null
                        pendingNoteId = null
                        dragOffset = Offset.Zero
                        hoveredBucket = null
                        scope.launch { snackbar.showSnackbar("Zettel in „" + destination.name + "“ gelandet.") }
                    } catch (_: Exception) {
                        dragOffset = Offset.Zero
                        hoveredBucket = null
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        scope.launch { snackbar.showSnackbar("Nicht gespeichert. Dein gefalteter Zettel bleibt erhalten.") }
                    } finally {
                        saving = false
                    }
                }
            }
        }

        ObserveBucketShake(heldBucketId) { bucketId ->
            if (pendingPaper == null && movingNote == null) {
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

        val latestDeliver by rememberUpdatedState(deliver)
        val latestTargetAt by rememberUpdatedState<(Offset) -> String?>(::targetAt)
        BackHandler(enabled = movingNote != null || (openBucket != null && openNote == null && recoveryBucket == null)) {
            if (movingNote != null) cancelTransfer() else openBucket = null
        }
        Box(Modifier.fillMaxSize().safeDrawingPadding().imePadding()
            .onGloballyPositioned { rootCoordinates = it }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Main)
                    try {
                        do {
                            val event = awaitPointerEvent(PointerEventPass.Main)
                            val change = event.changes.firstOrNull { it.id == down.id }
                            if (carriedPointer != null) {
                                if (change == null || event.changes.size != 1) {
                                    carriedPointer = null
                                    hoveredBucket = null
                                    break
                                }
                                val point = rootCoordinates?.takeIf { it.isAttached }?.localToRoot(change.position)
                                if (point != null) {
                                    carriedPointer = point
                                    hoveredBucket = latestTargetAt(point)
                                    if (!change.pressed) {
                                        val hit = hoveredBucket
                                        carriedPointer = null
                                        hoveredBucket = null
                                        if (hit != null) latestDeliver(hit)
                                    }
                                }
                                change.consume()
                            }
                        } while (event.changes.any { it.pressed })
                    } finally {
                        if (carriedPointer != null) {
                            carriedPointer = null
                            hoveredBucket = null
                        }
                    }
                }
            }) {
            WoodBackdrop(Modifier.fillMaxSize())
            Column(Modifier.fillMaxSize().then(
                if (openBucket != null) Modifier.clearAndSetSemantics {} else Modifier
            )) {
                if (!imeVisible) {
                    Header(onAdd = { createDialog = true }, onBackup = { if (!backupBusy) backupDialog = true },
                        bucketCount = buckets.size)
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
                            movingNote != null -> "WÄHLE EINEN NEUEN EIMER"
                            pendingPaper != null -> "ZIEH DEN ZETTEL IN EINEN EIMER"
                            imeVisible -> "DEINE EIMER"
                            else -> "DEINE EIMER · ANTIPPEN ZUM ÖFFNEN"
                        },
                        modifier = Modifier.padding(start = 22.dp, top = 7.dp, bottom = 4.dp),
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        color = if (pendingPaper != null || movingNote != null) Ink.Brass else Ink.Muted
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
                                illustratedAsset = findIllustratedBucket(designSelections[bucket.id]),
                                humanAsset = findHumanArtistBucket(designSelections[bucket.id]),
                                count = countsByBucket[bucket.id] ?: 0,
                                highlighted = hoveredBucket == bucket.id ||
                                    heldBucketId == bucket.id || ejectingBucketId == bucket.id,
                                isHeld = heldBucketId == bucket.id,
                                isEjecting = ejectingBucketId == bucket.id,
                                impactToken = bucketImpactEvents[bucket.id] ?: 0,
                                onBoundsChanged = { rect -> bucketBounds[bucket.id] = rect },
                                onHold = {
                                    if (pendingPaper == null && movingNote == null) {
                                        heldBucketId = bucket.id
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        scope.launch { snackbar.showSnackbar("Eimer halten und zweimal schütteln …") }
                                    }
                                },
                                onRelease = {
                                    if (heldBucketId == bucket.id) heldBucketId = null
                                },
                                paperPending = pendingPaper != null || movingNote != null,
                                onOpen = {
                                    if (movingNote?.bucketId == bucket.id) {
                                        cancelTransfer()
                                    } else if (pendingPaper != null || movingNote != null) {
                                        deliver(bucket.id)
                                    } else {
                                        vm.selectBucket(bucket.id)
                                        openBucket = bucket.id
                                    }
                                }
                            )
                        }
                        if (movingNote == null && pendingPaper == null) item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                            DesignLab(buckets = buckets,
                                onApplied = { bucketId, designId -> changeDesign(bucketId, designId) },
                                onReset = { bucketId -> changeDesign(bucketId, null) })
                        }
                    }
                }
                WoodenShelf()
                if (pendingPaper == null && movingNote == null) {
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
                        message = movingNote?.text ?: pendingPaper.orEmpty(),
                        drawing = if (movingNote != null) movingNote?.drawingPayload else drawing,
                        progress = if (movingNote != null) 3f else fold.value,
                        ready = if (movingNote != null) !noteMoveSaving && carriedPointer == null else paperReady && !saving,
                        transfer = movingNote != null,
                        hidePaper = carriedPointer != null,
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
                            val newlyHovered = targetAt(pointer)
                            if (newlyHovered != hoveredBucket && newlyHovered != null) {
                                haptics.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                            }
                            hoveredBucket = newlyHovered
                        },
                        onDragEnd = {
                            val pointer = pointerStart + dragOffset
                            val hit = targetAt(pointer)
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
                            if (movingNote != null) {
                                cancelTransfer()
                            } else if (!saving) {
                                pendingPaper = null
                                pendingNoteId = null
                                hoveredBucket = null
                                dragOffset = Offset.Zero
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 12.dp)
                    )
                }
            }
            val visibleBucket = buckets.firstOrNull { it.id == openBucket }
        if (visibleBucket != null) {
            // Keep the sheet in the activity window: removing it must not break
            // the ancestor pointer stream carrying the note into the real grid.
            Box(Modifier.matchParentSize().background(Color(0x99000000))
                .clickable { openBucket = null })
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                    .heightIn(max = 690.dp),
                shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
                color = Ink.Paper,
                contentColor = Ink.InkBlack
            ) {
                Column {
                    Box(Modifier.fillMaxWidth().height(36.dp)
                        .clickable { openBucket = null }, contentAlignment = Alignment.Center) {
                        Box(Modifier.size(36.dp, 4.dp).background(Ink.BrassDark, CircleShape))
                    }
                BucketContents(
                    bucket = visibleBucket,
                    notes = if (visibleBucket.id == selectedId) notes.filter { it.bucketId == visibleBucket.id } else emptyList(),
                    canTransfer = buckets.any { it.id != visibleBucket.id } && pendingPaper == null,
                    burningNote = burningNote?.takeIf { it.bucketId == visibleBucket.id },
                    recoveryCount = deletedNotes.count { it.bucketId == visibleBucket.id },
                    onRecovery = { recoveryBucket = visibleBucket.id },
                    onNote = { openNote = it },
                    onLiftNote = { note, point -> beginTransfer(note, point) },
                    onIgnite = { note ->
                        if (burningNote == null) {
                            // Preserve a visual copy of the note while Room removes it from the
                            // active list. The durable soft-delete does not depend on the animation.
                            burningNote = note
                            scope.launch {
                            try {
                                val deletedAt = vm.burnNote(note.id)
                                if (ValueAnimator.areAnimatorsEnabled()) delay(NoteFireDurationMs)
                                burningNote = null
                                if (openBucket == note.bucketId) openBucket = null
                                haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                                val result = snackbar.showSnackbar(
                                    message = "Zettel verbrannt.",
                                    actionLabel = "Rückgängig",
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Long
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    vm.undoBurn(note.id, deletedAt)
                                    haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                                }
                            } catch (error: Exception) {
                                burningNote = null
                                snackbar.showSnackbar(error.message ?: "Verbrennen oder Wiederherstellen fehlgeschlagen.")
                            } finally {
                                if (burningNote?.id == note.id) burningNote = null
                            }
                            }
                        }
                    },
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
        }

            if (carriedPointer != null) {
                val point = carriedPointer!!
                val origin = rootCoordinates?.takeIf { it.isAttached }?.localToRoot(Offset.Zero) ?: Offset.Zero
                val halfWidth = with(density) { 77.dp.toPx() }
                val halfHeight = with(density) { 37.dp.toPx() }
                FoldedPaperArtwork(
                    progress = 3f,
                    message = movingNote?.text.orEmpty(),
                    drawing = movingNote?.drawingPayload,
                    modifier = Modifier.zIndex(4f).offset {
                        IntOffset((point.x - origin.x - halfWidth).roundToInt(),
                            (point.y - origin.y - halfHeight).roundToInt())
                    }.size(154.dp, 74.dp).graphicsLayer {
                        rotationZ = -7f
                        shadowElevation = 10.dp.toPx()
                    }
                )
            }
            SnackbarHost(
                hostState = snackbar,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp)
            )
        }

        if (backupDialog) {
            AlertDialog(
                onDismissRequest = { backupDialog = false },
                title = { Text("Sichern & wiederherstellen") },
                text = {
                    Text("Die Datei enthält Eimer, Zettel, Skizzen, verbrannte Zettel und Designauswahl. " +
                        "Beim Import werden vorhandene Einträge nicht überschrieben. " +
                        "Die Sicherung ist nicht verschlüsselt; bewahre sie privat auf.")
                },
                confirmButton = {
                    TextButton(onClick = {
                        backupDialog = false
                        exportDocument.launch("Eimer-Liste-Sicherung.json")
                    }) { Text("Exportieren") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        backupDialog = false
                        importDocument.launch(arrayOf("application/json", "text/plain", "application/octet-stream"))
                    }) { Text("Importieren") }
                }
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

        recoveryBucket?.let { bucketId ->
            val recoverable = deletedNotes.filter { it.bucketId == bucketId }
            AlertDialog(
                onDismissRequest = { recoveryBucket = null },
                title = { Text("Verbrannte Zettel") },
                text = {
                    LazyColumn(Modifier.heightIn(max = 320.dp)) {
                        if (recoverable.isEmpty()) {
                            item { Text("Keine verbrannten Zettel vorhanden.") }
                        }
                        items(recoverable, key = { it.id }) { note ->
                            TextButton(onClick = {
                                val deletedAt = note.deletedAt ?: return@TextButton
                                scope.launch {
                                    try {
                                        vm.undoBurn(note.id, deletedAt)
                                        haptics.performHapticFeedback(HapticFeedbackType.Confirm)
                                    } catch (error: Exception) {
                                        recoveryBucket = null
                                        openBucket = null
                                        snackbar.showSnackbar(error.message ?: "Wiederherstellen fehlgeschlagen.")
                                    }
                                }
                            }) {
                                Text("↶ " + note.text.ifBlank { "Skizze" }, maxLines = 2,
                                    overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { recoveryBucket = null }) { Text("Schließen") } }
            )
        }
        openNote?.let { note ->
            DialogPaper(note = note, onDismiss = { openNote = null })
        }
        if (sketchOpen) {
            PaperSketchEditor(
                drawingPayload = drawing,
                text = draft,
                onPayloadChange = { drawing = it },
                onTextEdit = {
                    drawing = it
                    sketchOpen = false
                    focusTextOnReturn = true
                },
                onApply = {
                    drawing = it
                    sketchOpen = false
                },
                onFold = { finishedDrawing ->
                    // Preserve the completed sketch before starting the real fold sequence.
                    drawing = finishedDrawing
                    sketchOpen = false
                    if (pendingPaper == null && buckets.isNotEmpty() &&
                        NoteRules.canSubmit(draft, finishedDrawing)) {
                        keyboard?.hide()
                        focus.clearFocus()
                        pendingNoteId = UUID.randomUUID().toString()
                        pendingPaper = draft
                    }
                }
            )
        }
    }
}

@Composable
private fun Header(onAdd: () -> Unit, onBackup: () -> Unit, bucketCount: Int) {
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
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier.size(44.dp).clip(CircleShape)
                    .clickable(onClick = onBackup)
                    .semantics { contentDescription = "Eimer-Liste sichern und wiederherstellen" },
                contentAlignment = Alignment.Center
            ) { Text("↕", fontSize = 24.sp, color = Ink.Brass) }
            Spacer(Modifier.width(5.dp))
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
    illustratedAsset: IllustratedBucketAsset?,
    humanAsset: HumanArtistBucket?,
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
    // Pointer handlers survive recomposition; the current callbacks must route a
    // folded slip to delivery rather than opening a stale bucket view.
    val latestOpen by rememberUpdatedState(onOpen)
    val latestHold by rememberUpdatedState(onHold)
    val latestRelease by rememberUpdatedState(onRelease)
    // Short spring after an actual database commit.
    val impact = remember { Animatable(0f) }
    val wobble = remember { Animatable(0f) }
    val ejectFlight = remember { Animatable(0f) }
    val wickerPaper = remember { Animatable(1f) }
    LaunchedEffect(impactToken, isWickerReference, illustratedAsset) {
        if ((isWickerReference || illustratedAsset != null) && impactToken > 0 && ValueAnimator.areAnimatorsEnabled()) {
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
                    onTap = { latestOpen() },
                    onLongPress = { latestHold() },
                    onPress = {
                        try { tryAwaitRelease() }
                        finally { latestRelease() }
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
            if (humanAsset != null) {
                HumanArtistBucketArtwork(humanAsset, artworkModifier)
            } else if (illustratedAsset != null) {
                LayeredIllustratedBucket(
                    asset = illustratedAsset,
                    title = bucket.name,
                    paperProgress = if (isEjecting) 1f - ejectFlight.value else wickerPaper.value,
                    modifier = artworkModifier
                )
            } else if (isWickerReference) {
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
            if (isEjecting && !isWickerReference && illustratedAsset == null) {
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
                        .semantics { contentDescription = "Notiztext" }
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
    transfer: Boolean,
    hidePaper: Boolean,
    dragOffset: Offset,
    onPaperBounds: (Rect) -> Unit,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val latestDragStart by rememberUpdatedState(onDragStart)
    val latestDrag by rememberUpdatedState(onDrag)
    val latestDragEnd by rememberUpdatedState(onDragEnd)
    val latestDragCancel by rememberUpdatedState(onDragCancel)
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                if (transfer) "ZETTEL ANGEHOBEN · NEUEN EIMER WÄHLEN"
                else if (ready) "ZIEH DEINEN ZETTEL IN EINEN EIMER" else "DEIN ZETTEL FALTET SICH ...",
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
                        alpha = if (hidePaper) 0f else 1f
                    }
                    .pointerInput(ready, message) {
                        if (ready) {
                            detectDragGestures(
                                onDragStart = { latestDragStart(it) },
                                onDragEnd = { latestDragEnd() },
                                onDragCancel = { latestDragCancel() },
                                onDrag = { change, delta ->
                                    change.consume()
                                    latestDrag(delta)
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
            if (transfer) "Ziehe den Zettel in einen Eimer oder tippe auf ihn."
            else if (ready) "Daneben loslassen? Kein Problem – der Zettel bleibt hier." else "Eins · zwei · drei",
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
    canTransfer: Boolean,
    burningNote: NoteEntity?,
    recoveryCount: Int,
    onRecovery: () -> Unit,
    onNote: (NoteEntity) -> Unit,
    onLiftNote: (NoteEntity, Offset?) -> Unit,
    onIgnite: (NoteEntity) -> Unit,
    onRandom: () -> Unit
) {
    val touchSlop = LocalViewConfiguration.current.touchSlop
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var resumed by remember(lifecycle) { mutableStateOf(lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) }
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, _ ->
            resumed = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    val latestOpenNote by rememberUpdatedState(onNote)
    val latestLiftNote by rememberUpdatedState(onLiftNote)
    val latestIgnite by rememberUpdatedState(onIgnite)
    val noteHaptics = rememberEimerHapticFeedback()
    val displayedNotes = if (burningNote != null && notes.none { it.id == burningNote.id }) {
        (notes + burningNote).sortedByDescending { it.createdAt }
    } else notes
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
        if (recoveryCount > 0) {
            TextButton(onClick = onRecovery, modifier = Modifier.align(Alignment.End)) {
                Text("↶ Verbrannte Zettel ($recoveryCount)", color = Ink.BrassDark)
            }
        }
        Spacer(Modifier.height(7.dp))
        if(displayedNotes.isEmpty()) {
            Text("Noch leer.", color=Ink.InkBlack, modifier=Modifier.padding(vertical=16.dp))
        } else {
            LazyColumn(
                modifier=Modifier.fillMaxWidth().heightIn(max=510.dp),
                verticalArrangement=Arrangement.spacedBy(10.dp),
                contentPadding=PaddingValues(bottom=35.dp)
            ) {
                items(displayedNotes,key={it.id}) { note ->
                    var visualPhase by remember(note.id) { mutableStateOf(NotePressSafety.Phase.PRESSED) }
                    val warningActive = visualPhase == NotePressSafety.Phase.WARNING
                    val lifted = visualPhase == NotePressSafety.Phase.LIFTED ||
                        warningActive || visualPhase == NotePressSafety.Phase.DRAGGING
                    val liftScale by androidx.compose.animation.core.animateFloatAsState(
                        targetValue = if (lifted) 1.045f else 1f,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "note-lift"
                    )
                    val burningActive = burningNote?.id == note.id
                    var noteCoordinates by remember(note.id) { mutableStateOf<LayoutCoordinates?>(null) }
                    val burnProgress by androidx.compose.animation.core.animateFloatAsState(
                        targetValue = if (burningActive) 1f else 0f,
                        animationSpec = tween(durationMillis = NoteFireDurationMs.toInt(), easing = androidx.compose.animation.core.LinearEasing),
                        label = "paper-burn-progress"
                    )
                    val warningAngle = remember(note.id) { Animatable(0f) }
                    LaunchedEffect(warningActive) {
                        if (warningActive && ValueAnimator.areAnimatorsEnabled()) {
                            while (true) {
                                warningAngle.animateTo(-1.1f, tween(90))
                                warningAngle.animateTo(1.1f, tween(135))
                            }
                        } else warningAngle.snapTo(0f)
                    }
                    DisposableEffect(warningActive) {
                        val stop = if (warningActive) noteHaptics.beginBurnWarning() else null
                        onDispose { stop?.invoke() }
                    }
                    Box(
                        Modifier.fillMaxWidth().heightIn(min=68.dp)
                            .graphicsLayer {
                                rotationZ = if (warningActive) warningAngle.value else 0f
                                scaleX = liftScale * (1f - burnProgress * .07f)
                                scaleY = liftScale
                                shadowElevation = if (lifted) 9.dp.toPx() else 0f
                                alpha = 1f
                            }
                            .onGloballyPositioned { noteCoordinates = it }
                            .semantics {
                                if (!burningActive) {
                                    contentDescription = "Zettel: " + note.text.ifBlank { "Skizze" }
                                    onClick(label = "Zettel öffnen") { onNote(note); true }
                                    customActions = if (canTransfer) listOf(
                                        CustomAccessibilityAction("Zettel umlegen") { onLiftNote(note, null); true }
                                    ) else emptyList()
                                }
                            }
                            .pointerInput(note.id, touchSlop, burningActive, resumed, canTransfer) {
                                if (burningActive || !resumed) return@pointerInput
                                awaitEachGesture {
                                    val down = awaitFirstDown(requireUnconsumed = true)
                                    val start = noteCoordinates?.takeIf { it.isAttached }?.localToRoot(down.position)
                                        ?: return@awaitEachGesture
                                    val startedAt = down.uptimeMillis
                                    var lastPosition = start
                                    val gesture = NotePressSafety().apply { down(startedAt) }
                                    visualPhase = gesture.phase
                                    try {
                                        while (gesture.isTracking()) {
                                            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                                                gesture.cancel()
                                                break
                                            }
                                            val elapsed = (android.os.SystemClock.uptimeMillis() - startedAt).coerceAtLeast(0L)
                                            val nextDeadline = gesture.nextDeadlineMs()
                                            val event = if (nextDeadline == null) {
                                                awaitPointerEvent(PointerEventPass.Main)
                                            } else {
                                                withTimeoutOrNull(
                                                    (nextDeadline - elapsed).coerceAtLeast(1L)
                                                ) { awaitPointerEvent(PointerEventPass.Main) }
                                            }
                                            if (event == null) {
                                                if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                                                    gesture.cancel()
                                                    break
                                                }
                                                val phase = gesture.advance(android.os.SystemClock.uptimeMillis())
                                                visualPhase = phase
                                                when (phase) {
                                                    NotePressSafety.Phase.LIFTED ->
                                                        noteHaptics.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                                                    NotePressSafety.Phase.IGNITED ->
                                                        noteHaptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    else -> Unit
                                                }
                                            } else {
                                                val pointer = event.changes.firstOrNull { it.id == down.id }
                                                if (pointer == null || event.changes.size > 1 ||
                                                    event.changes.any { it.isConsumed && it.id != down.id }) {
                                                    gesture.cancel()
                                                } else if (!pointer.pressed) {
                                                    if (!pointer.previousPressed || pointer.isConsumed) gesture.cancel()
                                                    else {
                                                        lastPosition = noteCoordinates?.takeIf { it.isAttached }
                                                            ?.localToRoot(pointer.position) ?: lastPosition
                                                        gesture.release()
                                                    }
                                                } else {
                                                    val point = noteCoordinates?.takeIf { it.isAttached }
                                                        ?.localToRoot(pointer.position)
                                                    if (point == null || pointer.isConsumed) {
                                                        gesture.cancel()
                                                        continue
                                                    }
                                                    lastPosition = point
                                                    val delta = lastPosition - start
                                                    if (delta.getDistance() > touchSlop) {
                                                        gesture.move(beyondSlop = true)
                                                        visualPhase = gesture.phase
                                                        if (gesture.phase == NotePressSafety.Phase.DRAGGING) {
                                                            pointer.consume()
                                                            if (canTransfer) {
                                                                latestLiftNote(note, lastPosition)
                                                                return@awaitEachGesture
                                                            }
                                                            gesture.cancel()
                                                        }
                                                    }
                                                    // The scrolling parent may consume a change after our Main
                                                    // pass. Never continue a stationary hold after that takeover.
                                                    if (gesture.isTracking() && gesture.phase != NotePressSafety.Phase.DRAGGING) {
                                                        val finalEvent = awaitPointerEvent(PointerEventPass.Final)
                                                        if (finalEvent.changes.any { it.isConsumed }) gesture.cancel()
                                                    }
                                                }
                                            }
                                        }
                                        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) return@awaitEachGesture
                                        when (gesture.phase) {
                                            NotePressSafety.Phase.RELEASED -> when (gesture.releasedFrom) {
                                                NotePressSafety.Phase.DRAGGING,
                                                NotePressSafety.Phase.LIFTED, NotePressSafety.Phase.WARNING ->
                                                    if (canTransfer) latestLiftNote(note, null)
                                                NotePressSafety.Phase.PRESSED -> latestOpenNote(note)
                                                else -> Unit
                                            }
                                            NotePressSafety.Phase.IGNITED -> {
                                                // Once ignition is confirmed, the reversible data operation
                                                // is not owned by the visual animation's coroutine.
                                                if (gesture.mayCommitBurn()) latestIgnite(note)
                                            }
                                            else -> Unit
                                        }
                                    } finally {
                                        visualPhase = NotePressSafety.Phase.PRESSED
                                        // Data is unchanged until the overview accepts a drop.
                                    }
                                }
                            }
                    ) {
                        Box(Modifier.fillMaxWidth()) {
                            PaperSheet(Modifier.fillMaxWidth().heightIn(min = 68.dp), burnProgress = (burnProgress / .88f).coerceIn(0f, 1f)) {
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
                            if (warningActive || burningActive) {
                                NoteFire(
                                    progress = burnProgress,
                                    warning = warningActive && !burningActive,
                                    modifier = Modifier.matchParentSize()
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
