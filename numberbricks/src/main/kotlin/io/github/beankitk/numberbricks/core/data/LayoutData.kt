package io.github.beankitk.numberbricks.core.data

interface LayoutData {
    val rows: Int
    val cols: Int
    val brickCount: Int
}

inline fun LayoutData.asString() = "LayoutData(rows=$rows, cols=$cols, brickCount=$brickCount)"