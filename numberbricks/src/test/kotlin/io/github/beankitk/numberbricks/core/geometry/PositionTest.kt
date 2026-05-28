package io.github.beankitk.numberbricks.core.geometry

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PositionTest {

    @Test
    fun testGivenValidCoordinates_createsPosition() {
        val origin = Position(0, 0)
        assertEquals(0, origin.row)
        assertEquals(0, origin.col)

        val typical = Position(3, 5)
        assertEquals(3, typical.row)
        assertEquals(5, typical.col)

        val largeIndices = Position(1000, 2000)
        assertEquals(1000, largeIndices.row)
        assertEquals(2000, largeIndices.col)
    }

    @Test
    fun testPositionZero_isOrigin() {
        assertEquals(0, Position.Zero.row)
        assertEquals(0, Position.Zero.col)
    }

    @Test
    fun testOccupies_matchesRowOrColumn() {
        val pos = Position(3, 5)

        assertTrue(pos.occupies(row = 3))
        assertTrue(pos.occupies(col = 5))
        assertTrue(pos.occupies(row = 3, col = 5))
        assertTrue(pos.occupies(row = 3, col = 7))
        assertTrue(pos.occupies(row = 1, col = 5))

        assertFalse(pos.occupies(row = 1))
        assertFalse(pos.occupies(col = 7))
        assertFalse(pos.occupies(row = 1, col = 7))
    }

    @Test
    fun testIfNoRowOrColumnIsGiven_throws() {
        val pos = Position(3, 5)

        assertFailsWith<IllegalArgumentException> { pos.occupies(row = null, col = null) }
    }

    @Test
    fun testToString_formatsCorrectly() {
        assertEquals("Position(0, 0)", Position.Zero.toString())
        assertEquals("Position(3, 5)", Position(3, 5).toString())
        assertEquals("Position(10, 20)", Position(10, 20).toString())
    }

    @Test
    fun testGivenPackedValue_reconstructsSamePosition() {
        val positions =
            listOf(Position(0, 0), Position(3, 5), Position(100, 200), Position(999, 1234))

        positions.forEach { original ->
            val reconstructed = Position(original.packedValue)
            assertEquals(original.row, reconstructed.row)
            assertEquals(original.col, reconstructed.col)
        }
    }

    @Test
    fun testEqualPositions_areEqual() {
        val pos1 = Position(3, 5)
        val pos2 = Position(3, 5)
        val pos3 = Position(3, 7)

        assertEquals(pos1, pos2)
        assertFalse(pos1 == pos3)
    }
}
