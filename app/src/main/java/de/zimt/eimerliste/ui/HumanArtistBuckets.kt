package de.zimt.eimerliste.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import de.zimt.eimerliste.R

/**
 * Real third-party, human-authored 3D game art from Kenney (CC0).
 * These are 192px rasterized model previews, NOT fabricated layered renders.
 * No paper occlusion is claimed until matching REAR/FRONT passes are produced.
 * See docs/DESIGN_ASSET_PROVENANCE.md for file-specific source and license.
 */
data class HumanArtistBucket(val id: String, val title: String, val imageRes: Int)
val humanArtistBuckets = listOf(
    HumanArtistBucket("human-kenney-open-box", "Offene Schachtel", R.drawable.kenney_human_open_box),
    HumanArtistBucket("human-kenney-barrel", "Fass", R.drawable.kenney_human_barrel),
    HumanArtistBucket("human-kenney-pot", "Kochtopf", R.drawable.kenney_human_pot),
    HumanArtistBucket("human-kenney-chest", "Piratentruhe", R.drawable.kenney_human_pirate_chest),
    HumanArtistBucket("human-kenney-crate", "Holzkiste", R.drawable.kenney_human_crate)
)
fun findHumanArtistBucket(id: String?): HumanArtistBucket? =
    humanArtistBuckets.firstOrNull { it.id == id }

@Composable
fun HumanArtistBucketArtwork(asset: HumanArtistBucket, modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Image(painter = painterResource(asset.imageRes),
            contentDescription = asset.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize())
    }
}
