package io.github.beankitk.numberbricks.core.geometry

import androidx.compose.ui.util.packInts

/**
 * Represents the canonical (row, column) cell position in the two-dimensional grid.
 *
 * @property packedValue The long value in which row and col are packed into.
 */
@JvmInline
value class Position(val packedValue: Long) {
    /**
     * The non-negative row index of this position in the grid.
     */
    val row: Int
        get() = (packedValue shr 32).toInt()

    /**
     * The non-negative column index of this position in the grid.
     */
    val col: Int
        get() = (packedValue and 0xFFFFFFFF).toInt()

    override fun toString() = "Position($row, $col)"

    companion object {
        /** Creates a position with both row and column set to zero*/
        val Zero = Position(0x0L)
    }
}

/**
 * Creates a grid position from row and column indices.
 *
 * @param row The non-negative row index in the grid
 * @param column The non-negative column index in the grid
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Position(row: Int, col: Int) = Position(packInts(row, col))