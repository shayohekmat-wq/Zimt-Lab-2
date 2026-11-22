package de.zimt.eimerliste.domain

import org.junit.Assert.*
import org.junit.Test

class ShakeDecisionTest {
    @Test fun twoIndependentPeaksTriggerOnce() {
        val shake = ShakeDecision()
        assertFalse(shake.observePeak(3f,1000,10f))
        assertFalse(shake.observePeak(14f,1100,10f))
        assertFalse(shake.observePeak(15f,1150,10f))
        assertTrue(shake.observePeak(14f,1370,10f))
        assertFalse(shake.observePeak(16f,1660,10f))
    }
    @Test fun slowSeparatedPeaksDoNotTrigger() {
        val shake = ShakeDecision()
        assertFalse(shake.observePeak(15f,1000,10f))
        assertFalse(shake.observePeak(15f,2100,10f))
    }
}
