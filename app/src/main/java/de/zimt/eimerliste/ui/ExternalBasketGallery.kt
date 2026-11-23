package de.zimt.eimerliste.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/** Real photographs of hand-made objects. Each file's original author and CC0 provenance
 * are recorded in docs/DESIGN_ASSET_PROVENANCE.md. They are never presented as AI images.
 * This is an opt-in preview gallery, not an installed/offline bucket design yet. */
private data class BasketPhoto(val title: String, val filename: String, val author: String)
private val realBaskets = listOf(
    BasketPhoto("Geflochtener Korb", "Woven-Basket.jpg", "AgnesAbah"),
    BasketPhoto("Dekorkorb", "Decorative_woven_basket.jpg", "KaoneChombo"),
    BasketPhoto("Teepflückkorb", "Wicker_Basket_for_Tea_Harvesting.jpg", "AGRI-BIOTECH FARM INTERNATIONAL"),
    BasketPhoto("Traditioneller Korb", "Traditional_Woven_Basket.jpg", "Gaone1991"),
    BasketPhoto("Palmblattkorb", "Woven_basket_from_palm_leaves.jpg", "KaoneChombo"),
    BasketPhoto("Historischer Korb", "Wicker_Basket_Containing_Clay_Coins_(10112682876).jpg", "Gary Todd")
)

@Composable
private fun RemoteCommonsImage(filename: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var bitmap by remember(filename) { mutableStateOf<Bitmap?>(null) }
    var failed by remember(filename) { mutableStateOf(false) }
    LaunchedEffect(filename) {
        bitmap = withContext(Dispatchers.IO) {
            try {
                val encoded = java.net.URLEncoder.encode(filename, "UTF-8").replace("+", "%20")
                val url = URL("https://commons.wikimedia.org/wiki/Special:FilePath/$encoded?width=360")
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 6000
                conn.readTimeout = 9000
                conn.instanceFollowRedirects = true
                try {
                    if (conn.responseCode !in 200..299) null
                    else conn.inputStream.use { stream ->
                        val options = BitmapFactory.Options().apply { inSampleSize = 2 }
                        BitmapFactory.decodeStream(stream, null, options)
                    }
                } finally { conn.disconnect() }
            } catch (_: Exception) { null }
        }
        failed = bitmap == null
    }
    Box(modifier.background(Color(0xFF3A2A21), RoundedCornerShape(5.dp)),
        contentAlignment = Alignment.Center) {
        val loaded = bitmap
        if (loaded != null) {
            Image(loaded.asImageBitmap(), contentDescription = filename,
                modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
        } else {
            Text(if (failed) "Bild nicht verfügbar" else "Lädt …",
                color=Ink.Paper,fontSize=10.sp,textAlign=TextAlign.Center)
        }
    }
}

@Composable
fun RealBasketGallery() {
    realBaskets.chunked(3).forEach { row ->
        Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
            row.forEach { photo ->
                Column(Modifier.weight(1f), horizontalAlignment=Alignment.CenterHorizontally) {
                    RemoteCommonsImage(photo.filename,Modifier.fillMaxWidth().aspectRatio(.9f))
                    Text(photo.title,color=Ink.Paper,fontSize=10.sp,maxLines=2,textAlign=TextAlign.Center)
                    Text("CC0 · ${photo.author}",color=Ink.Muted,fontSize=8.sp,maxLines=1)
                }
            }
        }
        Spacer(Modifier.height(9.dp))
    }
}

@Composable
fun GeneratedBasketPreview() {
    val names = listOf("Weide", "Bambus", "Spiralgeflecht", "Picknick", "Holzdauben", "Kupfer")
    // Do not pass off generic Canvas buckets as actual image-generated illustrations.
    // The original generated contact sheet must first be imported and cut into six assets.
    names.chunked(3).forEach { row ->
        Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
            row.forEach { name ->
                Box(Modifier.weight(1f).padding(vertical=3.dp)
                    .background(Ink.Walnut, RoundedCornerShape(5.dp))
                    .height(64.dp),contentAlignment=Alignment.Center) {
                    Text(name,color=Ink.Paper,fontSize=11.sp,textAlign=TextAlign.Center)
                }
            }
        }
    }
}
