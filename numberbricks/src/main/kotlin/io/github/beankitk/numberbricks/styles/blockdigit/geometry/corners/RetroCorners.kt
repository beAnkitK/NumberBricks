package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.data.CornerType
import io.github.beankitk.numberbricks.data.CornerProfile
import io.github.beankitk.numberbricks.data.ShapeRadius

class RetroCorners(
    outerRadius: CornerRadius,
    private val singleCornerRadius: CornerRadius = outerRadius
): AutoCornerProvider() {

    override val edgeRadius = CornerRadius.Zero
    override val outerRadius = outerRadius
    override val cornerNeighborRadius = CornerRadius.Zero
    override val cornerRadius = CornerRadius.Zero
    override val jointInlineRadius = CornerRadius.Zero
    override val jointRadius = CornerRadius.Zero
    override val innerRadius = CornerRadius.Zero

    protected override fun modifyShapeRadius(
        digit: Int,
        index: Int,
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

    private val outerToSingleRadius: (CornerRadius) -> CornerRadius = { if (it == outerRadius) singleCornerRadius else it }

    companion object {
        val bubble = RetroCorners(CornerRadius(0.5f))

        fun soloCurve(
            outerRadius: Float = 0.2f,
            singleCornerRadius: Float = 0.85f
        ) = RetroCorners(CornerRadius(outerRadius), CornerRadius(singleCornerRadius))
    }
}