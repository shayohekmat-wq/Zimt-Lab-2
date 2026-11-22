package de.zimt.eimerliste.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import kotlin.math.min

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
        var draft by rememberSaveable { mutableStateOf("") }
        var busy by remember { mutableStateOf(false) }
        var foldingText by remember { mutableStateOf<String?>(null) }
        val folding = remember { Animatable(0f) }
        val scope = rememberCoroutineScope()
        val snackbar = remember { SnackbarHostState() }

        val selectedBucket = buckets.firstOrNull { it.id == selectedId }
        val countByBucket = remember(counts) { counts.associate { it.bucketId to it.noteCount } }

        LaunchedEffect(buckets, selectedId) {
            if (buckets.isNotEmpty() && selectedBucket == null) vm.selectBucket(buckets.first().id)
        }

        val send: () -> Unit = {
            val bucketId = selectedId
            val message = draft
            if (!busy && bucketId != null && NoteRules.canSubmit(message)) {
                scope.launch {
                    busy = true
                    try {
                        // Persist first. A failed write must never look like a successfully thrown note.
                        vm.addNote(bucketId, message)
                        draft = ""
                        foldingText = message
                        folding.snapTo(0f)
                        for (step in 1..4) {
                            folding.animateTo(
                                step.toFloat(),
                                animationSpec = tween(durationMillis=245,easing=FastOutSlowInEasing)
                            )
                        }
                        folding.animateTo(5f, tween(durationMillis=430,easing=FastOutSlowInEasing))
                    } catch (error: Exception) {
                        snackbar.showSnackbar("Der Zettel konnte nicht gespeichert werden. Dein Text bleibt erhalten.")
                    } finally {
                        busy = false
                        foldingText = null
                    }
                }
            }
        }

        Box(Modifier.fillMaxSize()) {
            WoodBackdrop(Modifier.fillMaxSize())
            Column(Modifier.fillMaxSize()) {
                Header(
                    onAdd = { createDialog = true },
                    bucketCount = buckets.size
                )
                if (buckets.isEmpty()) {
                    EmptyStage(
                        modifier = Modifier.weight(1f),
                        onCreate = { createDialog = true }
                    )
                } else {
                    Text(
                        "WÄHLE EINEN EIMER · TIPPE ZUM ÖFFNEN",
                        modifier=Modifier.padding(start=22.dp,top=11.dp,bottom=4.dp),
                        fontSize=10.sp,letterSpacing=1.1.sp,color=Ink.Muted
                    )
                    LazyVerticalGrid(
                        columns=GridCells.Fixed(2),
                        modifier=Modifier.weight(1f),
                        contentPadding=PaddingValues(horizontal=13.dp, vertical=7.dp),
                        horizontalArrangement=Arrangement.spacedBy(7.dp),
                        verticalArrangement=Arrangement.spacedBy(15.dp)
                    ) {
                        items(buckets, key={it.id}) { bucket ->
                            BucketTile(
                                bucket=bucket,
                                count=countByBucket[bucket.id] ?: 0,
                                selected=bucket.id==selectedId,
                                onOpen={
                                    vm.selectBucket(bucket.id)
                                    openBucket=bucket.id
                                }
                            )
                        }
                    }
                }
                WoodenShelf()
                PaperComposer(
                    title = selectedBucket?.name,
                    draft = draft,
                    onDraftChange = { draft = it.take(NoteRules.MAX_NOTE_LENGTH) },
                    onSend = send,
                    enabled = selectedId != null && !busy,
                    modifier=Modifier.fillMaxWidth().padding(horizontal=13.dp,vertical=14.dp)
                )
            }
            SnackbarHost(hostState=snackbar, modifier=Modifier.align(Alignment.TopCenter))
            foldingText?.let { text ->
                FoldedPaperOverlay(
                    message=text,
                    progress=folding.value,
                    modifier=Modifier.align(Alignment.BottomCenter).padding(bottom=106.dp)
                )
            }
        }

        if (createDialog) {
            CreateBucketDialog(
                onClose={ createDialog=false },
                onCreate={ name, material ->
                    scope.launch {
                        try {
                            vm.addBucket(name,material)
                            createDialog=false
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
                onDismissRequest={ openBucket=null },
                containerColor=Ink.Paper,
                contentColor=Ink.InkBlack,
                dragHandle={ BottomSheetDefaults.DragHandle(color=Ink.BrassDark) }
            ) {
                BucketContents(
                    bucket=visibleBucket,
                    notes=if (visibleBucket.id==selectedId) notes else emptyList(),
                    onNote={ openNote=it }
                )
            }
        }
        openNote?.let { note ->
            DialogPaper(
                note=note,
                onDismiss={ openNote=null }
            )
        }
    }
}

@Composable
private fun Header(onAdd: () -> Unit, bucketCount:Int) {
    Row(
        Modifier.fillMaxWidth().padding(start=23.dp,end=20.dp,top=21.dp,bottom=9.dp),
        verticalAlignment=Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                "Eimer-Liste",
                fontFamily=FontFamily.Serif,
                fontSize=33.sp, lineHeight=36.sp,
                fontWeight=FontWeight.Bold,
                color=Ink.Paper
            )
            Text(
                "Für alles, was wir noch erleben wollen",
                fontSize=12.sp,color=Ink.Muted
            )
        }
        Box(
            Modifier.size(48.dp).clip(CircleShape)
                .background(Brush.verticalGradient(listOf(Ink.Brass,Ink.BrassDark)))
                .clickable(onClick=onAdd),
            contentAlignment=Alignment.Center
        ) {
            Text("+",fontSize=29.sp,color=Ink.InkBlack)
        }
    }
}

