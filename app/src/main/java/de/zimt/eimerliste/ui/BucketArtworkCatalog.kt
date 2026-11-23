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

data class BucketArtworkSpec(
    val id: String,
    val displayName: String,
    val origin: ArtworkOrigin,
    val opening: ArtworkOpening,
    val labelY: Float,
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

    fun find(id: String?): BucketArtworkSpec? = id?.let(premium::get)

    /** Legacy ids remain readable and writable, preserving existing preferences across updates. */
    fun isKnownDesignId(id: String): Boolean =
        findLabDesign(id) != null || premium.containsKey(id)

    fun releasedDesigns(): List<BucketArtworkSpec> = premium.values.filter { it.released }

    /** A safe, non-destructive fallback when a previously selected asset is absent. */
    fun resolveLegacy(id: String?): LabDesign? = findLabDesign(id)
}
