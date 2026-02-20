package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.blockdigit.geometry.position.AbstractPosition
import io.github.beankitk.numberbricks.data.CornerType
import io.github.beankitk.numberbricks.data.CornerProfile

/**
 * Provides corner radii to all blocks for a abstracted look. This is only
 * compatible with [AbstractPosition]. For other providers this may produce different
 * result. Use modifying hooks for correct roundings based on digit layouts.
 *
 * @param outerRadius The radius applied to Outer cornertype of block
 * @param cornerNeighborRadius The radius applied to CornerNeighbour cornertype of block
 * @param cornerRadius The radius applied to Corner cornertype of block
 * @param jointRadius The radius applied to Joint cornertype of block
 * @see io.github.beankitk.numberbricks.data.CornerType
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
         * Provides sharp corners radius to all blocks, equivalent to [DefaultCorners.zero].
         * Each block look like a square with no rounding.
         */
        val squared = AbstractCorners()

        /**
         * Provides fully rounded corners to all the outer corners of blocks. This is same as
         * [RetroCorners.bubble] creating all circular bubbled look.
         *
         * **Note:** This defines corner-radius assuming 1f block size, aligned with the [Block]
         * coordinate scale.
         */
        val bubble = AbstractCorners(
            outerRadius = CornerRadius(1f),
            cornerNeighborRadius = CornerRadius(0.5f)
        )

        /**
         * Provides rounded corners to outer corners of blocks. This is different from
         * [bubble] that it leaves the corner neigbours sharp for a balanced abstract rounding.
         *
         * **Note:** This, by default, defines corner-radius assuming 1f block size, aligned with
         * the [Block] coordinate scale.
         *
         * @param rX The horizontal radius applied to Outer cornertype of block
         * @param rY The vertical radius applied to Outer cornertype of block, default to horizontal radius
         * @return An [AbstractCorners] configured with the provided radii.
         */
        fun rounded(
            rX: Float = 0.5f,
            rY: Float = rX
        ) = AbstractCorners(outerRadius = CornerRadius(rX, rY))

        /**
         * Provides rounded corner for each corner types of abstract position block differently. This
         * allows to create a wide range of styles for digit. For achieving [RetroCorners.soloCurve],
         * use `AbstractCorners.vintage(outer = 0.2f, cornerNeighbor = 0.85f) to achieve same look.
         *
         * **Note:** This, by default, defines corner-radius assuming 1f block size, aligned with
         * the [Block] coordinate scale.
         *
         * @param outer The radius applied to Outer cornertype of block
         * @param cornerNeighbor The radius applied to CornerNeighbour cornertype of block
         * @return An [AbstractCorners] configured with the provided radii.
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