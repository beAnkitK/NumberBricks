package io.github.beankitk.numberbricks.core.layout

interface LayoutInfo {

    val rowsCount: Int
    val colsCount: Int
    val brickCount: Int

    companion object {
        fun of(
            rows: Int,
            cols: Int,
            bricks: Int
        ): LayoutInfo = LayoutInfoImpl(
            rowsCount = rows,
            colsCount = cols,
            brickCount = bricks
        )
    }
}

private data class LayoutInfoImpl(
    override val rowsCount: Int,
    override val colsCount: Int,
    override val brickCount: Int
) : LayoutInfo {

    init {
        require(rowsCount > 0) { "Rows must be positive" }
        require(colsCount > 0) { "Cols must be positive" }
        require(brickCount > 0) { "Brick count must be positive" }
        require(brickCount <= rowsCount * colsCount) {
            "Brick count cannot be greater than rows * cols"
        }
    }
}