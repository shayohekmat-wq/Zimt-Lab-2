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

/** Kept separate from Room v1: preferences are keyed by existing stable bucket IDs. */
object BucketDesignPreferences {
    private const val PREFS = "bucket-design-lab-v1"
    fun get(context: Context, bucketId: String): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(bucketId, null)
    fun set(context: Context, bucketId: String, designId: String) {
        require(findLabDesign(designId) != null)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(bucketId, designId).apply()
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
            val silhouette = Path().apply {
                moveTo(w*left,h*top); lineTo(w*right,h*top)
                lineTo(w*(right-.07f),h*bottom)
                quadraticBezierTo(w*.5f,h*(bottom+.055f),w*(left+.07f),h*bottom)
                close()
            }
            drawOval(Color(0x55000000),Offset(w*.19f,h*.85f),Size(w*.62f,h*.08f))
            drawArc(edge,180f,180f,false,Offset(w*.16f,h*.08f),Size(w*.68f,h*.48f),
                style=Stroke(width=w*(if(st==0) .028f else .018f)))
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
                    4 -> { // faceted low-poly geometry
                        for(i in 0..5) {
                            val x=w*(.19f+i*.105f)
                            drawLine(if(i%2==0) Color.White.copy(alpha=.36f) else edge.copy(alpha=.3f),
                                Offset(x,h*.3f),Offset(w*.5f,h*(.60f+(i%3)*.085f)),w*.025f)
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
            drawOval(c.copy(alpha=.92f),Offset(w*(left-.015f),h*(top-.045f)),
                Size(w*(right-left+.03f),h*.11f))
            drawOval(edge,Offset(w*(left+.02f),h*(top-.017f)),
                Size(w*(right-left-.04f),h*.05f))
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
    modifier: Modifier = Modifier
) {
    var activeStyle by remember { mutableIntStateOf(0) }
    var chosen by remember { mutableStateOf<LabDesign?>(null) }
    var target by remember { mutableStateOf<String?>(null) }
    Column(modifier.padding(12.dp)) {
        Text("Designlabor",fontSize=22.sp,fontWeight=FontWeight.Bold,color=Ink.Paper)
        Spacer(Modifier.height(8.dp))
        // Eight style tabs; selection stays inside the existing scrollable bucket grid.
        styles.chunked(4).forEachIndexed { row, group ->
            Row(horizontalArrangement=Arrangement.spacedBy(4.dp)) {
                group.forEachIndexed { idx, name ->
                    val index=row*4+idx
                    Text(name,modifier=Modifier.weight(1f)
                        .background(if(activeStyle==index) Ink.BrassDark else Ink.Walnut,
                            RoundedCornerShape(5.dp))
                        .clickable { activeStyle=index;chosen=null }
                        .padding(vertical=9.dp,horizontal=2.dp)
                        .semantics { contentDescription="Stil $name auswählen" },
                        color=Ink.Paper,fontSize=10.sp,textAlign=TextAlign.Center)
                }
            }
            Spacer(Modifier.height(5.dp))
        }
        val shown = labDesigns.filter { it.style==activeStyle }
        shown.chunked(3).forEach { row ->
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                row.forEach { design ->
                    Column(Modifier.weight(1f).padding(vertical=4.dp)
                        .border(if(chosen==design) 2.dp else 1.dp,
                            if(chosen==design) Ink.Brass else Ink.Muted.copy(alpha=.4f),
                            RoundedCornerShape(6.dp))
                        .clickable { chosen=design;target=null }
                        .semantics { contentDescription="${design.name} auswählen" },
                        horizontalAlignment=Alignment.CenterHorizontally) {
                        LabBucketArtwork(design,"",Modifier.fillMaxWidth().aspectRatio(1f))
                        Text(design.name,color=Ink.Paper,fontSize=11.sp,maxLines=1)
                    }
                }
                repeat(3-row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
        chosen?.let { design ->
            Spacer(Modifier.height(8.dp))
            Text("Für welchen Eimer?",color=Ink.Paper,fontSize=13.sp)
            buckets.forEach { bucket ->
                Text(bucket.name,modifier=Modifier.fillMaxWidth()
                    .clickable { target=bucket.id }.padding(9.dp)
                    .background(if(target==bucket.id) Ink.BrassDark.copy(alpha=.35f)
                        else Color.Transparent),color=Ink.Paper)
            }
            Button(onClick={target?.let { onApplied(it,design.id);chosen=null }},
                enabled=target!=null,modifier=Modifier.fillMaxWidth()) { Text("Übernehmen") }
        }
    }
}
