package io.github.beankitk.numberbricks.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.data.CornerType.Companion.Corner
import io.github.beankitk.numberbricks.data.CornerType.Companion.CornerNeighbor
import io.github.beankitk.numberbricks.data.CornerType.Companion.Edge
import io.github.beankitk.numberbricks.data.CornerType.Companion.Inner
import io.github.beankitk.numberbricks.data.CornerType.Companion.Joint
import io.github.beankitk.numberbricks.data.CornerType.Companion.JointInline
import io.github.beankitk.numberbricks.data.CornerType.Companion.Outer
import io.github.beankitk.numberbricks.testing.assertProfiles
import kotlin.test.Test

class CornerDetectorTest {

    @Test
    fun testDetectOuterCorners_onSingleRect() {
        val profiles = getCornerProfile(ShapeFixtures.SingleRect)

        assertProfiles(profiles, 1) { rect(0).all().shouldBe(Outer) }
    }

    @Test
    fun testDetectEdgeCorners_onHorizontalLine() {
        val profiles = getCornerProfile(ShapeFixtures.HLine)

        assertProfiles(profiles, 3) {
            rect(1).all().shouldBe(Edge)

            corners(
                rect(0).right(),
                rect(2).left()
            ).shouldBe(Edge)
        }
    }

    @Test
    fun testDetectEdgeCorners_onVerticalLine() {
        val profiles = getCornerProfile(ShapeFixtures.VLine)

        assertProfiles(profiles, 3) {
            rect(1).all().shouldBe(Edge)

            corners(
                rect(0).bottom(),
                rect(2).top()
            ).shouldBe(Edge)
        }
    }

    @Test
    fun testDetectCornerAndCornerNeighborCorners_onDiagonalRects() {
        val profiles = getCornerProfile(ShapeFixtures.DiagonalRects)

        assertProfiles(profiles, 2) {
            rects(0, 1).topRight().bottomLeft().shouldBe(CornerNeighbor)

            corners(
                rect(0).bottomRight(),
                rect(1).topLeft()
            ).shouldBe(Corner)
        }
    }

    @Test
    fun testDetectJointAndJointInlineCorners_onLShape() {
        val profiles = getCornerProfile(ShapeFixtures.LShape)

        assertProfiles(profiles, 3) {
            rect(1).topRight().shouldBe(Joint)

            corners(
                rect(0).bottomRight(),
                rect(2).topLeft()
            ).shouldBe(JointInline)
        }
    }

    @Test
    fun testDetectInnerCorners_onGrid2x2() {
        val profiles = getCornerProfile(ShapeFixtures.Grid2x2)

        assertProfiles(profiles, 4) {
            corners(
                rect(0).bottomRight(),
                rect(1).bottomLeft(),
                rect(2).topRight(),
                rect(3).topLeft(),
            ).shouldBe(Inner)
        }
    }

    @Test
    fun testDetectCornersType_onSparseGrid() {
        val profiles = getCornerProfile(ShapeFixtures.SparseGrid)

        assertProfiles(profiles, 4) { eachRect { it.all().shouldBe(Outer) } }
    }

    @Test
    fun testDetectCornersType_onGrid3x3() {
        val profiles = getCornerProfile(ShapeFixtures.Grid3x3)

        assertProfiles(profiles, 9) {
            rect(0).shouldMatch(Outer, Edge, Inner, Edge)
            rect(1).shouldMatch(Edge, Edge, Inner, Inner)
            rect(2).shouldMatch(Edge, Outer, Edge, Inner)

            rect(3).shouldMatch(Edge, Inner, Inner, Edge)
            rect(4).all().shouldBe(Inner)
            rect(5).shouldMatch(Inner, Edge, Edge, Inner)

            rect(6).shouldMatch(Edge, Inner, Edge, Outer)
            rect(7).shouldMatch(Inner, Inner, Edge, Edge)
            rect(8).shouldMatch(Inner, Edge, Outer, Edge)
        }
    }

    @Test
    fun testDetectCornersType_onPlusShape() {
        val profiles = getCornerProfile(ShapeFixtures.PlusShape)

        assertProfiles(profiles, 5) {
            rect(0).shouldMatch(Outer, Outer, JointInline, JointInline)
            rect(1).shouldMatch(Outer, JointInline, JointInline, Outer)
            rect(2).all().shouldBe(Joint)
            rect(3).shouldMatch(JointInline, Outer, Outer, JointInline)
            rect(4).shouldMatch(JointInline, JointInline, Outer, Outer)
        }
    }

