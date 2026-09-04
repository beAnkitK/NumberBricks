package io.github.beankitk.numberbricks.testing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.geometry.Brick
import io.github.beankitk.numberbricks.core.geometry.Position

/** Simple [Brick] implementation for testing. */
data class TestBrick(
    override val index: Int,
    override val position: Position,
    override val offset: Offset,
    override val size: Size,
) : Brick<TestBrick> {

    private val asRect = Rect(offset, size)

    override fun scaledBy(totalSize: Size, brickSize: Size): TestBrick {
        val width = brickSize.width
        val height = brickSize.height

        return copy(
            offset = Offset(x = offset.x * width, y = offset.y * height),
            size = Size(width = size.width * width, height = size.height * height),
        )
    }

    fun toRect() = asRect
}
