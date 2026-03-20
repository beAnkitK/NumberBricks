package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.data.CornerType
import io.github.beankitk.numberbricks.data.CornerProfile
import io.github.beankitk.numberbricks.data.RectCorners
import io.github.beankitk.numberbricks.data.CornerShape
import io.github.beankitk.numberbricks.data.CornerStyle

open class RetroCorners(
    outerCornerStyle: CornerStyle = CornerStyle(0.2f, CornerShape.Round),
    singleOuterCornerStyle: CornerStyle = CornerStyle(0.85f, CornerShape.Round)
): AutoCornersProvider() {

    constructor(
        outerRadius: Float = 0.2f,
		singleOuterRadius: Float = 0.85f,
        cornerShape: CornerShape = CornerShape.Round
    ) : this(CornerStyle(outerRadius, cornerShape), CornerStyle(singleOuterRadius, cornerShape))

    final override val edgeCornerStyle = CornerStyle.None
    final override val outerCornerStyle = outerCornerStyle
    final override val cornerNeighborCornerStyle = CornerStyle.None
    final override val cornerCornerStyle = CornerStyle.None
    final override val jointInlineCornerStyle = CornerStyle.None
    final override val jointCornerStyle = CornerStyle.None
    final override val innerCornerStyle = CornerStyle.None

    protected val singleOuterCornerStyle = singleOuterCornerStyle

    protected override fun modifyRectCorners(
        digit: Int,
        index: Int,
        rectCorners: RectCorners
    ): RectCorners = applySingleOuterCornerStylePolicy(rectCorners)

    protected fun applySingleOuterCornerStylePolicy(
        rectCorners: RectCorners
    ): RectCorners = with(rectCorners) {
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
                bottomLeft = outerToSingleCornerStyle(bottomLeft)
            )
        } else this
    }

    private val outerToSingleCornerStyle: (CornerStyle) -> CornerStyle =
        { if (it == outerCornerStyle) singleOuterCornerStyle else it }
}