@Composable
private fun EmptyStage(modifier:Modifier,onCreate:()->Unit) {
    Column(modifier.fillMaxWidth().padding(24.dp), horizontalAlignment=Alignment.CenterHorizontally, verticalArrangement=Arrangement.Center) {
        BucketArtwork(
            material=BucketMaterial.WICKER,
            title="Dein erster Eimer",
            modifier=Modifier.size(width=210.dp,height=224.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text("Jeder Wunsch beginnt mit einem Zettel.",
            fontFamily=FontFamily.Serif,fontSize=21.sp,color=Ink.Paper,textAlign=TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick=onCreate,
            colors=ButtonDefaults.buttonColors(containerColor=Ink.Brass,contentColor=Ink.InkBlack)
        ) { Text("Ersten Eimer erstellen") }
    }
}

@Composable
private fun BucketTile(bucket:BucketEntity,count:Int,selected:Boolean,onOpen:()->Unit) {
    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if(selected) Color(0x304C3B2A) else Color(0x1A120A07))
            .border(1.dp,if(selected) Ink.BrassDark else Color(0x354E3726),RoundedCornerShape(12.dp))
            .clickable(onClick=onOpen)
            .padding(vertical=6.dp),
        horizontalAlignment=Alignment.CenterHorizontally
    ) {
        BucketArtwork(
            material=BucketMaterial.fromKey(bucket.material),
            title=bucket.name,
            modifier=Modifier.fillMaxWidth().aspectRatio(1.08f)
        )
        Text(
            if(count==1) "1 Zettel" else count.toString()+" Zettel",
            color=if(selected) Ink.Brass else Ink.Muted,
            fontSize=12.sp,
            modifier=Modifier.padding(bottom=8.dp)
        )
    }
}

