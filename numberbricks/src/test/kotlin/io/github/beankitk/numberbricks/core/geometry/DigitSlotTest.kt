package io.github.beankitk.numberbricks.core.geometry

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull

class DigitSlotTest {

    @Test
    fun testGivenValidDigits_createsDigitSlot() {
        val noPrevious = DigitSlot(currentDigit = 5)
        assertNull(noPrevious.previousDigit)
        assertEquals(5, noPrevious.currentDigit)

        val bothDigits = DigitSlot(previousDigit = 3, currentDigit = 7)
        assertEquals(3, bothDigits.previousDigit)
        assertEquals(7, bothDigits.currentDigit)

        val boundaryDigits = DigitSlot(previousDigit = 0, currentDigit = 9)
        assertEquals(0, boundaryDigits.previousDigit)
        assertEquals(9, boundaryDigits.currentDigit)
    }

    @Test
    fun testWhenCreatedWithCurrentDigit_previousDigit_isNull() {
        val slot = DigitSlot(currentDigit = 5)
        assertNull(slot.previousDigit)
    }

    @Test
    fun testIfInvalidDigitIsGiven_throws() {
        val invalidInputs =
            listOf(
                { DigitSlot(currentDigit = -1) },
                { DigitSlot(currentDigit = 10) },
                { DigitSlot(previousDigit = -1, currentDigit = 5) },
                { DigitSlot(previousDigit = 10, currentDigit = 5) },
                { DigitSlot(previousDigit = 5, currentDigit = -1) },
                { DigitSlot(previousDigit = 5, currentDigit = 10) },
            )

        invalidInputs.forEach { creation ->
            assertFailsWith<IllegalArgumentException> { creation() }
        }
    }

    @Test
    fun testWithCurrent_createsSlot_withCurrentAsPreviousAndNewAsCurrent() {
        val slot = DigitSlot(previousDigit = 1, currentDigit = 2)

        val updated = slot.withCurrent(3)
        assertEquals(2, updated.previousDigit)
        assertEquals(3, updated.currentDigit)

        val minBoundary = slot.withCurrent(0)
        assertEquals(2, minBoundary.previousDigit)
        assertEquals(0, minBoundary.currentDigit)

        val maxBoundary = slot.withCurrent(9)
        assertEquals(2, maxBoundary.previousDigit)
        assertEquals(9, maxBoundary.currentDigit)
    }

    @Test
    fun testIfInvalidDigitIsGiven_withCurrent_throws() {
        val slot = DigitSlot(currentDigit = 5)

        assertFailsWith<IllegalArgumentException> { slot.withCurrent(-1) }
        assertFailsWith<IllegalArgumentException> { slot.withCurrent(10) }
    }

    @Test
    fun testAfterMultipleUpdates_withCurrent_preservesPreviousAndCurrentDigit() {
        val chained = DigitSlot(currentDigit = 5).withCurrent(6).withCurrent(7)

        assertEquals(6, chained.previousDigit)
        assertEquals(7, chained.currentDigit)
    }

    @Test
    fun testIsSame_returnsWhetherDigitsAreEqual() {
        val cases =
            listOf(
                DigitSlot(previousDigit = 5, currentDigit = 5) to true,
                DigitSlot(previousDigit = 3, currentDigit = 7) to false,
                DigitSlot(currentDigit = 5) to false,
                DigitSlot(previousDigit = 0, currentDigit = 0) to true,
                DigitSlot(previousDigit = 9, currentDigit = 9) to true,
            )

        cases.forEach { (slot, expected) -> assertEquals(expected, slot.isSame()) }
    }

    @Test
    fun testGivenPackedValue_from_reconstructsDigitSlot() {
        val cases =
            listOf(
                DigitSlot(currentDigit = 7),
                DigitSlot(previousDigit = 2, currentDigit = 8),
                DigitSlot(previousDigit = 0, currentDigit = 0),
                DigitSlot(previousDigit = 9, currentDigit = 9),
            )

        cases.forEach { original ->
            val reconstructed = DigitSlot.from(original.packed)

            assertEquals(original.previousDigit, reconstructed.previousDigit)
            assertEquals(original.currentDigit, reconstructed.currentDigit)
        }
    }

    @Test
    fun testIfPackedValueIsInvalid_from_throws() {
        val invalidPacked = (5L shl 32) or 10L

        assertFailsWith<IllegalArgumentException> { DigitSlot.from(invalidPacked) }
    }

    @Test
    fun testToString_formatsCorrectly() {
        assertEquals(
            "[curr = 7, prev = 3]",
            DigitSlot(previousDigit = 3, currentDigit = 7).toString(),
        )

        assertEquals("[curr = 5, prev = null]", DigitSlot(currentDigit = 5).toString())
    }

    @Test
    fun testAfterMultipleUpdates_packedValuePreservesState() {
        val slot = DigitSlot(currentDigit = 1).withCurrent(2).withCurrent(3).withCurrent(4)

        val reconstructed = DigitSlot.from(slot.packed)

        assertEquals(3, reconstructed.previousDigit)
        assertEquals(4, reconstructed.currentDigit)
    }
}
