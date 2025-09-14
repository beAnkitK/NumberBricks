package io.github.beankitk.numberbricks

internal fun targetsForDigit(digit: Int, cellPx: Float): Pair<FloatArray, FloatArray> {
    val xs = FloatArray(13) { cellPx }
    val ys = FloatArray(13) { cellPx * 2f }
    
    val x0 = 0f
    val x1 = cellPx
    val x2 = cellPx * 2f
    val y0 = 0f
    val y1 = cellPx
    val y2 = cellPx * 2f
    val y3 = cellPx * 3f
    val y4 = cellPx * 4f

    when (digit) {
        1 -> {
            for (i in 0..12) xs[i] = x2
            ys[0] = y0; ys[1] = y0; ys[2] = y0
            ys[3] = y1; ys[4] = y1
            ys[5] = y2; ys[6] = y2; ys[7] = y2
            ys[8] = y3; ys[9] = y3
            ys[10] = y4; ys[11] = y4; ys[12] = y4
        }

        2 -> {
            xs[0] = x0; ys[0] = y0
            xs[1] = x1; ys[1] = y0
            xs[2] = x2; ys[2] = y0
            xs[3] = x2; ys[3] = y1
            xs[4] = x2; ys[4] = y1
            xs[5] = x0; ys[5] = y2
            xs[6] = x1; ys[6] = y2
            xs[7] = x2; ys[7] = y2
            xs[8] = x0; ys[8] = y3
            xs[9] = x0; ys[9] = y3
            xs[10] = x0; ys[10] = y4
            xs[11] = x1; ys[11] = y4
            xs[12] = x2; ys[12] = y4
        }

        3 -> {
            xs[0] = x0; ys[0] = y0
            xs[1] = x1; ys[1] = y0
            xs[2] = x2; ys[2] = y0
            xs[3] = x2; ys[3] = y1
            xs[4] = x2; ys[4] = y1
            xs[5] = x0; ys[5] = y2
            xs[6] = x1; ys[6] = y2
            xs[7] = x2; ys[7] = y2
            xs[8] = x2; ys[8] = y3
            xs[9] = x2; ys[9] = y3
            xs[10] = x0; ys[10] = y4
            xs[11] = x1; ys[11] = y4
            xs[12] = x2; ys[12] = y4
        }

        4 -> {
            xs[0] = x0; ys[0] = y0
            xs[1] = x0; ys[1] = y0
            xs[2] = x2; ys[2] = y0
            xs[3] = x0; ys[3] = y1
            xs[4] = x2; ys[4] = y1
            xs[5] = x0; ys[5] = y2
            xs[6] = x1; ys[6] = y2
            xs[7] = x2; ys[7] = y2
            xs[8] = x2; ys[8] = y3
            xs[9] = x2; ys[9] = y3
            xs[10] = x2; ys[10] = y4
            xs[11] = x2; ys[11] = y4
            xs[12] = x2; ys[12] = y4
        }

        5 -> {
            xs[0] = x0; ys[0] = y0
            xs[1] = x1; ys[1] = y0
            xs[2] = x2; ys[2] = y0
            xs[3] = x0; ys[3] = y1
            xs[4] = x2; ys[4] = y0
            xs[5] = x0; ys[5] = y2
            xs[6] = x1; ys[6] = y2
            xs[7] = x2; ys[7] = y2
            xs[8] = x2; ys[8] = y3
            xs[9] = x2; ys[9] = y3
            xs[10] = x0; ys[10] = y4
            xs[11] = x1; ys[11] = y4
            xs[12] = x2; ys[12] = y4
        }

        6 -> {
            xs[0] = x0; ys[0] = y0
            xs[1] = x1; ys[1] = y0
            xs[2] = x2; ys[2] = y0
            xs[3] = x0; ys[3] = y1
            xs[4] = x2; ys[4] = y0
            xs[5] = x0; ys[5] = y2
            xs[6] = x1; ys[6] = y2
            xs[7] = x2; ys[7] = y2
            xs[8] = x0; ys[8] = y3
            xs[9] = x2; ys[9] = y3
            xs[10] = x0; ys[10] = y4
            xs[11] = x1; ys[11] = y4
            xs[12] = x2; ys[12] = y4
        }

        7 -> {
            xs[0] = x0; ys[0] = y0
            xs[1] = x1; ys[1] = y0
            xs[2] = x2; ys[2] = y0
            xs[3] = x2; ys[3] = y1
            xs[4] = x2; ys[4] = y1
            xs[5] = x2; ys[5] = y2
            xs[6] = x2; ys[6] = y2
            xs[7] = x2; ys[7] = y2
            xs[8] = x2; ys[8] = y3
            xs[9] = x2; ys[9] = y3
            xs[10] = x2; ys[10] = y4
            xs[11] = x2; ys[11] = y4
            xs[12] = x2; ys[12] = y4
        }

        8 -> {
            xs[0] = x0; ys[0] = y0
            xs[1] = x1; ys[1] = y0
            xs[2] = x2; ys[2] = y0
            xs[3] = x0; ys[3] = y1
            xs[4] = x2; ys[4] = y1
            xs[5] = x0; ys[5] = y2
            xs[6] = x1; ys[6] = y2
            xs[7] = x2; ys[7] = y2
            xs[8] = x0; ys[8] = y3
            xs[9] = x2; ys[9] = y3
            xs[10] = x0; ys[10] = y4
            xs[11] = x1; ys[11] = y4
            xs[12] = x2; ys[12] = y4
        }

        9 -> {
            xs[0] = x0; ys[0] = y0
            xs[1] = x1; ys[1] = y0
            xs[2] = x2; ys[2] = y0
            xs[3] = x0; ys[3] = y1
            xs[4] = x2; ys[4] = y1
            xs[5] = x0; ys[5] = y2
            xs[6] = x1; ys[6] = y2
            xs[7] = x2; ys[7] = y2
            xs[8] = x0; ys[8] = y4
            xs[9] = x2; ys[9] = y3
            xs[10] = x0; ys[10] = y4
            xs[11] = x1; ys[11] = y4
            xs[12] = x2; ys[12] = y4
        }

        else -> {
            xs[0] = x0; ys[0] = y0
            xs[1] = x1; ys[1] = y0
            xs[2] = x2; ys[2] = y0
            xs[3] = x0; ys[3] = y1
            xs[4] = x2; ys[4] = y1
            xs[5] = x0; ys[5] = y2
            xs[6] = x0; ys[6] = y2
            xs[7] = x2; ys[7] = y2
            xs[8] = x0; ys[8] = y3
            xs[9] = x2; ys[9] = y3
            xs[10] = x0; ys[10] = y4
            xs[11] = x1; ys[11] = y4
            xs[12] = x2; ys[12] = y4
        }
    }

    return Pair(xs, ys)
}