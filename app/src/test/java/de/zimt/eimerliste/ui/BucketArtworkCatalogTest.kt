package de.zimt.eimerliste.ui

import org.junit.Assert.*
import org.junit.Test

class BucketArtworkCatalogTest {
    @Test fun legacyDesignIdsRemainValid() {
        assertEquals(48, labDesigns.size)
        assertTrue(BucketArtworkCatalog.isKnownDesignId("lab-0-0"))
        assertTrue(BucketArtworkCatalog.isKnownDesignId("lab-7-5"))
        assertFalse(BucketArtworkCatalog.isKnownDesignId("lab-8-0"))
        assertFalse(BucketArtworkCatalog.isKnownDesignId("premium-not-bundled"))
        assertEquals("lab-0-0", BucketArtworkCatalog.resolveLegacy("lab-0-0")?.id)
        assertNull(BucketArtworkCatalog.resolveLegacy("missing"))
    }

    @Test fun noUnverifiedPremiumDesignIsAdvertised() {
        assertTrue(BucketArtworkCatalog.releasedDesigns().isEmpty())
    }

    @Test fun openingGeometryMustBeValid() {
        assertThrows(IllegalArgumentException::class.java) {
            ArtworkOpening(0.9f, 0.1f, 0.2f, 0.8f)
        }
        assertThrows(IllegalArgumentException::class.java) {
            ArtworkOpening(0.1f, 0.9f, 0.8f, 0.2f)
        }
    }

    @Test fun cannotReleaseUnlayeredArtwork() {
        assertThrows(IllegalArgumentException::class.java) {
            BucketArtworkSpec(
                id = "premium-1", displayName = "Unfertig",
                origin = ArtworkOrigin.AI_GENERATED,
                opening = ArtworkOpening(.2f, .8f, .2f, .6f),
                labelY = .5f, released = true
            )
        }
    }

    @Test fun humanArtworkRequiresAttributionAndLicense() {
        assertThrows(IllegalArgumentException::class.java) {
            BucketArtworkSpec(
                id = "human-1", displayName = "Unbelegt",
                origin = ArtworkOrigin.HUMAN_DESIGNED,
                opening = ArtworkOpening(.2f, .8f, .2f, .6f),
                labelY = .5f, backLayerResource = "rear",
                frontLayerResource = "front", openingMaskResource = "mask",
                released = true
            )
        }
    }

    @Test fun paperPathIsClampedAndReversible() {
        val path = ArtworkPaperPath(.5f, .25f, .5f, .75f)
        assertEquals(.25f, path.position(-2f).second, .0001f)
        assertEquals(.5f, path.position(.5f).second, .0001f)
        assertEquals(.75f, path.position(3f).second, .0001f)
        assertEquals(path.position(0f), path.position(1f - 1f))
    }

    @Test fun paperIsAlwaysBehindFrontLayer() {
        assertEquals(
            listOf(ArtworkLayer.REAR, ArtworkLayer.PAPER, ArtworkLayer.FRONT, ArtworkLayer.LABEL),
            BUCKET_PAINT_ORDER
        )
    }

    @Test fun rejectPaperPathThatMissesOpening() {
        assertThrows(IllegalArgumentException::class.java) {
            BucketArtworkSpec(
                id = "test-missed-opening", displayName = "Unbrauchbar",
                origin = ArtworkOrigin.ORIGINAL_CONSTRUCTION,
                opening = ArtworkOpening(.2f, .8f, .2f, .4f),
                labelY = .5f,
                paperPath = ArtworkPaperPath(.5f, .8f, .5f, .9f)
            )
        }
    }

    @Test fun rejectPaperPathEnteringThroughSolidSideWall() {
        assertThrows(IllegalArgumentException::class.java) {
            BucketArtworkSpec(
                id = "test-side-wall", displayName = "Seitlicher Fehler",
                origin = ArtworkOrigin.ORIGINAL_CONSTRUCTION,
                opening = ArtworkOpening(.3f, .7f, .2f, .4f),
                labelY = .6f,
                paperPath = ArtworkPaperPath(.05f, .1f, .1f, .6f)
            )
        }
    }

    @Test fun allowPaperPathCrossingRealOpening() {
        val artwork = BucketArtworkSpec(
            id = "test-real-opening", displayName = "Korrekte Bahn",
            origin = ArtworkOrigin.ORIGINAL_CONSTRUCTION,
            opening = ArtworkOpening(.3f, .7f, .2f, .4f),
            labelY = .6f,
            paperPath = ArtworkPaperPath(.5f, .1f, .5f, .6f)
        )
        assertEquals(.5f, artwork.paperPath.position(.5f).first, .0001f)
    }
    @Test fun allIllustratedDesignsHaveUniqueStableIdsAndLayerResources() {
        assertTrue(illustratedBucketAssets.size >= 6)
        assertEquals(illustratedBucketAssets.size,
            illustratedBucketAssets.map { it.id }.toSet().size)
        illustratedBucketAssets.forEach { asset ->
            assertTrue(BucketArtworkCatalog.isKnownDesignId(asset.id))
            assertEquals(asset, findIllustratedBucket(asset.id))
            assertTrue(asset.rear != asset.front)
        }
    }

    @Test fun previousLegacySelectionAndWickerStayAvailable() {
        assertTrue(BucketArtworkCatalog.isKnownDesignId(WICKER_REFERENCE_ID))
        assertTrue(BucketArtworkCatalog.isKnownDesignId("lab-0-0"))
        assertTrue(BucketArtworkCatalog.isKnownDesignId("lab-7-5"))
        assertNull(findIllustratedBucket("lab-7-5"))
    }

    @Test fun importedHumanArtistAssetsHaveStableDistinctIds() {
        assertEquals(5, humanArtistBuckets.size)
        assertEquals(5, humanArtistBuckets.map { it.id }.toSet().size)
        humanArtistBuckets.forEach { asset ->
            assertTrue(BucketArtworkCatalog.isKnownDesignId(asset.id))
            assertEquals(asset, findHumanArtistBucket(asset.id))
            assertTrue(asset.imageRes != 0)
        }
        assertNull(findHumanArtistBucket("illustrated-astral-01"))
    }

}
