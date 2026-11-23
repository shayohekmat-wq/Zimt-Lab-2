package de.zimt.eimerliste.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import de.zimt.eimerliste.R

/**
 * Bundled user PNG artwork split into transparent rear and front layers.
 * The folded note is always drawn between these layers and disappears into the opening.
 * This renderer is deliberately independent of the app's Room model for later ZIMT reuse.
 */
data class IllustratedBucketAsset(
    val id: String,
    val title: String,
    val rear: Int,
    val front: Int
)

// Only the three user-provided PNG pairs are selectable in the active design laboratory.
val illustratedBucketAssets = listOf(
    IllustratedBucketAsset("user-wicker-01", "Flechtkorb", R.drawable.flechtkorb_hinten, R.drawable.flechtkorb_vorne),
    IllustratedBucketAsset("user-wood-01", "Holzkübel", R.drawable.holzkuebel_hinten, R.drawable.holzkuebel_vorne),
    IllustratedBucketAsset("user-zinc-01", "Zinkeimer", R.drawable.bucket_zinc_user_rear, R.drawable.bucket_zinc_user_front)
)

// Historical designs remain resolvable for existing saved selections; never offer them as new choices.
private val legacyIllustratedBucketAssets = listOf(
IllustratedBucketAsset("illustrated-astral-01", "Astralgefäß",
        R.drawable.bucket_astral_rear, R.drawable.bucket_astral_front),
    IllustratedBucketAsset("illustrated-copper-01", "Kupferkessel",
        R.drawable.bucket_copper_rear, R.drawable.bucket_copper_front),
    IllustratedBucketAsset("illustrated-porcelain-01", "Porzellankrug",
        R.drawable.bucket_porcelain_rear, R.drawable.bucket_porcelain_front),
    IllustratedBucketAsset("illustrated-forest-01", "Waldkorb",
        R.drawable.bucket_forest_rear, R.drawable.bucket_forest_front),
    IllustratedBucketAsset("illustrated-clockwork-01", "Uhrwerkkorb",
        R.drawable.bucket_clockwork_rear, R.drawable.bucket_clockwork_front),
    IllustratedBucketAsset("illustrated-amethyst-01", "Amethysttruhe",
        R.drawable.bucket_amethyst_rear, R.drawable.bucket_amethyst_front)
)

fun findIllustratedBucket(id: String?): IllustratedBucketAsset? =
    (illustratedBucketAssets + legacyIllustratedBucketAssets).firstOrNull { it.id == id }

@Composable
fun LayeredIllustratedBucket(
    asset: IllustratedBucketAsset,
    title: String,
    paperProgress: Float,
    modifier: Modifier = Modifier
) {
    val t = if (paperProgress.isFinite()) paperProgress.coerceIn(0f, 1f) else 0f
    // Smooth the insertion/extraction at both ends; reverse progress uses the same path.
    val eased = t * t * (3f - 2f * t)
    // Artwork shares a viewport; only the paper flight is design-specific.
    val mouthStart = when (asset.id) {
        "user-wicker-01" -> .14f
        "user-wood-01" -> .12f
        "user-zinc-01" -> .13f
        else -> .14f
    }
    val mouthTravel = when (asset.id) {
        "user-wicker-01" -> .36f
        "user-wood-01" -> .33f
        "user-zinc-01" -> .35f
        else -> .36f
    }
    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val vesselHeight = maxHeight
        // Both original PNG layers use precisely the same Fit viewport.
        // Do not translate or scale either layer independently: doing so changes
        // the opening geometry and breaks the authored front/rear alignment.
        Image(painterResource(asset.rear), contentDescription = null,
            contentScale = androidx.compose.ui.layout.ContentScale.Fit,
            modifier = Modifier.fillMaxSize())
        // Paper starts above the rim and descends behind the opaque front.
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Box(
                Modifier.fillMaxWidth(.18f).fillMaxHeight(.15f)
                    .offset(y = vesselHeight * (mouthStart + mouthTravel * eased))
                    .graphicsLayer {
                        rotationZ = -9f + 15f * eased
                        scaleX = 1f - .18f * eased
                        scaleY = 1f - .18f * eased
                        // Fully hide the note at the bottom, including transparent front openings.
                        alpha = (1f - ((eased - .65f) / .35f).coerceIn(0f, 1f))
                    }
            ) {
                androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                    drawRect(Color(0xFFFFF1CF))
                    drawLine(Color(0xFFBDA47D),
                        androidx.compose.ui.geometry.Offset(size.width * .15f, size.height * .48f),
                        androidx.compose.ui.geometry.Offset(size.width * .85f, size.height * .48f),
                        strokeWidth = 2f)
                }
            }
        }
        Image(painterResource(asset.front), contentDescription = null,
            contentScale = androidx.compose.ui.layout.ContentScale.Fit,
            modifier = Modifier.fillMaxSize())
        // Do not paint a second, clipped title over the decorative artwork.
        // Gallery cards already display the full bucket name below the image.

    }
}
