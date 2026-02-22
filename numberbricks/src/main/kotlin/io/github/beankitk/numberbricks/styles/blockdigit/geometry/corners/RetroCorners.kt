package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.data.CornerType
import io.github.beankitk.numberbricks.data.CornerProfile
import io.github.beankitk.numberbricks.data.ShapeRadius

open class RetroCorners(
    outerRadius: CornerRadius = CornerRadius(0.2f),
    singleOuterRadius: CornerRadius = CornerRadius(0.85f)
): AutoCornersProvider() {

    constructor(
        outerRadius: Float = 0.2f,
		singleOuterRadius: Float = 0.85f
    ) : this(CornerRadius(outerRadius), CornerRadius(singleOuterRadius))

    final override val edgeRadius = CornerRadius.Zero
    final override val outerRadius = outerRadius
    final override val cornerNeighborRadius = CornerRadius.Zero
    final override val cornerRadius = CornerRadius.Zero
    final override val jointInlineRadius = CornerRadius.Zero
    final override val jointRadius = CornerRadius.Zero
    final override val innerRadius = CornerRadius.Zero

    protected val singleOuterRadius = singleOuterRadius

    protected override fun modifyShapeRadius(
        digit: Int,
        index: Int,
        shapeRadius: ShapeRadius
    ): ShapeRadius = applySingleOuterRadiusPolicy(shapeRadius)

    protected fun applySingleOuterRadiusPolicy(
        shapeRadius: ShapeRadius
    ): ShapeRadius = with(shapeRadius) {
        var outerRadiusCount = 0

        if (topLeft == outerRadius) outerRadiusCount++
        if (topRight == outerRadius) outerRadiusCount++
        if (bottomRight == outerRadius) outerRadiusCount++
        if (bottomLeft == outerRadius) outerRadiusCount++

        if (outerRadiusCount == 1) {
            copy(
                topLeft = outerToSingleRadius(topLeft),
                topRight = outerToSingleRadius(topRight),
                bottomRight = outerToSingleRadius(bottomRight),
                bottomLeft = outerToSingleRadius(bottomLeft)
            )
        } else this
    }

    private val outerToSingleRadius: (CornerRadius) -> CornerRadius =
        { if (it == outerRadius) singleOuterRadius else it }
}