package io.github.beankitk.numberbricks.blockdigit.geometry

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.data.CornerShape
import io.github.beankitk.numberbricks.data.CornerStyle 
import io.github.beankitk.numberbricks.data.RectCorners
import kotlin.test.Test
import kotlin.test.assertEquals

class BlockTest {

    private val startBlock = Block(
        index = 1,
        position = Position(0, 0),
        offset = Offset(10f, 20f),
        size = Size(100f, 200f),
        corners = createRectCorners(0.2f)
    )

    private val endBlock = Block(
        index = 2,
        position = Position(1, 1),
        offset = Offset(20f, 40f),
        size = Size(200f, 400f),
        corners = createRectCorners(0.4f)
    )

    @Test
    fun testToRect_returnsRect() {
        val offset = Offset(1.5f, 2.0f)
        val size = Size(3.0f, 4.0f)
        val block = Block(
            index = 1,
            position = Position(0, 0),
            offset = offset,
            size = size,
            corners = createRectCorners(0f)
        )

        val rect = block.toRect()

        assertEquals(offset.x, rect.left)
        assertEquals(offset.y, rect.top)
        assertEquals(offset.x + size.width, rect.right)
        assertEquals(offset.y + size.height, rect.bottom)
    }

    @Test
    fun testToRoundRect_returnsRoundRect() {
        val offset = Offset(0f, 0f)
        val size = Size(10f, 10f)
        val block = Block(
            index = 1,
            position = Position(0, 0),
            offset = offset,
            size = size,
            corners = createRectCorners(0.5f)
        )

        val roundRect = block.toRoundRect()

        assertEquals(0.5f, roundRect.topLeftCornerRadius.x)
        assertEquals(0.5f, roundRect.topRightCornerRadius.x)
        assertEquals(0.5f, roundRect.bottomRightCornerRadius.x)
        assertEquals(0.5f, roundRect.bottomLeftCornerRadius.x)
    }

    @Test
    fun testScaledBy_whenValid_scalesCorrectly() {
        val block = Block(
            index = 42,
            position = Position(2, 3),
            offset = Offset(2.5f, 3.0f),
            size = Size(1.5f, 1.0f),
            corners = createRectCorners(0.8f)
        )
        val totalSize = Size(200f, 200f)
        val brickSize = Size(40f, 50f)

        val scaledBlock = block.scaledBy(totalSize, brickSize)

        assertEquals(42, scaledBlock.index)
        assertEquals(Position(2, 3), scaledBlock.position)
        assertEquals(100f, scaledBlock.offset.x)
        assertEquals(150f, scaledBlock.offset.y)
        assertEquals(60f, scaledBlock.size.width)
        assertEquals(50f, scaledBlock.size.height)
        assertEquals(CornerRadius(32f), scaledBlock.corners.topLeft.radius)
    }

    @Test
    fun testLerp_whenTIsZero_returnsStartIdentity() {
        val result = lerp(startBlock, endBlock, 0.0f)

        assertEquals(endBlock.index, result.index)
        assertEquals(endBlock.position, result.position)
        assertEquals(startBlock.offset, result.offset)
        assertEquals(startBlock.size, result.size)
        assertEquals(startBlock.corners.topLeft.radius, result.corners.topLeft.radius)
    }

    @Test
    fun testLerp_whenTIsOne_returnsEndIdentity() {
        val result = lerp(startBlock, endBlock, 1.0f)

        assertEquals(endBlock.index, result.index)
        assertEquals(endBlock.position, result.position)
        assertEquals(endBlock.offset, result.offset)
        assertEquals(endBlock.size, result.size)
        assertEquals(endBlock.corners.topLeft.radius, result.corners.topLeft.radius)
    }

    @Test
    fun testLerp_whenTIsHalf_returnsInterpolatedValues() {
        val result = lerp(startBlock, endBlock, 0.5f)

        assertEquals(endBlock.index, result.index)
        assertEquals(endBlock.position, result.position)
        assertEquals(15f, result.offset.x)
        assertEquals(30f, result.offset.y)
        assertEquals(150f, result.size.width)
        assertEquals(300f, result.size.height)
        assertEquals(CornerRadius(0.3f), result.corners.topLeft.radius)
    }

    private fun createRectCorners(radius: Float): RectCorners {
        val style = CornerStyle(radius, CornerShape.Round)
        return RectCorners(
            topLeft = style,
            topRight = style,
            bottomRight = style,
            bottomLeft = style
        )
    }
}
