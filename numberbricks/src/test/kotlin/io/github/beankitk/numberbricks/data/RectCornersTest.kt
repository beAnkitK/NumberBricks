package io.github.beankitk.numberbricks.data

import androidx.compose.ui.geometry.CornerRadius
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RectCornersTest {

    @Test
    fun testGivenIndividualCornerStyles_createsRectCorners() {
        val corners =
            RectCorners(
                topLeft = CornerStyle(10f, CornerShape.Round),
                topRight = CornerStyle(15f, CornerShape.Square),
                bottomRight = CornerStyle(20f, CornerShape.Round),
                bottomLeft = CornerStyle(25f, CornerShape.Square),
            )

        assertEquals(CornerStyle(10f, CornerShape.Round), corners.topLeft)
        assertEquals(CornerStyle(15f, CornerShape.Square), corners.topRight)
        assertEquals(CornerStyle(20f, CornerShape.Round), corners.bottomRight)
        assertEquals(CornerStyle(25f, CornerShape.Square), corners.bottomLeft)
    }

    @Test
    fun testGivenNoArguments_createsRectCornersWithNoneStyle() {
        val corners = RectCorners()

        assertEquals(CornerStyle.None, corners.topLeft)
        assertEquals(CornerStyle.None, corners.topRight)
        assertEquals(CornerStyle.None, corners.bottomRight)
        assertEquals(CornerStyle.None, corners.bottomLeft)
    }

    @Test
    fun testGivenUniformCornerStyle_createsRectCornersWithUniformStyle() {
        val style = CornerStyle(10f, CornerShape.Round)
        val corners = RectCorners(style)

        assertEquals(style, corners.topLeft)
        assertEquals(style, corners.topRight)
        assertEquals(style, corners.bottomRight)
        assertEquals(style, corners.bottomLeft)
    }

    @Test
    fun testGivenRadiusAndShape_createsRectCorners() {
        val radius = CornerRadius(15f, 20f)
        val corners = RectCorners(radius, CornerShape.Square)

        assertEquals(CornerStyle(radius, CornerShape.Square), corners.topLeft)
        assertEquals(CornerStyle(radius, CornerShape.Square), corners.topRight)
        assertEquals(CornerStyle(radius, CornerShape.Square), corners.bottomRight)
        assertEquals(CornerStyle(radius, CornerShape.Square), corners.bottomLeft)
    }

    @Test
    fun testGivenFloatRadiusAndShape_createsRectCorners() {
        val uniformCorners = RectCorners(radiusX = 10f, shape = CornerShape.Round)
        assertEquals(CornerRadius(10f, 10f), uniformCorners.topLeft.radius)

        val nonUniformCorners =
            RectCorners(radiusX = 10f, shape = CornerShape.Square, radiusY = 20f)
        assertEquals(CornerRadius(10f, 20f), nonUniformCorners.topLeft.radius)
        assertEquals(CornerShape.Square, nonUniformCorners.topLeft.shape)
    }

    @Test
    fun testGivenIndividualRadiiAndShape_createsRectCornersWithIndividualRadii() {
        val corners = RectCorners(tl = 5f, tr = 10f, br = 15f, bl = 20f, shape = CornerShape.Round)

        assertEquals(CornerStyle(5f, CornerShape.Round), corners.topLeft)
        assertEquals(CornerStyle(10f, CornerShape.Round), corners.topRight)
        assertEquals(CornerStyle(15f, CornerShape.Round), corners.bottomRight)
        assertEquals(CornerStyle(20f, CornerShape.Round), corners.bottomLeft)
    }

    @Test
    fun testWhenAllCornersAreZero_isZero_returnsTrue() {
        val zeroCorners = RectCorners(0f, CornerShape.Square)
        assertTrue(zeroCorners.isZero())
    }

    @Test
    fun testWhenAnyCornerIsNonZero_isZero_returnsFalse() {
        val corners =
            RectCorners(
                topLeft = CornerStyle.None,
                topRight = CornerStyle.None,
                bottomRight = CornerStyle(5f, CornerShape.Round),
                bottomLeft = CornerStyle.None,
            )

        assertFalse(corners.isZero())
    }

    @Test
    fun testWhenAllCornersAreSquare_isRect_returnsTrue() {
        val rectCorners = RectCorners(10f, CornerShape.Square)
        assertTrue(rectCorners.isRect())
    }

    @Test
    fun testWhenAnyCornerIsRound_isRect_returnsFalse() {
        val corners =
            RectCorners(
                topLeft = CornerStyle(10f, CornerShape.Square),
                topRight = CornerStyle(10f, CornerShape.Round),
                bottomRight = CornerStyle(10f, CornerShape.Square),
                bottomLeft = CornerStyle(10f, CornerShape.Square),
            )
        assertFalse(corners.isRect())
    }

    @Test
    fun testWhenAllCornersAreSquareOrRound_isRoundRect_returnsTrue() {
        val allSquare = RectCorners(10f, CornerShape.Square)
        assertTrue(allSquare.isRoundRect())

        val allRound = RectCorners(10f, CornerShape.Round)
        assertTrue(allRound.isRoundRect())

        val mixed =
            RectCorners(
                topLeft = CornerStyle(10f, CornerShape.Square),
                topRight = CornerStyle(10f, CornerShape.Round),
                bottomRight = CornerStyle(10f, CornerShape.Square),
                bottomLeft = CornerStyle(10f, CornerShape.Round),
            )
        assertTrue(mixed.isRoundRect())
    }

    @Test
    fun testGivenSharp_isZeroAndRect() {
        assertTrue(RectCorners.Sharp.isZero())
        assertTrue(RectCorners.Sharp.isRect())
        assertTrue(RectCorners.Sharp.isRoundRect())
        assertEquals(CornerStyle.None, RectCorners.Sharp.topLeft)
        assertEquals(CornerStyle.None, RectCorners.Sharp.topRight)
        assertEquals(CornerStyle.None, RectCorners.Sharp.bottomRight)
        assertEquals(CornerStyle.None, RectCorners.Sharp.bottomLeft)
    }

    @Test
    fun testGivenTwoCorners_lerp_interpolatesAllCorners() {
        val start = RectCorners(10f, CornerShape.Square)
        val end = RectCorners(30f, CornerShape.Round)

        val midpoint = lerp(start, end, 0.5f)
        assertEquals(CornerRadius(20f, 20f), midpoint.topLeft.radius)
        assertEquals(CornerRadius(20f, 20f), midpoint.topRight.radius)
        assertEquals(CornerRadius(20f, 20f), midpoint.bottomRight.radius)
        assertEquals(CornerRadius(20f, 20f), midpoint.bottomLeft.radius)
        assertEquals(CornerShape.Round, midpoint.topLeft.shape)

        val atStart = lerp(start, end, 0f)
        assertEquals(start.topLeft.radius, atStart.topLeft.radius)

        val atEnd = lerp(start, end, 1f)
        assertEquals(end.topLeft.radius, atEnd.topLeft.radius)
    }

    @Test
    fun testGivenDifferentCornerRadii_lerp_interpolatesEachCorner() {
        val start =
            RectCorners(
                topLeft = CornerStyle(5f, CornerShape.Square),
                topRight = CornerStyle(10f, CornerShape.Square),
                bottomRight = CornerStyle(15f, CornerShape.Square),
                bottomLeft = CornerStyle(20f, CornerShape.Square),
            )

        val end =
            RectCorners(
                topLeft = CornerStyle(15f, CornerShape.Round),
                topRight = CornerStyle(30f, CornerShape.Round),
                bottomRight = CornerStyle(35f, CornerShape.Round),
                bottomLeft = CornerStyle(40f, CornerShape.Round),
            )

        val midpoint = lerp(start, end, 0.5f)
        assertEquals(CornerRadius(10f, 10f), midpoint.topLeft.radius)
        assertEquals(CornerRadius(20f, 20f), midpoint.topRight.radius)
        assertEquals(CornerRadius(25f, 25f), midpoint.bottomRight.radius)
        assertEquals(CornerRadius(30f, 30f), midpoint.bottomLeft.radius)
    }

    @Test
    fun testWhenStylesHaveSameValues_areEqual() {
        val corners1 = RectCorners(10f, CornerShape.Round)
        val corners2 = RectCorners(10f, CornerShape.Round)
        val corners3 = RectCorners(10f, CornerShape.Square)

        assertEquals(corners1, corners2)
        assertFalse(corners1 == corners3)
    }
}
