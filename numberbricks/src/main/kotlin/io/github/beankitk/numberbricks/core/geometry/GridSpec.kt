package io.github.beankitk.numberbricks.core.geometry

interface GridSpec {
    val rows: Int
    val cols: Int
    val brickCount: Int
}

fun GridSpec(rows: Int, cols: Int, brickCount: Int): GridSpec {
    require(rows > 0) { "Rows must be greater than 0" }
    require(cols > 0) { "Cols must be greater than 0" }
    require(brickCount > 0) { "Brick count must be greater than 0" }
    require(brickCount <= rows * cols) {
        "Brick count cannot be greater than ${rows * cols}"
    }

    return object : GridSpec {
        override val rows = rows
        override val cols = cols
        override val brickCount = brickCount
    }
}

internal fun GridSpec.asString() = "GridSpec(rows = $rows, cols = $cols, brickCount = $brickCount)"