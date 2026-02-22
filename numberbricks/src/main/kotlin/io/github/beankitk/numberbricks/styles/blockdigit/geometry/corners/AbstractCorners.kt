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
): AutoCornersProvider() {

    constructor(
        outerRadius: Float = 0f,
        cornerNeighborRadius: Float = 0f,
        cornerRadius: Float = 0f,
        jointRadius: Float = 0f
    ) : this(
        outerRadius = CornerRadius(outerRadius),
        cornerNeighborRadius = CornerRadius(cornerNeighborRadius),
        cornerRadius = CornerRadius(cornerRadius),
        jointRadius = CornerRadius(jointRadius)
    )

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
        val Bubble = AbstractCorners(
            outerRadius = CornerRadius(1f),
            cornerNeighborRadius = CornerRadius(0.5f)
        )

        fun rounded(
            radiusX: Float = 0.5f,
            radiusY: Float = radiusX
        ) = AbstractCorners(outerRadius = CornerRadius(radiusX, radiusY))

        fun retro(
            outerRadius: Float = 0.2f,
            cornerNeighborRadius: Float = 0.85f
         ) = AbstractCorners(
            outerRadius = CornerRadius(outerRadius),
            cornerNeighborRadius = CornerRadius(cornerNeighborRadius)
         )
    }
}