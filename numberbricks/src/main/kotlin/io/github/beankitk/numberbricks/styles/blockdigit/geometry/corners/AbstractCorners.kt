package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.blockdigit.geometry.position.AbstractPosition
import io.github.beankitk.numberbricks.data.CornerType
import io.github.beankitk.numberbricks.data.CornerProfile
import io.github.beankitk.numberbricks.data.CornerShape
import io.github.beankitk.numberbricks.data.CornerStyle

class AbstractCorners(
    outerCornerStyle: CornerStyle = CornerStyle.None,
    cornerNeighborCornerStyle: CornerStyle = CornerStyle.None,
    cornerCornerStyle: CornerStyle = CornerStyle.None,
    jointCornerStyle: CornerStyle = CornerStyle.None
): AutoCornersProvider() {

    constructor(
        outerRadius: Float = 0f,
        cornerNeighborRadius: Float = 0f,
        cornerRadius: Float = 0f,
        jointRadius: Float = 0f,
        cornerShape: CornerShape = CornerShape.Round
    ) : this(
        outerCornerStyle = CornerStyle(outerRadius, cornerShape),
        cornerNeighborCornerStyle = CornerStyle(cornerNeighborRadius, cornerShape),
        cornerCornerStyle = CornerStyle(cornerRadius, cornerShape),
        jointCornerStyle = CornerStyle(jointRadius, cornerShape)
    )

    override val edgeCornerStyle = CornerStyle.None
    override val outerCornerStyle = outerCornerStyle
    override val cornerNeighborCornerStyle = cornerNeighborCornerStyle
    override val cornerCornerStyle = cornerCornerStyle
    override val jointInlineCornerStyle = CornerStyle.None
    override val jointCornerStyle = jointCornerStyle
    override val innerCornerStyle = CornerStyle.None

    protected override fun modifyCornerProfile(
        digit: Int,
        index: Int,
        cornerProfile: CornerProfile
    ): CornerProfile =
        when {
            digit == 1 && index in 0..2 -> cornerProfile.copy(bottomLeft = CornerType.Outer)
            digit == 3 && index in 5..7 -> cornerProfile.copy(topLeft = CornerType.Outer, bottomLeft = CornerType.Outer)
            else -> super.modifyCornerProfile(digit, index, cornerProfile)
        }

    companion object {
        fun rounded(
            radius: Float = 0.5f,
            cornerShape: CornerShape = CornerShape.Round,
            radiusY: Float = radius
        ) = AbstractCorners(outerCornerStyle = CornerStyle(radius, cornerShape, radiusY))

        fun retro(
            outerRadius: Float = 0.2f,
            cornerNeighborRadius: Float = 0.85f,
            cornerShape: CornerShape = CornerShape.Round
         ) = AbstractCorners(
            outerRadius = outerRadius,
            cornerNeighborRadius = cornerNeighborRadius,
            cornerShape = cornerShape
         )

        fun bubble(
            outerRadius: Float = 1f,
            cornerNeighborRadius: Float = 0.5f,
            cornerRadius: Float = 1f,
            cornerShape: CornerShape = CornerShape.Round
         ) = AbstractCorners(
            outerRadius = outerRadius,
            cornerNeighborRadius = cornerNeighborRadius,
            cornerRadius = cornerRadius,
            cornerShape = cornerShape
        )
    }
}