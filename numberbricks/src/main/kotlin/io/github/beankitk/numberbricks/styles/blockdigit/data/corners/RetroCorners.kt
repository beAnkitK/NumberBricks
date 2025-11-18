package io.github.beankitk.numberbricks.blockdigit.data.corners

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
        if (listOf(topLeft, topRight, bottomRight, bottomLeft).count { it == outerRadius } == 1)
            copy(
                topLeft = if (topLeft == outerRadius) singleCornerRadius else topLeft,
                topRight = if (topRight == outerRadius) singleCornerRadius else topRight,
                bottomRight = if (bottomRight == outerRadius) singleCornerRadius else bottomRight,
                bottomLeft = if (bottomLeft == outerRadius) singleCornerRadius else bottomLeft
            )
        else this
    }
    
    companion object {
        val bubble = RetroCorners(CornerRadius(0.5f))
        
        fun soloCurve(
            outerRadius: Float = 0.2f,
            singleCornerRadius: Float = 0.85f
        ) = RetroCorners(CornerRadius(outerRadius), CornerRadius(singleCornerRadius))
    }
}