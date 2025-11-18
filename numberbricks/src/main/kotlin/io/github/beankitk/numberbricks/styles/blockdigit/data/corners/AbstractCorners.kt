package io.github.beankitk.numberbricks.blockdigit.data.corners

import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.data.CornerType
import io.github.beankitk.numberbricks.data.CornerProfile

class AbstractCorners(
    outerRadius: CornerRadius = CornerRadius.Zero,
    cornerNeighborRadius: CornerRadius = CornerRadius.Zero,
    cornerRadius: CornerRadius = CornerRadius.Zero,
    jointRadius: CornerRadius = CornerRadius.Zero
): AutoCornerProvider() {
    
    override val edgeRadius = CornerRadius.Zero
    override val outerRadius = outerRadius
    override val cornerNeighborRadius = cornerNeighborRadius
    override val cornerRadius = cornerRadius
    override val jointInlineRadius = CornerRadius.Zero
    override val jointRadius = jointRadius
    override val innerRadius = CornerRadius.Zero
    
    protected override fun modifyCornerProfile(
        digit: Int,
        index: Int,
        profile: CornerProfile
    ): CornerProfile =
        when {
            digit == 1 && index in 0..2 -> profile.copy(bottomLeft = CornerType.Outer)
            digit == 3 && index in 5..7 -> profile.copy(topLeft = CornerType.Outer, bottomLeft = CornerType.Outer)
            else -> super.modifyCornerProfile(digit, index, profile)
        }

    companion object {
        val squared = AbstractCorners()
        
        fun rounded(
            rX: Float = 0.5f,
            rY: Float = rX
        ) = AbstractCorners(outerRadius = CornerRadius(rX, rY))
        
        fun vintage(
            r1: Float = 1f,
            r2: Float = 1f
        ) = AbstractCorners(
            outerRadius = CornerRadius(r1),
            cornerNeighborRadius = CornerRadius(r2)
        )
    }
}