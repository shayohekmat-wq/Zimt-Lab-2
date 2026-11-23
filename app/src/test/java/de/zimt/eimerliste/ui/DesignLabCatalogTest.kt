package de.zimt.eimerliste.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DesignLabCatalogTest {
    @Test fun catalogHasEightStylesAndSixDistinctVariantsEach() {
        assertEquals(48, labDesigns.size)
        assertEquals(48, labDesigns.map { it.id }.toSet().size)
        assertEquals((0..7).toSet(), labDesigns.map { it.style }.toSet())
        (0..7).forEach { style ->
            val group = labDesigns.filter { it.style == style }
            assertEquals(6, group.size)
            assertEquals(6, group.map { it.name }.toSet().size)
            assertEquals((0..5).toSet(), group.map { it.variant }.toSet())
        }
    }

    @Test fun visualProfilesHaveValidAnchorsAndStableIds() {
        labDesigns.forEach { design ->
            val profile = visualProfile(design)
            assertEquals(design.id, profile.designId)
            assertTrue(profile.openingLeft >= 0f)
            assertTrue(profile.openingRight <= 1f)
            assertTrue(profile.openingLeft < profile.openingRight)
            assertTrue(profile.openingY in 0f..1f)
            assertTrue(profile.labelY in 0f..1f)
        }
    }

    @Test fun lowPolyOpeningAnchorsReflectDifferentConstructions() {
        val openings = labDesigns.filter { it.style == 4 }
            .map { visualProfile(it).openingY }
        assertTrue(openings.toSet().size >= 3)
    }

    @Test fun stableDesignLookupNeverAcceptsUnknownId() {
        labDesigns.forEach { assertEquals(it, findLabDesign(it.id)) }
        assertNull(findLabDesign(null))
        assertNull(findLabDesign("lab-8-0"))
        assertNull(findLabDesign("lab-0-6"))
    }
}
