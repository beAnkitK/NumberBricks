package io.github.beankitk.numberbricks.data

import androidx.compose.ui.geometry.CornerRadius
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CornerStyleTest {

    @Test
    fun testGivenRadiusAndShape_constructor_createsStyle() {
        val style = CornerStyle(radius = CornerRadius(10f, 20f), shape = CornerShape.Round)

        assertEquals(CornerRadius(10f, 20f), style.radius)
        assertEquals(CornerShape.Round, style.shape)
    }

    @Test
    fun testGivenFloatRadius_constructor_createsStyle() {
        val uniformStyle = CornerStyle(radius = 15f, shape = CornerShape.Square)
        assertEquals(CornerRadius(15f, 15f), uniformStyle.radius)
        assertEquals(CornerShape.Square, uniformStyle.shape)

        val nonUniformStyle = CornerStyle(radius = 10f, shape = CornerShape.Round, radiusY = 20f)
        assertEquals(CornerRadius(10f, 20f), nonUniformStyle.radius)
        assertEquals(CornerShape.Round, nonUniformStyle.shape)
    }

    @Test
    fun testWhenRadiiAreEqual_isUniform_returnsTrue() {
        val uniformRound = CornerStyle(radius = 10f, shape = CornerShape.Round)
        assertTrue(uniformRound.isUniform())

        val uniformSquare = CornerStyle(radius = 10f, shape = CornerShape.Square)
        assertTrue(uniformSquare.isUniform())

        val nonUniform = CornerStyle(radius = 10f, shape = CornerShape.Round, radiusY = 20f)
        assertFalse(nonUniform.isUniform())
    }

    @Test
    fun testWhenEitherAxisRadiusIsZero_isZero_returnsTrue() {
        val zeroStyle = CornerStyle(radius = 0f, shape = CornerShape.Square)
        assertTrue(zeroStyle.isZero())

        val nonZeroStyle = CornerStyle(radius = 5f, shape = CornerShape.Round)
        assertFalse(nonZeroStyle.isZero())

        val partialZero = CornerStyle(radius = 0f, radiusY = 5f, shape = CornerShape.Round)
        assertTrue(partialZero.isZero())
    }

    @Test
    fun testWhenShapeIsSquare_isSquare_returnsTrue() {
        val squareStyle = CornerStyle(radius = 10f, shape = CornerShape.Square)
        assertTrue(squareStyle.isSquare())
        assertFalse(squareStyle.isRound())

        val roundStyle = CornerStyle(radius = 10f, shape = CornerShape.Round)
        assertFalse(roundStyle.isSquare())
        assertTrue(roundStyle.isRound())
    }

    @Test
    fun testGivenNone_isZeroAndSquare() {
        assertTrue(CornerStyle.None.isZero())
        assertTrue(CornerStyle.None.isSquare())
        assertFalse(CornerStyle.None.isRound())
        assertEquals(CornerRadius.Zero, CornerStyle.None.radius)
        assertEquals(CornerShape.Square, CornerStyle.None.shape)
    }

    @Test
    fun testGivenTwoStyles_lerp_interpolatesRadius() {
        val start = CornerStyle(radius = 10f, shape = CornerShape.Square)
        val end = CornerStyle(radius = 30f, shape = CornerShape.Round)

        val midpoint = lerp(start, end, 0.5f)
        assertEquals(CornerRadius(20f, 20f), midpoint.radius)

        val quarterway = lerp(start, end, 0.25f)
        assertEquals(CornerRadius(15f, 15f), quarterway.radius)

        val atStart = lerp(start, end, 0f)
        assertEquals(start.radius, atStart.radius)

        val atEnd = lerp(start, end, 1f)
        assertEquals(end.radius, atEnd.radius)
    }

    @Test
    fun testGivenTwoStyles_lerp_transitionsShapeAtMidpoint() {
        val start = CornerStyle(radius = 10f, shape = CornerShape.Square)
        val end = CornerStyle(radius = 30f, shape = CornerShape.Round)

        val beforeMidpoint = lerp(start, end, 0.49f)
        assertEquals(CornerShape.Square, beforeMidpoint.shape)

        val atMidpoint = lerp(start, end, 0.5f)
        assertEquals(CornerShape.Round, atMidpoint.shape)

        val afterMidpoint = lerp(start, end, 0.51f)
        assertEquals(CornerShape.Round, afterMidpoint.shape)
    }

    @Test
    fun testGivenNonUniformRadii_lerp_interpolatesRadius() {
        val start = CornerStyle(radius = 10f, shape = CornerShape.Round, radiusY = 20f)
        val end = CornerStyle(radius = 30f, shape = CornerShape.Square, radiusY = 40f)

        val midpoint = lerp(start, end, 0.5f)
        assertEquals(CornerRadius(20f, 30f), midpoint.radius)
        assertEquals(CornerShape.Square, midpoint.shape)
    }

    @Test
    fun testWhenStylesHaveSameValues_areEqual() {
        val style1 = CornerStyle(radius = 10f, shape = CornerShape.Round)
        val style2 = CornerStyle(radius = 10f, shape = CornerShape.Round)
        val style3 = CornerStyle(radius = 10f, shape = CornerShape.Square)

        assertEquals(style1, style2)
        assertFalse(style1 == style3)
    }
}
