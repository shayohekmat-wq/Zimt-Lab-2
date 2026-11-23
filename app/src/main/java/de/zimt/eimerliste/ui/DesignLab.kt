package de.zimt.eimerliste.ui

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.zimt.eimerliste.data.BucketEntity
import kotlin.math.sin

/** All artwork is original Compose vector geometry. Stable IDs, no network or third-party assets. */
data class LabDesign(val style: Int, val variant: Int, val name: String) {
    val id: String get() = "lab-$style-$variant"
}
private val styles = listOf("Comic", "3D", "Pixel Art", "Aquarell", "Low Poly", "Knetoptik", "Kupferstich", "CharmingX4")
private val names = listOf(
    listOf("Strandkübel","Farbeimer","Schatzkorb","Gartenkorb","Holzeimer","Sternenkübel"),
    listOf("Kupfer","Rattan","Emaille","Messing","Keramik","Eichenholz"),
    listOf("Abenteuer","Bergwerk","Schatz","Zauberwald","Burg","Markt"),
    listOf("Blütenkorb","Lavendel","Tonkrug","Weide","Blauer Eimer","Sommer"),
    listOf("Sechseck","Stein","Polygon","Holz","Stahl","Kristall"),
    listOf("Ton","Bonbon","Blume","Wolle","Bunt","Schief"),
    listOf("Kohleeimer","Bauernkorb","Apotheke","Zierkübel","Wasser","Antik"),
    listOf("Ahorn","Walnuss","Amethyst","Glas","Messing","Zink")
)
val labDesigns: List<LabDesign> = names.flatMapIndexed { style, row ->
    row.mapIndexed { variant, name -> LabDesign(style, variant, name) }
}
fun findLabDesign(id: String?): LabDesign? = labDesigns.firstOrNull { it.id == id }

/** Normalized anchors allow independently authored art to share app interactions.
 * Artwork coordinates are relative to the visible object's full bounding box.
 * Future bitmap designs must provide front/back layers instead of fake galleries.
 */
data class BucketVisualProfile(
    val designId: String,
    val openingLeft: Float,
    val openingRight: Float,
    val openingY: Float,
    val labelY: Float,
    val hasSeparateFrontLayer: Boolean = false
) {
    init {
        require(openingLeft in 0f..1f && openingRight in 0f..1f)
        require(openingLeft < openingRight)
        require(openingY in 0f..1f && labelY in 0f..1f)
    }
}
fun visualProfile(design: LabDesign): BucketVisualProfile {
    val lowPoly = design.style == 4
    val openingY = if (lowPoly) when (design.variant) {
        1 -> .38f
        4 -> .40f
        2 -> .20f
        5 -> .23f
        0 -> .25f
        else -> .29f
    } else .27f + (design.variant % 3) * .025f
    val openingLeft = if (lowPoly) when (design.variant) {
        4 -> .10f
        1 -> .12f
        2 -> .30f
        5 -> .28f
        0 -> .23f
        else -> .20f
    } else .17f + (design.variant % 2) * .035f
    return BucketVisualProfile(design.id, openingLeft, 1f-openingLeft, openingY, .54f)
}

/** Kept separate from Room v1: preferences are keyed by existing stable bucket IDs. */
object BucketDesignPreferences {
    private const val PREFS = "bucket-design-lab-v1"
    fun get(context: Context, bucketId: String): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(bucketId, null)
    fun set(context: Context, bucketId: String, designId: String) {
        require(BucketArtworkCatalog.isKnownDesignId(designId))
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(bucketId, designId).apply()
    }
    fun clear(context: Context, bucketId: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .remove(bucketId).apply()
    }
}

