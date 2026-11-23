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
}
