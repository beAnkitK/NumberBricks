package io.github.beankitk.numberbricks.core.geometry

import io.github.beankitk.numberbricks.testing.DEFAULT_OFFSET
import io.github.beankitk.numberbricks.testing.DEFAULT_POSITION
import io.github.beankitk.numberbricks.testing.DEFAULT_SIZE
import io.github.beankitk.numberbricks.testing.TestDigitBuilder
import io.github.beankitk.numberbricks.testing.UniformOffset
import io.github.beankitk.numberbricks.testing.UniformPosition
import io.github.beankitk.numberbricks.testing.UniformSize
import io.github.beankitk.numberbricks.testing.createGridSpec
import io.github.beankitk.numberbricks.testing.createProps
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class NumberComposerTest {

    private val gridSpec = createGridSpec(5, 3, 13)
    private val props = createProps()

    private val digitBuilder =
        TestDigitBuilder(
            positionProvider = UniformPosition(DEFAULT_POSITION),
            offsetProvider = UniformOffset(DEFAULT_OFFSET),
            sizeProvider = UniformSize(DEFAULT_SIZE),
        )

    private fun createNumberComposer() =
        DefaultNumberComposer(
            digitGridSpec = gridSpec,
            geometryProps = props,
            digitBuilder = digitBuilder,
        )

    @Test
    fun testWhenNotInitialized_isUninitialized() {
        val composer = createNumberComposer()
        assertFalse(composer.isInitialized)
    }

    @Test
    fun testWhenNotInitialized_currentNumberAndUpdateNumber_throw() {
        val composer = createNumberComposer()

        assertFailsWith<IllegalStateException> { composer.currentNumber }
        assertFailsWith<IllegalStateException> { composer.updateNumber(5) }
    }

    @Test
    fun testWhenAlreadyInitialized_initiate_throws() {
        val composer = createNumberComposer()
        composer.initiate(0)

        assertTrue(composer.isInitialized)
        assertFailsWith<IllegalStateException> { composer.initiate(0) }
    }

    @Test
    fun testGivenValidInitialNumber_initiate_initializesComposer() {
        val composer = createNumberComposer()
        composer.initiate(456)

        assertEquals(456, composer.currentNumber)
        assertEquals(3, composer.getDigitCount())
        assertEquals(6, composer.getDigitSlotAt(0)!!.currentDigit)
        assertEquals(5, composer.getDigitSlotAt(1)!!.currentDigit)
        assertEquals(4, composer.getDigitSlotAt(2)!!.currentDigit)
    }

    @Test
    fun testIfIntMinValueIsGiven_initiateAndUpdateNumber_throws() {
        val composer = createNumberComposer()
        assertFailsWith<IllegalArgumentException> { composer.initiate(Int.MIN_VALUE) }

        composer.initiate(688)
        assertFailsWith<IllegalArgumentException> { composer.updateNumber(Int.MIN_VALUE) }
    }

    @Test
    fun testGivenNegativeNumber_numberUpdateUsesAbsoluteValue() {
        val composer = createNumberComposer()
        composer.initiate(-42)
        assertEquals(42, composer.currentNumber)

        composer.updateNumber(-183)
        assertEquals(183, composer.currentNumber)
        assertEquals(42, composer.previousNumber)
    }

    @Test
    fun testWhenNotInitialized_previousNumber_isNull() {
        val composer = createNumberComposer()
        assertNull(composer.previousNumber)
    }

    @Test
    fun testWhenInitializedWithInitialNumber_previousNumber_isNull() {
        val composer = createNumberComposer()
        composer.initiate(7975)

        assertNull(composer.previousNumber)
    }

    @Test
    fun testAfterFirstNumberUpdate_previousNumber_isInitialNumber() {
        val composer = createNumberComposer()
        composer.initiate(65)
        composer.updateNumber(98)

        assertEquals(65, composer.previousNumber)
    }

    @Test
    fun testUpdateNumber_setsCurrentNumberToNewNumber() {
        val composer = createNumberComposer()
        composer.initiate(245)
        composer.updateNumber(759)

        assertEquals(759, composer.currentNumber)
    }

    @Test
    fun testUpdateNumber_movesCurrentNumberToPrevious_andUpdatesCurrent() {
        val composer = createNumberComposer()
        composer.initiate(446)
        assertEquals(446, composer.currentNumber)

        composer.updateNumber(759)
        assertEquals(446, composer.previousNumber)
        assertEquals(759, composer.currentNumber)

        composer.updateNumber(1072)
        assertEquals(759, composer.previousNumber)
        assertEquals(1072, composer.currentNumber)
    }

    @Test
    fun testGivenIdenticalValue_updateNumber_ignoresNumberUpdate() {
        val composer = createNumberComposer()
        composer.initiate(7285)

        composer.updateNumber(7285)
        composer.updateNumber(-7285)

        assertNull(composer.previousNumber)
        assertEquals(7285, composer.currentNumber)
    }

    @Test
    fun testUpdateNumber_updatesAffectedDigitSlots() {
        val composer = createNumberComposer()
        composer.initiate(12)
        composer.updateNumber(13)

        val onesSlot = composer.getDigitSlotAt(0)!!
        val tensSlot = composer.getDigitSlotAt(1)!!

        assertEquals(3, onesSlot.currentDigit)
        assertEquals(2, onesSlot.previousDigit)
        assertEquals(1, tensSlot.currentDigit)
        assertNull(tensSlot.previousDigit)
    }

    @Test
    fun testWhenNotInitialised_getDigitCount_returnsZero() {
        val composer = createNumberComposer()
        assertEquals(0, composer.getDigitCount())
    }

    @Test
    fun testGetDigitCount_returnsCurrentNumberLength() {
        val composer = createNumberComposer()
        composer.initiate(123)
        assertEquals(3, composer.getDigitCount())

        composer.updateNumber(72929)
        assertEquals(5, composer.getDigitCount())
    }

    @Test
    fun testWhenNotInitialised_getDigitSlotAt_returnsNull() {
        val composer = createNumberComposer()
        assertNull(composer.getDigitSlotAt(0))
    }

    @Test
    fun testIfOutOfBoundsIndexFound_getDigitSlotAt_returnsNull() {
        val composer = createNumberComposer()
        composer.initiate(5)

        assertNull(composer.getDigitSlotAt(1))
        assertNull(composer.getDigitSlotAt(100))
    }

    @Test
    fun testGetDigitSlotAt_returnsDigitsFromLeastSignificantToMostSignificant() {
        val composer = createNumberComposer()
        composer.initiate(456)

        assertEquals(6, composer.getDigitSlotAt(0)?.currentDigit)
        assertEquals(5, composer.getDigitSlotAt(1)?.currentDigit)
        assertEquals(4, composer.getDigitSlotAt(2)?.currentDigit)

        composer.updateNumber(789)
        assertEquals(9, composer.getDigitSlotAt(0)?.currentDigit)
        assertEquals(8, composer.getDigitSlotAt(1)?.currentDigit)
        assertEquals(7, composer.getDigitSlotAt(2)?.currentDigit)
    }

    @Test
    fun testAfterNumberShrinks_getDigitSlotAt_returnsNullForRemovedSlots() {
        val composer = createNumberComposer()
        composer.initiate(100)
        composer.updateNumber(5)

        assertNull(composer.getDigitSlotAt(1))
        assertNull(composer.getDigitSlotAt(2))
    }

    @Test
    fun testWhenNotInitialised_getBricks_returnsNull() {
        val composer = createNumberComposer()
        assertNull(composer.getBricks(7))
    }

    @Test
    fun testIfInvalidDigitIsGiven_getBricks_throws() {
        val composer = createNumberComposer().also { it.initiate(0) }

        listOf(-1, 10, 100).forEach { digit ->
            assertFailsWith<IllegalArgumentException> { composer.getBricks(digit) }
        }
    }

    @Test
    fun testGetBricks_returnsBricksOnlyForEncounteredDigits() {
        val composer = createNumberComposer()
        composer.initiate(123)

        assertNotNull(composer.getBricks(1))
        assertNotNull(composer.getBricks(2))
        assertNotNull(composer.getBricks(3))
        assertEquals(gridSpec.brickCount, composer.getBricks(1)!!.size)

        assertNull(composer.getBricks(0))
        assertNull(composer.getBricks(9))
    }

    @Test
    fun testAfterDigitAppearsInNumber_getBricks_returnsBricks() {
        val composer = createNumberComposer()
        composer.initiate(1)
        assertNull(composer.getBricks(7))

        composer.updateNumber(7)
        assertNotNull(composer.getBricks(7))
    }

    @Test
    fun testWhenNumberIsUpdated_getBricks_retainsCachedDigits() {
        val composer = createNumberComposer()
        composer.initiate(1)
        composer.updateNumber(2)
        composer.updateNumber(67)

        assertNotNull(composer.getBricks(1))
        assertNotNull(composer.getBricks(2))
        assertNotNull(composer.getBricks(6))
        assertNotNull(composer.getBricks(7))
    }

    @Test
    fun testWhenNotInitialised_getDefaultBricks_returnsNull() {
        val composer = createNumberComposer()
        assertNull(composer.getDefaultBricks())
    }

    @Test
    fun testGetDefaultBricks_returnsSameDefaultBricksInstance() {
        val composer = createNumberComposer()
        composer.initiate(0)

        val first = composer.getDefaultBricks()
        val second = composer.getDefaultBricks()

        assertNotNull(first)
        assertSame(first, second)
        assertEquals(gridSpec.brickCount, first.size)
    }

    @Test
    fun testWhenNotInitialized_dispose_doesNothing() {
        val composer = createNumberComposer()
        composer.dispose()

        assertFalse(composer.isInitialized)
    }

    @Test
    fun testDispose_resetsComposerToUninitialized_andClearsState() {
        val composer = createNumberComposer()
        composer.initiate(9)
        composer.updateNumber(785)
        composer.dispose()

        assertFalse(composer.isInitialized)
        assertFailsWith<IllegalStateException> { composer.currentNumber }
        assertFailsWith<IllegalStateException> { composer.updateNumber(5) }
        assertNull(composer.previousNumber)
        assertEquals(0, composer.getDigitCount())
        assertNull(composer.getDigitSlotAt(0))
        assertNull(composer.getBricks(8))
        assertNull(composer.getDefaultBricks())
    }

    @Test
    fun testAfterDispose_composerCanBeInitializedAgainWithNewNumber() {
        val composer = createNumberComposer()
        composer.initiate(5)
        composer.dispose()
        assertFalse(composer.isInitialized)

        composer.initiate(789)
        assertTrue(composer.isInitialized)
        assertEquals(789, composer.currentNumber)
        assertEquals(3, composer.getDigitCount())
        assertNull(composer.previousNumber)
        assertNotNull(composer.getDefaultBricks())
    }
}
