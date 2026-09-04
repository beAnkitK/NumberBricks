package io.github.beankitk.numberbricks.core.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.testing.TestBrick
import kotlin.test.Test
import kotlin.test.assertEquals

class BrickTest {

    @Test
    fun testToRect_whenCalled_returnsRect() {
        val offset = Offset(10f, 20f)
        val size = Size(100f, 200f)
        val brick = TestBrick(index = 1, position = Position(0, 0), offset = offset, size = size)

        val rect = brick.toRect()

        assertEquals(offset.x, rect.left)
        assertEquals(offset.y, rect.top)
        assertEquals(offset.x + size.width, rect.right)
        assertEquals(offset.y + size.height, rect.bottom)
    }

    @Test
    fun testScaledBy_whenValid_scalesOffsetAndSize() {
        val brick =
            TestBrick(
                index = 42,
                position = Position(2, 3),
                offset = Offset(2.5f, 3f),
                size = Size(1.5f, 1f),
            )
        val totalSize = Size(200f, 300f)
        val brickSize = Size(40f, 50f)

        val scaledBrick = brick.scaledBy(totalSize, brickSize)

        assertEquals(42, scaledBrick.index)
        assertEquals(Position(2, 3), scaledBrick.position)
        assertEquals(Offset(100f, 150f), scaledBrick.offset)
        assertEquals(Size(60f, 50f), scaledBrick.size)
    }

    @Test
    fun testScaledBy_whenCalled_doesNotChangeIdentity() {
        val brick =
            TestBrick(
                index = 42,
                position = Position(2, 3),
                offset = Offset(2.5f, 3f),
                size = Size(1.5f, 1f),
            )

        val scaledBrick = brick.scaledBy(totalSize = Size(999f, 999f), brickSize = Size(40f, 50f))

        assertEquals(brick.index, scaledBrick.index)
        assertEquals(brick.position, scaledBrick.position)
    }
}