@Composable
fun LabBucketArtwork(design: LabDesign, title: String, modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val v = design.variant
            val st = design.style
            val base = when(st) {
                0 -> listOf(0xFFE9B85E,0xFF79B6CA,0xFFD77C73,0xFF96B477,0xFFC68B56,0xFF8D78BE)
                1 -> listOf(0xFFBA7148,0xFFB18A58,0xFFE2D9C5,0xFFC8A252,0xFFAF8B76,0xFF795B43)
                2 -> listOf(0xFFAD7047,0xFF717A81,0xFFDBAD42,0xFF6B9776,0xFF937BA5,0xFFCA9362)
                3 -> listOf(0xFFDAACB4,0xFF9A93CC,0xFFD4A078,0xFFBDA77A,0xFF94BBD1,0xFFB8CA8A)
                4 -> listOf(0xFF7BA6A4,0xFF9B9A9E,0xFFBEA178,0xFF8EAE8A,0xFF889DAB,0xFFA8A2C3)
                5 -> listOf(0xFFDDA77E,0xFFE7A0B8,0xFFB5D4A3,0xFFB7B0DD,0xFFF3C873,0xFFD5A6A1)
                6 -> listOf(0xFFAB8459,0xFFB19C75,0xFFAA8A69,0xFFB7A077,0xFF90795B,0xFF9D835C)
                else -> listOf(0xFFB58E62,0xFF715039,0xFF9B79B4,0xFFA1B8B5,0xFFD5AE64,0xFF8B999D)
            }
            val c = Color(base[v])
            val edge = if(st == 0) Color(0xFF302A28) else Color(0xFF4D3728)
            val top = .27f + (v % 3) * .025f
            val bottom = .85f - (v % 2) * .025f
            val left = .17f + (v % 2) * .035f
            val right = .83f - (v % 3) * .018f
            // Shape is part of the design, not just a color parameter.
            // The six low-poly variants have distinct physical constructions.
            val silhouette = Path().apply {
                if (st == 4) {
                    when(v) {
                        0 -> { // Hexagonal vessel, angular rim and straight walls
                            moveTo(w*.23f,h*.25f); lineTo(w*.77f,h*.25f)
                            lineTo(w*.88f,h*.37f); lineTo(w*.77f,h*.79f)
                            lineTo(w*.5f,h*.89f); lineTo(w*.23f,h*.79f)
                            lineTo(w*.12f,h*.37f)
                        }
                        1 -> { // Heavy trapezoid stone trough, no curved base
                            moveTo(w*.12f,h*.38f); lineTo(w*.88f,h*.38f)
                            lineTo(w*.79f,h*.83f); lineTo(w*.21f,h*.83f)
                        }
                        2 -> { // Tall, narrow asymmetric polygon
                            moveTo(w*.30f,h*.20f); lineTo(w*.77f,h*.25f)
                            lineTo(w*.84f,h*.48f); lineTo(w*.68f,h*.88f)
                            lineTo(w*.31f,h*.83f); lineTo(w*.19f,h*.44f)
                        }
                        3 -> { // Stave-built wooden bucket
                            moveTo(w*.20f,h*.29f); lineTo(w*.80f,h*.29f)
                            lineTo(w*.72f,h*.86f); lineTo(w*.28f,h*.86f)
                        }
                        4 -> { // Wide squat steel pail
                            moveTo(w*.10f,h*.40f); lineTo(w*.90f,h*.40f)
                            lineTo(w*.75f,h*.75f); lineTo(w*.25f,h*.75f)
                        }
                        else -> { // Crystal taper with a pointed bottom
                            moveTo(w*.28f,h*.23f); lineTo(w*.72f,h*.23f)
                            lineTo(w*.88f,h*.39f); lineTo(w*.70f,h*.74f)
                            lineTo(w*.50f,h*.91f); lineTo(w*.30f,h*.74f)
                            lineTo(w*.12f,h*.39f)
                        }
                    }
                } else {
                    moveTo(w*left,h*top); lineTo(w*right,h*top)
                    lineTo(w*(right-.07f),h*bottom)
                    quadraticBezierTo(w*.5f,h*(bottom+.055f),w*(left+.07f),h*bottom)
                }
                close()
            }
            drawOval(Color(0x55000000),Offset(w*.19f,h*.85f),Size(w*.62f,h*.08f))
            if (st != 4 || v == 3 || v == 4) {
                drawArc(edge,180f,180f,false,Offset(w*.16f,h*.08f),Size(w*.68f,h*.48f),
                    style=Stroke(width=w*(if(st==0) .028f else .018f)))
            }
            drawPath(silhouette,brush=if(st==2) Brush.horizontalGradient(listOf(c,c))
                else Brush.horizontalGradient(listOf(c.copy(alpha=.72f),c,c.copy(alpha=.7f))))
            clipPath(silhouette) {
                when(st) {
                    0 -> { // comic: bright halftone dots and strong ink outlines
                        for (x in 0..7) for (y in 0..7) {
                            drawCircle(Color.White.copy(alpha=.18f),w*.011f,
                                Offset(w*(.23f+x*.075f),h*(.35f+y*.065f)))
                        }
                    }
                    1 -> { // dimensional metal/wood: reflected light and vertical grain
                        for(i in 0..9) drawLine(
                            if(i%3==0) Color.White.copy(alpha=.23f) else edge.copy(alpha=.13f),
                            Offset(w*(.22f+i*.059f),h*.30f),
                            Offset(w*(.25f+i*.046f),h*.84f),w*.008f)
                    }
                    2 -> { // deliberately stepped pixel blocks
                        for(i in 0..7) for(j in 0..6) if((i+j+v)%3==0)
                            drawRect(edge.copy(alpha=.22f),Offset(w*(.2f+i*.076f),h*(.34f+j*.077f)),
                                Size(w*.065f,h*.058f))
                    }
                    3 -> { // transparent pigment washes
                        for(i in 0..12) drawCircle(
                            if(i%2==0) Color.White.copy(alpha=.10f) else edge.copy(alpha=.07f),
                            w*(.08f+i%3*.017f),
                            Offset(w*(.24f+(i*7%9)*.061f),h*(.34f+(i*3%8)*.067f)))
                    }
                    4 -> { // Each construction receives different facet planes.
                        val facets = when(v) {
                            0 -> listOf(.18f,.34f,.50f,.66f,.82f)
                            1 -> listOf(.21f,.40f,.60f,.79f)
                            2 -> listOf(.26f,.46f,.73f)
                            3 -> listOf(.28f,.40f,.52f,.64f,.76f)
                            4 -> listOf(.20f,.37f,.63f,.80f)
                            else -> listOf(.20f,.36f,.50f,.64f,.80f)
                        }
                        facets.forEachIndexed { i, x ->
                            drawLine(if(i%2==0) Color.White.copy(alpha=.25f)
                                else edge.copy(alpha=.26f),
                                Offset(w*x,h*.26f),
                                Offset(w*(x + (if(i%2==0) .035f else -.025f)),h*.87f),
                                w*.012f)
                        }
                        if(v==3) { // Separate stave hoops
                            listOf(.43f,.73f).forEach { y ->
                                drawLine(edge.copy(alpha=.8f),Offset(w*.18f,h*y),
                                    Offset(w*.82f,h*y),w*.03f)
                            }
                        }
                    }
                    5 -> { // handmade clay fingerprint rings
                        for(i in 0..7) drawArc(Color.White.copy(alpha=.12f),
                            10f,145f,false,Offset(w*(.19f+i*.012f),h*(.33f+i*.062f)),
                            Size(w*.53f,h*.12f),style=Stroke(w*.012f))
                    }
                    6 -> { // fine vintage engraved crosshatching
                        for(i in 0..24) {
                            val y=h*(.31f+i*.024f)
                            drawLine(edge.copy(alpha=.35f),Offset(w*.14f,y),Offset(w*.88f,y+h*.12f),w*.004f)
                            if(i%2==0) drawLine(edge.copy(alpha=.19f),Offset(w*.14f,y+h*.12f),Offset(w*.88f,y),w*.003f)
                        }
                    }
                    else -> { // polished charmingX4, wood and brass trim
                        for(i in 0..7) drawLine(edge.copy(alpha=.24f),
                            Offset(w*(.24f+i*.075f),h*.29f),
                            Offset(w*(.27f+i*.06f),h*.84f),w*.006f)
                        drawLine(Color(0xFFE6C78C),Offset(w*.21f,h*.67f),Offset(w*.79f,h*.67f),w*.025f)
                    }
                }
            }
            drawPath(silhouette,edge,style=Stroke(w*(if(st==0) .023f else .012f)))
            if (st != 4) {
                drawOval(c.copy(alpha=.92f),Offset(w*(left-.015f),h*(top-.045f)),
                    Size(w*(right-left+.03f),h*.11f))
                drawOval(edge,Offset(w*(left+.02f),h*(top-.017f)),
                    Size(w*(right-left-.04f),h*.05f))
            } else {
                val profile = visualProfile(design)
                val rimY = profile.openingY
                val rimL = profile.openingLeft
                val rimR = profile.openingRight
                drawLine(edge,Offset(w*rimL,h*rimY),Offset(w*rimR,h*rimY),w*.027f)
                drawLine(Color.White.copy(alpha=.32f),
                    Offset(w*(rimL+.025f),h*(rimY-.015f)),
                    Offset(w*(rimR-.025f),h*(rimY-.015f)),w*.012f)
            }
            if(st==0 || st==7) {
                drawCircle(Color(0xFFE9C58D),w*.016f,Offset(w*left,h*(top+.05f)))
                drawCircle(Color(0xFFE9C58D),w*.016f,Offset(w*right,h*(top+.05f)))
            }
        }
        if(title.isNotBlank()) {
            Box(Modifier.fillMaxWidth(.57f).offset(y=19.dp)
                .background(Color(0xFFF3E1BC),RoundedCornerShape(3.dp))
                .border(1.dp,Color(0xFF806340),RoundedCornerShape(3.dp))
                .padding(horizontal=4.dp,vertical=5.dp),contentAlignment=Alignment.Center) {
                Text(title,color=Color(0xFF33281E),fontSize=12.sp,fontWeight=FontWeight.Bold,
                    maxLines=2,textAlign=TextAlign.Center)
            }
        }
    }
}

