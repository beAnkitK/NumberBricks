package io.github.beankitk.numberbricks

import androidx.compose.ui.geometry.Offset

internal fun computeTargetOffsetsFor(digit: Int, cellPx: Float): Array<Offset> {
    val layout = DIGIT_LAYOUTS.getOrElse(digit) { DIGIT_LAYOUTS[10] }
    return Array(13) { i ->
        val xIndex = layout[i * 2].toInt()
        val yIndex = layout[i * 2 + 1].toInt()
        Offset(xIndex * cellPx, yIndex * cellPx)
    }
}

private val DIGIT_LAYOUTS: Array<ByteArray> = arrayOf(
    // digit 0
    byteArrayOf(
        0,0, 1,0, 2,0,
        0,1, 2,1,
        0,2, 0,2, 2,2,
        0,3, 2,3,
        0,4, 1,4, 2,4
    ),
    // digit 1
    byteArrayOf(
        2,0, 2,0, 2,0,
        2,1, 2,1,
        2,2, 2,2, 2,2,
        2,3, 2,3,
        2,4, 2,4, 2,4
    ),
    // digit 2
    byteArrayOf(
        0,0, 1,0, 2,0,
        2,1, 2,1,
        0,2, 1,2, 2,2,
        0,3, 0,3,
        0,4, 1,4, 2,4
    ),
    // digit 3
    byteArrayOf(
        0,0, 1,0, 2,0,
        2,1, 2,1,
        0,2, 1,2, 2,2,
        2,3, 2,3,
        0,4, 1,4, 2,4
    ),
    // digit 4
    byteArrayOf(
        0,0, 2,0, 2,0,
        0,1, 2,1,
        0,2, 1,2, 2,2,
        2,3, 2,3,
        2,4, 2,4, 2,4
    ),
    // digit 5
    byteArrayOf(
        0,0, 1,0, 2,0,
        0,1, 2,0,
        0,2, 1,2, 2,2,
        2,3, 2,3,
        0,4, 1,4, 2,4
    ),
    // digit 6
    byteArrayOf(
        0,0, 1,0, 2,0,
        0,1, 2,0,
        0,2, 1,2, 2,2,
        0,3, 2,3,
        0,4, 1,4, 2,4
    ),
    // digit 7
    byteArrayOf(
        0,0, 1,0, 2,0,
        2,1, 2,1,
        2,2, 2,2, 2,2,
        2,3, 2,3,
        2,4, 2,4, 2,4
    ),
    // digit 8
    byteArrayOf(
        0,0, 1,0, 2,0,
        0,1, 2,1,
        0,2, 1,2, 2,2,
        0,3, 2,3,
        0,4, 1,4, 2,4
    ),
    // digit 9
    byteArrayOf(
        0,0, 1,0, 2,0,
        0,1, 2,1,
        0,2, 1,2, 2,2,
        0,4, 2,3,
        0,4, 1,4, 2,4
    ),
    //default at center 1,2 
    byteArrayOf(
        1,2, 1,2, 1,2,
        1,2, 1,2, 
        1,2, 1,2, 1,2,
        1,2, 1,2,
        1,2, 1,2, 1,2,
    )
    
)