package io.github.beankitk.numberbricks.core.layout

interface LayoutProperties {
    val config: LayoutConfig
}

class DefaultLayoutProperties(
    rows: Int, cols: Int, bricks: Int
): LayoutProperties {
    override val config: LayoutConfig = LayoutConfig.of(
        rows = rows, cols = cols, bricks = bricks
    )
}

class LayoutConfig private constructor(
    rows: Int, cols: Int, bricks: Int
) {
    val rows: Int = rows
    val cols: Int = cols
    val bricks: Int = bricks

    override fun toString() = "(rows=$rows, cols=$cols, bricks=$bricks)"

    companion object {
        fun of(rows: Int, cols: Int, bricks: Int): LayoutConfig {
            require(rows >= 0) { "Rows must be positive" }
            require(cols >= 0) { "Cols must be positive" }
            require(bricks >= 0) { "Brick count must be positive" }
            require(bricks <= rows * cols) { "Brick count cannot be greater than ${rows * cols}" }
            return LayoutConfig(rows = rows, cols = cols, bricks = bricks)
        }
    }

}

inline fun emptyLayoutConfig(): LayoutConfig = LayoutConfig.of(0, 0, 0)