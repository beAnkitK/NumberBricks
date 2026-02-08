package io.github.beankitk.numberbricks.core.geometry

/**
 * Defines the geometry configuration for digit construction.
 *
 * Provides immutable properties shared across all geometry providers and builders
 * to ensure consistent brick layouts. Owned by the number composer and passed to
 * builders during construction and providers via [GeometryProvider.attachWith]
 */
interface GeometryProps {
    /** The grid configuration defining layout dimensions and brick count. */
    val config: GridConfig
}

/**
 * Defines the grid dimensions and brick count for digit layouts.
 *
 * Specifies the logical grid structure that digits are constructed within.
 * The brick count may be less than or equal to the total grid cells (rows × cols),
 * allowing for sparse layouts where not all grid positions contain bricks.
 *
 * @property rows Number of rows in the digit grid (must be non-negative)
 * @property cols Number of columns in the digit grid (must be non-negative)
 * @property bricks Total number of bricks used in the digit (must be ≤ rows × cols)
 *
 * @throws IllegalArgumentException if any dimension is negative or brick count exceeds grid capacity
 */
data class GridConfig(
    val rows: Int,
    val cols: Int,
    val bricks: Int
) {
    init {
        require(rows >= 0) { "Rows must be non-negative" }
        require(cols >= 0) { "Cols must be non-negative" }
        require(bricks >= 0) { "Brick count must be non-negative" }
        require(bricks <= rows * cols) {
            "Brick count cannot be greater than ${rows * cols}"
        }
    }
}

/**
 * Creates an empty grid configuration with zero dimensions.
 *
 * Useful as a placeholder or initial state before actual configuration is available.
 *
 * @return A [GridConfig] with 0 rows, 0 columns, and 0 bricks
 */
inline fun emptyGridConfig(): GridConfig = GridConfig(0, 0, 0)