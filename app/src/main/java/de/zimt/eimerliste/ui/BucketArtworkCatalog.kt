package de.zimt.eimerliste.ui

/**
 * Stable artwork contract for future PNG, SVG/vector and authored 3D-render layers.
 * All coordinates are normalized to the full artwork bounds.
 *
 * This is the catalog foundation, not a claim that premium layered artwork exists yet.
 */
enum class ArtworkOrigin { HUMAN_DESIGNED, AI_GENERATED, ORIGINAL_CONSTRUCTION, LEGACY_EXPERIMENT }

data class ArtworkOpening(
    val left: Float,
    val right: Float,
    val top: Float,
    val bottom: Float
) {
    init {
        require(left in 0f..1f && right in 0f..1f && left < right)
        require(top in 0f..1f && bottom in 0f..1f && top < bottom)
    }
}

/**
 * Normalized paper trajectory. Coordinates stay independent of image resolution,
 * so a paper animation can be reused by authored PNG/SVG and 3D-rendered assets.
 */
data class ArtworkPaperPath(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float
) {
    init {
        require(startX in 0f..1f && startY in 0f..1f)
        require(endX in 0f..1f && endY in 0f..1f)
    }

    fun position(progress: Float): Pair<Float, Float> {
        val t = progress.coerceIn(0f, 1f)
        return (startX + (endX - startX) * t) to
            (startY + (endY - startY) * t)
    }
}

/** Paint order is enforced independently of asset origin. */
enum class ArtworkLayer { REAR, PAPER, FRONT, LABEL }
val BUCKET_PAINT_ORDER = listOf(
    ArtworkLayer.REAR, ArtworkLayer.PAPER, ArtworkLayer.FRONT, ArtworkLayer.LABEL
)

data class BucketArtworkSpec(
    val id: String,
    val displayName: String,
    val origin: ArtworkOrigin,
    val opening: ArtworkOpening,
    val labelY: Float,
    /** Coordinates used by both incoming and outgoing folded-paper animation. */
    val paperPath: ArtworkPaperPath = ArtworkPaperPath(
        startX = (opening.left + opening.right) / 2f,
        startY = opening.top,
        endX = (opening.left + opening.right) / 2f,
        endY = opening.bottom
    ),
    val backLayerResource: String? = null,
    val frontLayerResource: String? = null,
    val openingMaskResource: String? = null,
    val sourceUrl: String? = null,
    val licenseId: String? = null,
    val released: Boolean = false
) {
    init {
        require(id.isNotBlank() && displayName.isNotBlank())
        require(labelY in 0f..1f)
        require(paperPath.startY <= opening.bottom && paperPath.endY >= opening.top)
        // Paper must pass through the opening, not a solid side wall.
        val entryT = if (paperPath.endY == paperPath.startY) 0f else
            ((opening.top - paperPath.startY) / (paperPath.endY - paperPath.startY)).coerceIn(0f, 1f)
        val entryX = paperPath.position(entryT).first
        require(entryX in opening.left..opening.right) {
            "Paper path must enter through the visible opening"
        }
        require(backLayerResource == null || backLayerResource.isNotBlank())
        require(frontLayerResource == null || frontLayerResource.isNotBlank())
        require(openingMaskResource == null || openingMaskResource.isNotBlank())
        if (released) {
            require(backLayerResource != null && frontLayerResource != null && openingMaskResource != null) {
                "Released artwork must have independently authored rear, front and opening mask assets"
            }
            if (origin == ArtworkOrigin.HUMAN_DESIGNED) {
                require(!sourceUrl.isNullOrBlank() && !licenseId.isNullOrBlank()) {
                    "Human-designed released assets require traceable source and license"
                }
            }
        }
    }
}

/** No new artworks are advertised until their actual resources have been bundled and tested. */
object BucketArtworkCatalog {
    private val premium = linkedMapOf<String, BucketArtworkSpec>()

    /** Stable identifiers for selectable layered studies, kept separate from premium releases. */
    private const val STUDY_PREFIX = "layer-study-"
    fun studyMaterial(id: String?): LayeredMaterialStudy? = when (id) {
        "${STUDY_PREFIX}walnut" -> LayeredMaterialStudy.WALNUT
        "${STUDY_PREFIX}zinc" -> LayeredMaterialStudy.ZINC
        "${STUDY_PREFIX}enamel" -> LayeredMaterialStudy.ENAMEL
        else -> null
    }
    fun studyId(material: LayeredMaterialStudy): String = when (material) {
        LayeredMaterialStudy.WALNUT -> "${STUDY_PREFIX}walnut"
        LayeredMaterialStudy.ZINC -> "${STUDY_PREFIX}zinc"
        LayeredMaterialStudy.ENAMEL -> "${STUDY_PREFIX}enamel"
    }

    fun find(id: String?): BucketArtworkSpec? = id?.let(premium::get)

    /** Legacy ids remain readable and writable, preserving existing preferences across updates. */
    fun isKnownDesignId(id: String): Boolean =
        findLabDesign(id) != null || premium.containsKey(id) || studyMaterial(id) != null || id == WICKER_REFERENCE_ID

    fun releasedDesigns(): List<BucketArtworkSpec> = premium.values.filter { it.released }

    /** A safe, non-destructive fallback when a previously selected asset is absent. */
    fun resolveLegacy(id: String?): LabDesign? = findLabDesign(id)
}
