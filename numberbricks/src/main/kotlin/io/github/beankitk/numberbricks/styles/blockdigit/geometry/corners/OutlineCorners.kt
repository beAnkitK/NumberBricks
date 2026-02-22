package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.data.CornerProfile
import io.github.beankitk.numberbricks.data.ShapeRadius

class OutlineCorners(outerRadius: CornerRadius): AutoCornersProvider() {

    constructor(outerRadius: Float) : this(CornerRadius(outerRadius))

    override val edgeRadius = CornerRadius.Zero
    override val outerRadius = outerRadius
    override val cornerNeighborRadius = CornerRadius.Zero
    override val cornerRadius = CornerRadius.Zero
    override val jointInlineRadius = CornerRadius.Zero
    override val jointRadius = CornerRadius.Zero
    override val innerRadius = CornerRadius.Zero
}