@Composable
private fun PaperComposer(
    title:String?,
    draft:String,
    onDraftChange:(String)->Unit,
    onSend:()->Unit,
    enabled:Boolean,
    modifier:Modifier=Modifier
) {
    Column(modifier) {
        Text(
            title?.let { "ZETTEL FÜR „"+it+"“" } ?: "DEIN PAPIERZETTEL",
            color=Ink.Muted,fontSize=10.sp,letterSpacing=1.2.sp,
            modifier=Modifier.padding(start=9.dp,bottom=6.dp),
            maxLines=1,overflow=TextOverflow.Ellipsis
        )
        PaperSheet(Modifier.fillMaxWidth().heightIn(min=90.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(start=17.dp,end=13.dp,top=14.dp,bottom=15.dp),
                verticalAlignment=Alignment.Bottom
            ) {
                BasicTextField(
                    value=draft,
                    onValueChange=onDraftChange,
                    enabled=enabled,
                    modifier=Modifier.weight(1f).heightIn(min=53.dp,max=142.dp),
                    textStyle=TextStyle(
                        fontFamily=FontFamily.Serif,
                        color=Ink.InkBlack,fontSize=17.sp,lineHeight=23.sp
                    ),
                    keyboardOptions=KeyboardOptions(imeAction=ImeAction.Send),
                    keyboardActions=KeyboardActions(onSend={onSend()}),
                    decorationBox={ inner ->
                        Box {
                            if(draft.isEmpty()) {
                                Text(
                                    "Schreib einen Wunsch auf ...",
                                    fontFamily=FontFamily.Serif,
                                    color=Ink.BrassDark.copy(alpha=.85f),
                                    fontSize=16.sp
                                )
                            }
                            inner()
                        }
                    }
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    Modifier.size(45.dp).clip(CircleShape)
                        .background(if(enabled && NoteRules.canSubmit(draft)) Ink.Brass else Color(0xFFCCBA98))
                        .clickable(enabled=enabled && NoteRules.canSubmit(draft),onClick=onSend),
                    contentAlignment=Alignment.Center
                ) {
                    Text("➤",color=Ink.InkBlack,fontSize=23.sp)
                }
            }
        }
    }
}

@Composable
private fun FoldedPaperOverlay(message:String, progress:Float, modifier:Modifier=Modifier) {
    val fold = min(progress,4f)
    val fly = (progress - 4f).coerceIn(0f,1f)
    Box(
        modifier
            .graphicsLayer {
                scaleX = (1f - .17f * fold) * (1f - .55f * fly)
                scaleY = (1f - .16f * fold) * (1f - .55f * fly)
                rotationZ = 7f * fold + 45f * fly
                translationY = -(70.dp.toPx()*fold/4f + 180.dp.toPx()*fly)
                translationX = 36.dp.toPx() * fly
                alpha = 1f - .95f*fly
            }
    ) {
        PaperSheet(Modifier.width(215.dp).height(87.dp)) {
            Text(
                text=message,
                modifier=Modifier.padding(13.dp),
                maxLines=3,overflow=TextOverflow.Ellipsis,
                color=Ink.InkBlack,fontSize=15.sp,fontFamily=FontFamily.Serif
            )
        }
    }
}

@Composable
private fun CreateBucketDialog(onClose:()->Unit,onCreate:(String,BucketMaterial)->Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var material by remember { mutableStateOf(BucketMaterial.ZINC) }
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
private fun BucketContents(bucket:BucketEntity,notes:List<NoteEntity>,onNote:(NoteEntity)->Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal=20.dp).heightIn(min=260.dp,max=650.dp)) {
        Text(bucket.name,fontFamily=FontFamily.Serif,fontSize=30.sp,fontWeight=FontWeight.Bold)
        Text(
            if(notes.isEmpty()) "Noch kein Zettel drin." else
                notes.size.toString()+" gefaltete "+if(notes.size==1) "Notiz" else "Notizen",
            color=Ink.BrassDark,fontSize=12.sp
        )
        Spacer(Modifier.height(14.dp))
        if(notes.isEmpty()) {
            Text("Schreib unten einen Wunsch auf, falte ihn und wirf ihn hier hinein.",
                color=Ink.InkBlack,modifier=Modifier.padding(vertical=30.dp))
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
                            Text(
                                note.text,
                                maxLines=2,
                                overflow=TextOverflow.Ellipsis,
                                color=Ink.InkBlack,fontSize=16.sp,fontFamily=FontFamily.Serif
                            )
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
        onDismissRequest=onDismiss,
        title={Text("Dein Wunsch",fontFamily=FontFamily.Serif,color=Ink.InkBlack)},
        text={
            PaperSheet(Modifier.fillMaxWidth().heightIn(min=160.dp)) {
                Text(
                    note.text,
                    Modifier.padding(22.dp),
                    color=Ink.InkBlack,fontFamily=FontFamily.Serif,fontSize=20.sp,lineHeight=29.sp
                )
            }
        },
        confirmButton={TextButton(onClick=onDismiss){Text("Wieder falten",color=Ink.BrassDark)}},
        containerColor=Ink.Paper
    )
}
