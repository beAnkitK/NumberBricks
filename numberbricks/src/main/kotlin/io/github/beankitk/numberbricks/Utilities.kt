package io.github.beankitk.numberbricks

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Density

/** Helper: compute targets for a digit in pixels (same layout as original but px units) */
internal fun computeTargetsForDigitPx(digit: Int, cellPx: Float): List<Offset> {
    val x0 = 0f
    val x1 = cellPx
    val x2 = cellPx * 2f

    val y0 = 0f
    val y1 = cellPx
    val y2 = cellPx * 2f
    val y3 = cellPx * 3f
    val y4 = cellPx * 4f

    val default = MutableList(13) { Offset(cellPx, cellPx * 2f) }
    fun set(i: Int, x: Float, y: Float) { default[i] = Offset(x, y) }

    when (digit) {
        1 -> {
            for (i in 0..12) set(i, x2, default[i].y)
            set(0, x2, y0); set(1, x2, y0); set(2, x2, y0)
            set(3, x2, y1); set(4, x2, y1)
            set(5, x2, y2); set(6, x2, y2); set(7, x2, y2)
            set(8, x2, y3); set(9, x2, y3)
            set(10, x2, y4); set(11, x2, y4); set(12, x2, y4)
        }
        2 -> {
            set(0, x0, y0); set(1, x1, y0); set(2, x2, y0)
            set(3, x2, y1); set(4, x2, y1)
            set(5, x0, y2); set(6, x1, y2); set(7, x2, y2)
            set(8, x0, y3); set(9, x0, y3)
            set(10, x0, y4); set(11, x1, y4); set(12, x2, y4)
        }
        3 -> {
            set(0, x0, y0); set(1, x1, y0); set(2, x2, y0)
            set(3, x2, y1); set(4, x2, y1)
            set(5, x0, y2); set(6, x1, y2); set(7, x2, y2)
            set(8, x2, y3); set(9, x2, y3)
            set(10, x0, y4); set(11, x1, y4); set(12, x2, y4)
        }
        4 -> {
            set(0, x0, y0); set(1, x0, y0); set(2, x2, y0)
            set(3, x0, y1); set(4, x2, y1)
            set(5, x0, y2); set(6, x1, y2); set(7, x2, y2)
            set(8, x2, y3); set(9, x2, y3)
            set(10, x2, y4); set(11, x2, y4); set(12, x2, y4)
        }
        5 -> {
            set(0, x0, y0); set(1, x1, y0); set(2, x2, y0)
            set(3, x0, y1); set(4, x2, y0)
            set(5, x0, y2); set(6, x1, y2); set(7, x2, y2)
            set(8, x2, y3); set(9, x2, y3)
            set(10, x0, y4); set(11, x1, y4); set(12, x2, y4)
        }
        6 -> {
            set(0, x0, y0); set(1, x1, y0); set(2, x2, y0)
            set(3, x0, y1); set(4, x2, y0)
            set(5, x0, y2); set(6, x1, y2); set(7, x2, y2)
            set(8, x0, y3); set(9, x2, y3)
            set(10, x0, y4); set(11, x1, y4); set(12, x2, y4)
        }
        7 -> {
            set(0, x0, y0); set(1, x1, y0); set(2, x2, y0)
            set(3, x2, y1); set(4, x2, y1)
            set(5, x2, y2); set(6, x2, y2); set(7, x2, y2)
            set(8, x2, y3); set(9, x2, y3)
            set(10, x2, y4); set(11, x2, y4); set(12, x2, y4)
        }
        8 -> {
            set(0, x0, y0); set(1, x1, y0); set(2, x2, y0)
            set(3, x0, y1); set(4, x2, y1)
            set(5, x0, y2); set(6, x1, y2); set(7, x2, y2)
            set(8, x0, y3); set(9, x2, y3)
            set(10, x0, y4); set(11, x1, y4); set(12, x2, y4)
        }
        9 -> {
            set(0, x0, y0); set(1, x1, y0); set(2, x2, y0)
            set(3, x0, y1); set(4, x2, y1)
            set(5, x0, y2); set(6, x1, y2); set(7, x2, y2)
            set(8, x0, y4) // as original note said
            set(9, x2, y3)
            set(10, x0, y4); set(11, x1, y4); set(12, x2, y4)
        }
        else -> {
            set(0, x0, y0); set(1, x1, y0); set(2, x2, y0)
            set(3, x0, y1); set(4, x2, y1)
            set(5, x0, y2); set(6, x0, y2); set(7, x2, y2)
            set(8, x0, y3); set(9, x2, y3)
            set(10, x0, y4); set(11, x1, y4); set(12, x2, y4)
        }
    }
    return default
}

/** Create an ImageBitmap for the given digit (draws all bricks at their target positions). */
internal fun createBitmapForDigit(
    widthPx: Int,
    heightPx: Int,
    targets: List<Offset>,
    cellSize: Float,
    color: Color,
    density: Density
): ImageBitmap {
    // Avoid 0-size bitmaps
    val w = maxOf(1, widthPx)
    val h = maxOf(1, heightPx)
    val image = ImageBitmap(w, h)
    val canvas = Canvas(image)
    val paint = Paint().apply { this.color = color }

    // draw background nothing; draw all bricks as rects
    for (i in 0 until targets.size.coerceAtMost(13)) {
        val off = targets[i]
        canvas.drawRect(
            left = off.x,
            top = off.y,
            right = off.x + cellSize,
            bottom = off.y + cellSize,
            paint = paint
        )
    }
    return image
}

/** Draw an already-created ImageBitmap quickly */
internal fun DrawScope.drawImageCached(bitmap: ImageBitmap) {
    val paint = Paint()
    drawIntoCanvas { canvas ->
        canvas.drawImage(bitmap, topLeftOffset = Offset.Zero, paint = paint)
    }
}