@Composable
fun DesignLab(
    buckets: List<BucketEntity>,
    onApplied: (String, String) -> Unit,
    onReset: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Scope freeze: exactly three original user-provided front/rear PNG pairs.
    val designs = illustratedBucketAssets.filter { it.id.startsWith("user-") }
    var selected by remember { mutableStateOf(designs.first().id) }
    var target by remember { mutableStateOf<String?>(null) }
    var confirm by remember { mutableStateOf(false) }
    var paperProgress by remember { mutableFloatStateOf(1f) }
    var playing by remember { mutableStateOf(false) }
    LaunchedEffect(playing) {
        if (playing) {
            for (step in 0..36) {
                paperProgress = step / 36f
                kotlinx.coroutines.delay(19L)
            }
            kotlinx.coroutines.delay(250L)
            for (step in 36 downTo 0) {
                paperProgress = step / 36f
                kotlinx.coroutines.delay(19L)
            }
            paperProgress = 1f
            playing = false
        }
    }
    Column(modifier.padding(12.dp)) {
        Text("Designlabor", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Ink.Paper)
        Spacer(Modifier.height(7.dp))
        Text("Drei Originale · deine Grafiken · zwei echte Bildebenen",
            color = Ink.Muted, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        designs.forEach { design ->
            val active = selected == design.id
            Row(
                Modifier.fillMaxWidth().padding(vertical = 5.dp)
                    .border(if (active) 2.dp else 1.dp,
                        if (active) Ink.Brass else Ink.Muted.copy(alpha = .35f),
                        RoundedCornerShape(8.dp))
                    .background(if (active) Ink.BrassDark.copy(alpha = .18f) else Ink.Walnut,
                        RoundedCornerShape(8.dp))
                    .clickable {
                        selected = design.id
                        paperProgress = 1f
                        confirm = false
                    }
                    .semantics { contentDescription = "${design.title} auswählen" }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LayeredIllustratedBucket(design, "", 1f, Modifier.size(105.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(design.title, color = Ink.Paper, fontSize = 16.sp,
                        fontWeight = FontWeight.Bold)
                    Text("Original-PNG · Innenraum + Vorderwand",
                        color = Ink.Muted, fontSize = 11.sp)
                }
                Text(if (active) "●" else "○", color = Ink.Brass, fontSize = 20.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("LIVE-VORSCHAU", color = Ink.Brass, fontSize = 12.sp,
            fontWeight = FontWeight.Bold)
        val asset = designs.first { it.id == selected }
        LayeredIllustratedBucket(asset, "", paperProgress,
            Modifier.fillMaxWidth().height(260.dp))
        Text("Zettel zwischen Innenraum und Vorderwand",
            color = Ink.Muted, fontSize = 11.sp)
        Slider(value = paperProgress, onValueChange = { paperProgress = it },
            valueRange = 0f..1f)
        OutlinedButton(onClick = { playing = true }, enabled = !playing,
            modifier = Modifier.fillMaxWidth()) {
            Text(if (playing) "Animation läuft …" else "Einwurf und Auswurf abspielen")
        }
        Spacer(Modifier.height(12.dp))
        Text("Für welchen Eimer?", color = Ink.Paper, fontSize = 14.sp)
        if (buckets.isEmpty()) {
            Text("Erstelle zuerst einen Eimer.", color = Ink.Muted)
        }
        buckets.forEach { bucket ->
            val active = target == bucket.id
            Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)
                .border(if (active) 2.dp else 1.dp,
                    if (active) Ink.Brass else Ink.Muted.copy(alpha = .35f),
                    RoundedCornerShape(6.dp))
                .clickable { target = bucket.id; confirm = false }
                .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text(if (active) "●" else "○", color = Ink.Brass)
                Spacer(Modifier.width(10.dp))
                Text(bucket.name, color = Ink.Paper, modifier = Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { confirm = true }, enabled = target != null,
            modifier = Modifier.fillMaxWidth()) { Text("Design übernehmen") }
        if (confirm && target != null) {
            AlertDialog(onDismissRequest = { confirm = false },
                title = { Text("Design übernehmen?") },
                text = { Text("Nur die Grafik wird geändert. Alle Zettel und Zeichnungen bleiben erhalten.") },
                confirmButton = {
                    TextButton(onClick = {
                        target?.let { onApplied(it, selected) }
                        confirm = false
                    }) { Text("Übernehmen") }
                },
                dismissButton = {
                    TextButton(onClick = { confirm = false }) { Text("Abbrechen") }
                })
        }
    }
}