    @Test
    fun testDetectCornersType_onDiagonalStaircase() {
        val profiles = getCornerProfile(ShapeFixtures.DiagonalStaircase)

        assertProfiles(profiles, 4) {
            corners(
                rect(0).topLeft(),
                rect(3).bottomRight()
            ).shouldBe(Outer)

            eachRect {
                it.topRight().bottomLeft().shouldBe(CornerNeighbor)
            }

            corners(
                rects(0, 1, 2).bottomRight(),
                rects(1, 2, 3).topLeft()
            ).shouldBe(Corner)
        }
    }

    @Test
    fun testDetectCornersType_onStairCase() {
        val profiles = getCornerProfile(ShapeFixtures.StairCase)

        assertProfiles(profiles, 10) {
            rect(0).shouldMatch(Outer, Outer, JointInline, Edge)
            rect(1).shouldMatch(Edge, Joint, Inner, Edge)
            rect(2).shouldMatch(JointInline, Outer, JointInline, Inner)

            rect(3).shouldMatch(Edge, Inner, Inner, Edge)
            rect(4).shouldMatch(Inner, Joint, Inner, Inner)
            rect(5).shouldMatch(JointInline, Outer, JointInline, Inner)

            rect(6).shouldMatch(Edge, Inner, Edge, Outer)
            rect(7).shouldMatch(Inner, Inner, Edge, Edge)
            rect(8).shouldMatch(Inner, Joint, Edge, Edge)
            rect(9).shouldMatch(JointInline, Outer, Outer, Edge)
        }
    }

    @Test
    fun testDetectCornersType_onDonut() {
        val profiles = getCornerProfile(ShapeFixtures.Donut)

        assertProfiles(profiles, 4) {
            corners(
                rect(0).top(), rect(1).left(),
                rect(2).right(), rect(3).bottom()
            ).shouldBe(CornerNeighbor)

            corners(
                rect(0).bottom(), rect(1).right(),
                rect(2).left(), rect(3).top()
            ).shouldBe(Corner)
        }
    }

    @Test
    fun testDetectCornersType_onUShape() {
        val profiles = getCornerProfile(ShapeFixtures.UShape)

        assertProfiles(profiles, 7) {
            rects(0, 1).shouldMatch(Outer, Outer, Edge, Edge)
            rect(2).shouldMatch(Edge, Edge, JointInline, Edge)
            rect(3).shouldMatch(Edge, Edge, Edge, JointInline)
            rect(4).shouldMatch(Edge, Joint, Edge, Outer)
            rect(5).shouldMatch(JointInline, JointInline, Edge, Edge)
            rect(6).shouldMatch(Joint, Edge, Outer, Edge)
        }
    }

    @Test
    fun testDetectCornersType_onSnakePath() {
        val profiles = getCornerProfile(ShapeFixtures.SnakePath)

        assertProfiles(profiles, 6) {
            rect(0).shouldMatch(Outer, Edge, JointInline, Outer)
            rect(1).shouldMatch(Edge, Outer, JointInline, Joint)
            rect(2).shouldMatch(JointInline, Joint, JointInline, Outer)
            rect(3).shouldMatch(JointInline, Outer, JointInline, Joint)
            rect(4).shouldMatch(JointInline, Joint, Edge, Outer)
            rect(5).shouldMatch(JointInline, Outer, Outer, Edge)
        }
    }

    @Test
    fun testDetectCornersType_onHollowRectangle() {
        val profiles = getCornerProfile(ShapeFixtures.HollowRectangle)

        assertProfiles(profiles, 8) {
            rect(0).shouldMatch(Outer, Edge, Joint, Edge)
            rect(1).shouldMatch(Edge, Edge, JointInline, JointInline)
            rect(2).shouldMatch(Edge, Outer, Edge, Joint)

            rect(3).shouldMatch(Edge, JointInline, JointInline, Edge)
            rect(4).shouldMatch(JointInline, Edge, Edge, JointInline)

            rect(5).shouldMatch(Edge, Joint, Edge, Outer)
            rect(6).shouldMatch(JointInline, JointInline, Edge, Edge)
            rect(7).shouldMatch(Joint, Edge, Outer, Edge)
        }
    }

