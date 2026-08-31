package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import io.github.beankitk.numberbricks.data.CornerShape
import io.github.beankitk.numberbricks.data.CornerStyle
import io.github.beankitk.numberbricks.data.CornerType

/**
 * Provides a [CornersProvider] that applies styling only to outer corners of the digit geometry.
 *
 * This extends [AutoCornersProvider] and applies the given [CornerStyle] to corners classified as
 * [CornerType.Outer], while all other corner types remain unstyled. Suitable for outline-style
 * geometry where only the exposed outer contour requires corner styling.
 *
 * @param outerCornerStyle The style applied to outer corners
 */
class OutlineCorners(outerCornerStyle: CornerStyle) : AutoCornersProvider() {

    /**
     * Creates an [OutlineCorners] provider using radius and shape.
     *
     * @param outerRadius The radius used for outer corner styling
     * @param outerShape The shape used for outer corners (defaults to [CornerShape.Round])
     */
    constructor(
        outerRadius: Float,
        outerShape: CornerShape = CornerShape.Round,
    ) : this(CornerStyle(outerRadius, outerShape))

    override val key: CornersProvider.Key
        get() = OutlineCorners.Key

    override val edgeCornerStyle = CornerStyle.None
    override val outerCornerStyle = outerCornerStyle
    override val cornerNeighborCornerStyle = CornerStyle.None
    override val cornerCornerStyle = CornerStyle.None
    override val jointInlineCornerStyle = CornerStyle.None
    override val jointCornerStyle = CornerStyle.None
    override val innerCornerStyle = CornerStyle.None

    /** Key identifying the [OutlineCorners] provider within the [CornersProvider] family. */
    object Key : CornersProvider.Key {
        override fun toString(): String = "OutlineCorners"
    }
}
