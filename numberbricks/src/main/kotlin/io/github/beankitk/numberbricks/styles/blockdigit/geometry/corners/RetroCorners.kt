package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.data.CornerType
import io.github.beankitk.numberbricks.data.CornerProfile
import io.github.beankitk.numberbricks.data.ShapeRadius

/**
 * Provides shape-corner radii to all bricks rounding outer corners of bricks
 * differently for single outer corner giving a retro style looking digit.
 *
 * **Note:** This may does apply rounding to some bricks based on the digit layouts.
 * Use the modifying hooks available to adjust rounding based on specific layout. For example,
 * this is incompatible with [io.github.beankitk.numberbricks.blockdigit.geometry.position.AbstractPosition]
 * that sometimes produce different result.
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
         * Retro Rounded style that applies equal rounding to both single & double outer corners.
         *
         * **Note:** This defines offsets assuming 1f brick size, aligned with the [Block] coordinate scale.
         */
        val bubble = RetroCorners(CornerRadius(0.5f))

        /**
         * Retro style that differently rounds single and double outer corners creating a sleek look.
         *
         * **Note:** This defines offsets assuming 1f brick size, aligned with the [Block] coordinate scale.
         */
        fun soloCurve(
            outerRadius: Float = 0.2f,
            singleCornerRadius: Float = 0.85f
        ) = RetroCorners(CornerRadius(outerRadius), CornerRadius(singleCornerRadius))
    }
}