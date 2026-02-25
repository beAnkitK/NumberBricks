package io.github.beankitk.numberbricks.core.geometry

interface GridSpec {
    val rows: Int
    val cols: Int
    val bricks: Int
}

fun GridSpec(rows: Int, cols: Int, bricks:Int): GridSpec {
    require(rows > 0) { "Rows must be greater than 0" }
    require(cols > 0) { "Cols must be greater than 0" }
    require(bricks > 0) { "Brick count must be greater than 0" }
    require(bricks <= rows * cols) {
        "Brick count cannot be greater than ${rows * cols}"
    }

    return object : GridSpec {
        override val rows = rows
        override val cols = cols
        override val bricks = bricks
    }
}

internal fun GridSpec.asString() = "GridSpec(rows = $rows, cols = $cols, bricks = $bricks)"