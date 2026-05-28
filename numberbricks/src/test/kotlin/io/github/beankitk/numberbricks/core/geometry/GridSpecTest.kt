package io.github.beankitk.numberbricks.core.geometry

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GridSpecTest {

    @Test
    fun testGivenValidValues_createsGridSpec() {
        val grid = GridSpec(rows = 3, cols = 2, brickCount = 5)

        assertEquals(3, grid.rows)
        assertEquals(2, grid.cols)
        assertEquals(5, grid.brickCount)
    }

    @Test
    fun testGivenBoundaryValues_createsGridSpec() {
        val minGrid = GridSpec(rows = 1, cols = 1, brickCount = 1)
        assertEquals(1, minGrid.rows)
        assertEquals(1, minGrid.cols)
        assertEquals(1, minGrid.brickCount)

        val maxBricks = GridSpec(rows = 5, cols = 4, brickCount = 20)
        assertEquals(20, maxBricks.brickCount)

        val sparseGrid = GridSpec(rows = 10, cols = 10, brickCount = 1)
        assertEquals(1, sparseGrid.brickCount)
    }

    @Test
    fun testIfRowsAreInvalid_throws() {
        assertFailsWith<IllegalArgumentException> { GridSpec(rows = 0, cols = 2, brickCount = 1) }
        assertFailsWith<IllegalArgumentException> { GridSpec(rows = -1, cols = 2, brickCount = 1) }
    }

    @Test
    fun testIfColsAreInvalid_throws() {
        assertFailsWith<IllegalArgumentException> { GridSpec(rows = 2, cols = 0, brickCount = 1) }
        assertFailsWith<IllegalArgumentException> { GridSpec(rows = 2, cols = -1, brickCount = 1) }
    }

    @Test
    fun testIfBrickCountIsInvalid_throws() {
        assertFailsWith<IllegalArgumentException> { GridSpec(rows = 3, cols = 3, brickCount = 0) }
        assertFailsWith<IllegalArgumentException> { GridSpec(rows = 3, cols = 3, brickCount = -1) }
        assertFailsWith<IllegalArgumentException> { GridSpec(rows = 2, cols = 3, brickCount = 7) }
    }

    @Test
    fun testAsString_formatsCorrectly() {
        val grid = GridSpec(rows = 5, cols = 3, brickCount = 12)

        assertEquals("GridSpec(rows = 5, cols = 3, brickCount = 12)", grid.asString())
    }

    @Test
    fun testGivenLargeGridCapacity_doesNotOverflow() {
        val largeGrid = GridSpec(rows = 100000, cols = 100000, brickCount = Int.MAX_VALUE)

        assertEquals(100000, largeGrid.rows)
        assertEquals(100000, largeGrid.cols)
        assertEquals(Int.MAX_VALUE, largeGrid.brickCount)
    }
}