    @Test
    fun testDetectCornersType_onZeroSizedRects() {
        val rects = arrayOf(cell(0, 0, width = 0f), cell(5, 5, height = 0f))
        val profiles = getCornerProfile(rects)

        assertProfiles(profiles, 2) {
            rect(0).all().shouldBe(Outer)
            rect(1).all().shouldBe(Outer)
        }
    }

    @Test
    fun testDetectCornersType_onDegenerateRectBesideNeighbor() {
        // Self-overlaps must be ignored while a real neighboring rectangle is still detected.
        val rects = arrayOf(cell(0, 0), cell(0, 1, width = 0f))
        val profiles = getCornerProfile(rects)

        assertProfiles(profiles, 2) {
            rect(0).shouldMatch(Outer, Edge, Edge, Outer)

            // Only the corners shared with the neighboring rectangle become Edge.
            // Corners overlapping solely because of the rectangle's zero width remain Outer.
            rect(1).shouldMatch(Edge, Outer, Outer, Edge)
        }
    }

    @Test
    fun testModifyProfile_overridesProfileCorrectly() {
        val profiles = getCornerProfile(ShapeFixtures.DiagonalRects) { index, profile ->
            if (index == 0) {
                profile.copy(bottomRight = Joint)
            } else if (index == 1) {
                profile.copy(topLeft = Joint)
            } else { profile }
        }

        assertProfiles(profiles, 2) {
            rect(0).shouldMatch(Outer, CornerNeighbor, Joint, CornerNeighbor)
            rect(1).shouldMatch(Joint, CornerNeighbor, Outer, CornerNeighbor)
        }
    }

    @Test
    fun testEmptyArray_returnsEmptyProfiles() {
        val profiles = getCornerProfile(emptyArray())
        assertProfiles(profiles, 0)
    }
}

private fun cell(row: Int, col: Int, width: Float = 1f, height: Float = 1f) =
    Rect(offset = Offset(col.toFloat(), row.toFloat()), size = Size(width, height))

private object ShapeFixtures {

    val SingleRect = arrayOf(cell(0, 0))

    val HLine = arrayOf(
        cell(0, 0), cell(0, 1), cell(0, 2)
    )

    val VLine = arrayOf(
        cell(0, 0),
        cell(1, 0),
        cell(2, 0)
)

    val DiagonalRects = arrayOf(
        cell(0, 0),
                    cell(1, 1)
    )

    val Grid2x2 = arrayOf(
        cell(0, 0), cell(0, 1),
        cell(1, 0), cell(1, 1)
    )

    val Grid3x3 = arrayOf(
        cell(0, 0), cell(0, 1), cell(0, 2),
        cell(1, 0), cell(1, 1), cell(1, 2),
        cell(2, 0), cell(2, 1), cell(2, 2)
    )

    val SparseGrid = arrayOf(
        cell(0, 0),     cell(0, 3),

        cell(3, 0),     cell(3, 3)
    )

    val LShape = arrayOf(
        cell(0, 0),
        cell(1, 0), cell(1, 1)
    )

    val PlusShape = arrayOf(
                    cell(0, 1),
        cell(1, 0), cell(1, 1), cell(1, 2),
                    cell(2, 1)
    )

    val DiagonalStaircase = arrayOf(
        cell(0, 0),
                cell(1, 1),
                        cell(2, 2),
                                cell(3, 3)
    )

    val StairCase = arrayOf(
        cell(0, 0),
        cell(1, 0), cell(1, 1),
        cell(2, 0), cell(2, 1), cell(2, 2),
        cell(3, 0), cell(3, 1), cell(3, 2), cell(3, 3)
    )

    val Donut = arrayOf(
                    cell(0, 1),
        cell(1, 0),             cell(1, 2),
                    cell(2, 1)
    )

    val UShape = arrayOf(
        cell(0, 0),             cell(0, 2),
        cell(1, 0),             cell(1, 2),
        cell(2, 0), cell(2, 1), cell(2, 2)
    )

    val SnakePath = arrayOf(
        cell(0, 0), cell(0, 1),
                    cell(1, 1), cell(1, 2),
                                cell(2, 2), cell(2, 3)
    )

    val HollowRectangle = arrayOf(
        cell(0, 0), cell(0, 1), cell(0, 2),
        cell(1, 0),             cell(1, 2),
        cell(2, 0), cell(2, 1), cell(2, 2)
    )
}
