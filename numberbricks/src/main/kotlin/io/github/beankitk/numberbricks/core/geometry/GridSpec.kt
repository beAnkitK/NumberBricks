package io.github.beankitk.numberbricks.core.geometry

/**
 * Defines the grid constraints used for digit geometry.
 *
 * Specifies the logical grid in which a digit geometry is composed. The grid is defined by its row
 * and column count, while [brickCount] determines how many cells are occupied within that grid.
 *
 * The number of bricks may be less than or equal to the total number of cells (`rows * cols`),
 * allowing sparse layouts where some grid positions are empty.
 *
 * Preconditions:
 * - `rows > 0`
 * - `cols > 0`
 * - `brickCount > 0`
 * - `brickCount <= rows * cols`
 *
 * @property rows Number of rows in the grid
 * @property cols Number of columns in the grid
 * @property brickCount Number of bricks used in the grid
 */
interface GridSpec {
    val rows: Int
    val cols: Int
    val brickCount: Int
}

/**
 * Creates a [GridSpec] with the provided dimensions and validates its constraints.
 *
 * @param rows Number of rows in the grid. Must be greater than 0
 * @param cols Number of columns in the grid. Must be greater than 0
 * @param brickCount Number of bricks in the grid. Must be greater than 0 and less than or equal to
 *   `rows * cols`
 * @throws IllegalArgumentException if any constraint is violated
 */
fun GridSpec(rows: Int, cols: Int, brickCount: Int): GridSpec {
    require(rows > 0) { "Rows must be greater than 0" }
    require(cols > 0) { "Cols must be greater than 0" }
    require(brickCount > 0) { "Brick count must be greater than 0" }
    require(validateBrickCount(rows, cols, brickCount)) { "Brick count cannot be greater than ${rows * cols}" }

    return object : GridSpec {
        override val rows = rows
        override val cols = cols
        override val brickCount = brickCount
    }
}

internal fun GridSpec.asString() = "GridSpec(rows = $rows, cols = $cols, brickCount = $brickCount)"

private fun validateBrickCount(rows: Int, cols: Int, brickCount: Int): Boolean {
    val q = brickCount / rows
    return q < cols || (q == cols && brickCount % rows == 0)
}
