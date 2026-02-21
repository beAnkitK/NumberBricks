package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.blockdigit.geometry.position.AbstractPosition
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

        val bubble = AbstractCorners(
            outerRadius = CornerRadius(1f),
            cornerNeighborRadius = CornerRadius(0.5f)
        )

        fun rounded(
            rX: Float = 0.5f,
            rY: Float = rX
        ) = AbstractCorners(outerRadius = CornerRadius(rX, rY))

        fun vintage(
            outer: Float = 1f,
            cornerNeighbor: Float = 1f
        ) = AbstractCorners(
            outerRadius = CornerRadius(outer),
            cornerNeighborRadius = CornerRadius(cornerNeighbor)
        )
    }
}