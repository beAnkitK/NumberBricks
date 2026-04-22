package io.github.beankitk.numberbricks.core.geometry

import androidx.compose.ui.util.packInts

/**
 * Represents the canonical (row, column) cell position in the two-dimensional grid.
 *
 * @property packedValue The long value in which row and col are packed into.
 */
@JvmInline
value class Position(val packedValue: Long) {
    /** The non-negative row index of this position in the grid. */
    val row: Int
        get() = (packedValue shr 32).toInt()

    /** The non-negative column index of this position in the grid. */
    val col: Int
        get() = (packedValue and 0xFFFFFFFF).toInt()

    /**
     * Checks whether this position occupies the specified row, column, or exact cell.
     *
     * Unlike [equals], which requires an exact position match, this returns `true` if the position
     * lies on the specified row, column, or the exact `(row, col)` cell.
     *
     * @param row The row index to match, or `null` to ignore the row
     * @param col The column index to match, or `null` to ignore the column
     * @return `true` if this position occupies the given row, column, or exact cell
     * @throws IllegalArgumentException if both `row` and `col` are `null`
     */
    fun occupies(row: Int? = null, col: Int? = null): Boolean {
        require(row != null || col != null) {
            "Either 'row' or 'col' must be provided to check position occupancy."
        }
        return (row == this.row) || (col == this.col)
    }

    override fun toString() = "Position($row, $col)"

    companion object {
        /** Creates a position with both row and column set to zero */
        val Zero = Position(0x0L)
    }
}

/**
 * Creates a grid position from row and column indices.
 *
 * @param row The non-negative row index in the grid
 * @param col The non-negative column index in the grid
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Position(row: Int, col: Int) = Position(packInts(row, col))
