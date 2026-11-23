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

    @Test fun stableDesignLookupNeverAcceptsUnknownId() {
        labDesigns.forEach { assertEquals(it, findLabDesign(it.id)) }
        assertNull(findLabDesign(null))
        assertNull(findLabDesign("lab-8-0"))
        assertNull(findLabDesign("lab-0-6"))
    }
}
