package io.github.beankitk.numberbricks.core.geometry

interface GeometryProps {
    val config: GridConfig
}

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

inline fun emptyGridConfig(): GridConfig = GridConfig(0, 0, 0)