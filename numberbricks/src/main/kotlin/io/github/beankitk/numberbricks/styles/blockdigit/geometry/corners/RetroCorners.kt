package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import io.github.beankitk.numberbricks.data.CornerShape
import io.github.beankitk.numberbricks.data.CornerStyle
import io.github.beankitk.numberbricks.data.CornerType
import io.github.beankitk.numberbricks.data.RectCorners

/**
 * Provides a [CornersProvider] that applies retro-style corner styling using distinct styles for
 * outer and single outer corners.
 *
 * This extends [AutoCornersProvider] and applies [outerCornerStyle] to corners classified as
 * [CornerType.Outer]. When a block has exactly one outer corner, that corner is replaced with
 * [singleOuterCornerStyle], producing a sharper, retro-inspired appearance.
 *
 * All other corner types remain unstyled.
 *
 * @param outerCornerStyle The style applied to outer corners
 * @param singleOuterCornerStyle The style applied when a block has exactly one outer corner
 * @see io.github.beankitk.numberbricks.data.CornerType
 */
open class RetroCorners(
    outerCornerStyle: CornerStyle = CornerStyle(0.2f, CornerShape.Round),
    singleOuterCornerStyle: CornerStyle = CornerStyle(0.85f, CornerShape.Round),
) : AutoCornersProvider() {

    /**
     * Creates a [RetroCorners] provider with corner radius values for outer and single outer
     * corners and a common shape.
     *
     * @param outerRadius The radius used for outer corners
     * @param singleOuterRadius The radius used when a block has exactly one outer corner
     * @param cornerShape The shape applied to both styles (defaults to [CornerShape.Round])
     */
    constructor(
        outerRadius: Float = 0.2f,
        singleOuterRadius: Float = 0.85f,
        cornerShape: CornerShape = CornerShape.Round,
    ) : this(CornerStyle(outerRadius, cornerShape), CornerStyle(singleOuterRadius, cornerShape))

    final override val edgeCornerStyle = CornerStyle.None
    final override val outerCornerStyle = outerCornerStyle
    final override val cornerNeighborCornerStyle = CornerStyle.None
    final override val cornerCornerStyle = CornerStyle.None
    final override val jointInlineCornerStyle = CornerStyle.None
    final override val jointCornerStyle = CornerStyle.None
    final override val innerCornerStyle = CornerStyle.None

    /**
     * Corner style applied when exactly one corner in a block is classified as [CornerType.Outer].
     */
    protected val singleOuterCornerStyle = singleOuterCornerStyle

    protected override fun modifyRectCorners(
        digit: Int,
        index: Int,
        rectCorners: RectCorners,
    ): RectCorners = applySingleOuterCornerStylePolicy(rectCorners)

    /**
     * Applies single outer corner styling when exactly one corner is classified as outer within the
     * given [RectCorners].
     *
     * @param rectCorners The corner styles to evaluate
     */
    protected fun applySingleOuterCornerStylePolicy(rectCorners: RectCorners): RectCorners =
        with(rectCorners) {
            var outerCornerStyleCount = 0

            if (topLeft == outerCornerStyle) outerCornerStyleCount++
            if (topRight == outerCornerStyle) outerCornerStyleCount++
            if (bottomRight == outerCornerStyle) outerCornerStyleCount++
            if (bottomLeft == outerCornerStyle) outerCornerStyleCount++

            if (outerCornerStyleCount == 1) {
                copy(
                    topLeft = outerToSingleCornerStyle(topLeft),
                    topRight = outerToSingleCornerStyle(topRight),
                    bottomRight = outerToSingleCornerStyle(bottomRight),
                    bottomLeft = outerToSingleCornerStyle(bottomLeft),
                )
            } else this
        }

    private val outerToSingleCornerStyle: (CornerStyle) -> CornerStyle = {
        if (it == outerCornerStyle) singleOuterCornerStyle else it
    }
}
