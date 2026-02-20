package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.data.CornerType
import io.github.beankitk.numberbricks.data.CornerProfile
import io.github.beankitk.numberbricks.data.ShapeRadius

/**
 * Provides corner radii to all blocks, rounding outer corners of blocks differently
 * from single outer corners giving a retro style looking digit.
 *
 * **Note:** This may does not apply rounding to some blocks based on the digit layouts.
 * Use the modifying hooks available to adjust rounding based on specific layout. For example,
 * this is incompatible with [io.github.beankitk.numberbricks.blockdigit.geometry.position.AbstractPosition]
 * that sometimes produce different result.
 *
 * @param outerRadius The radius applied to Outer cornertype of block
 * @param singleCornerRadius The radius applied to Outer cornertype when block has exactly one outer corner
 * @see io.github.beankitk.numberbricks.data.CornerType
 */
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
        /**
         * Rounded corner style that applies equal rounding to both outer & single outer corners.
         *
         * **Note:** This defines corner-radius assuming 1f block size, aligned with the [Block] coordinate scale.
         */
        val bubble = RetroCorners(CornerRadius(0.5f))

        /**
         * Retro corner style that differently rounds outer and single outer corners creating a sleek look.
         *
         * **Note:** This, by default, defines corner-radius assuming 1f block size, aligned with the [Block] coordinate scale.
         *
         * @param outerRadius The uniform radius applied to Outer cornertype
         * @param singleCornerRadius The uniform radius applied to Outer cornertype when block has exactly one outer corner
         * @return A [RetroCorners] provider with uneven rounding for outer & single outer corners
         */
        fun soloCurve(
            outerRadius: Float = 0.2f,
            singleCornerRadius: Float = 0.85f
        ) = RetroCorners(CornerRadius(outerRadius), CornerRadius(singleCornerRadius))
    }
}