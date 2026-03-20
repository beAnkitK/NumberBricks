package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.data.CornerProfile
import io.github.beankitk.numberbricks.data.CornerShape
import io.github.beankitk.numberbricks.data.CornerStyle

class OutlineCorners(outerCornerStyle: CornerStyle): AutoCornersProvider() {

    constructor(
        outerRadius: Float,
        outerShape: CornerShape = CornerShape.Round
    ) : this(CornerStyle(outerRadius, outerShape))

    override val edgeCornerStyle = CornerStyle.None
    override val outerCornerStyle = outerCornerStyle
    override val cornerNeighborCornerStyle = CornerStyle.None
    override val cornerCornerStyle = CornerStyle.None
    override val jointInlineCornerStyle = CornerStyle.None
    override val jointCornerStyle = CornerStyle.None
    override val innerCornerStyle = CornerStyle.None
}