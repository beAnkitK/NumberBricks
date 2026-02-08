package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.blockdigit.geometry.offset.AbstractOffset
import io.github.beankitk.numberbricks.data.CornerType
import io.github.beankitk.numberbricks.data.CornerProfile

/**
 * Provides shape-corner radii to all bricks for a abstracted look. This is only
 * compatible with [AbstractOffset]. For other providers this may produce different
 * result. Use modifying hooks for correct roundings based on digit layouts.
 */
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
        /**
         * Provides sharp corners radius to all bricks, equivalent to [DefaultCorners.zero].
         * Each block look like a square with no rounding.
         */
        val squared = AbstractCorners()

        /**
         * Provides fully rounded corners all the outer corners of bricks. This is same as
         * [RetroCorners.bubble] creating all circular bubbled look.
         */
        val bubble = AbstractCorners(
            outerRadius = CornerRadius(1f),
            cornerNeighborRadius = CornerRadius(0.5f)
        )

        /**
         * Provides rounded corners to outer corners of bricks. This is different from
         * [bubble] that it leaves the corner neigbours sharp for a balanced abstract rounding.
         */
        fun rounded(
            rX: Float = 0.5f,
            rY: Float = rX
        ) = AbstractCorners(outerRadius = CornerRadius(rX, rY))

        /**
         * Provides rounded corner each corner types of abstract offset block differently. This allows
         * to create a wide range of styles for digit. For achieving [RetroCorners.soloCurve], use
         * `AbstractCorners.vintage(outer = 0.2f, cornerNeighbor = 0.85f) to achieve same look.
         */
        fun vintage(
            outer: Float = 1f,
            cornerNeighbor: Float = 1f
        ) = AbstractCorners(
            outerRadius = CornerRadius(outer),
            cornerNeighborRadius = CornerRadius(cornerNeighbor)
        )
    }
}