package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import io.github.beankitk.numberbricks.blockdigit.geometry.position.AbstractPosition
import io.github.beankitk.numberbricks.data.CornerProfile
import io.github.beankitk.numberbricks.data.CornerShape
import io.github.beankitk.numberbricks.data.CornerStyle
import io.github.beankitk.numberbricks.data.CornerType

/**
 * Provides a [CornersProvider] that applies corner styling for digit geometry based on
 * [AbstractPosition].
 *
 * This extends [AutoCornersProvider] and applies styles to selected [CornerType] classifications
 * including [CornerType.Outer], [CornerType.CornerNeighbor], [CornerType.Corner], and
 * [CornerType.Joint]. All other corner types remain unstyled.
 *
 * By default, all styles are set to [CornerStyle.None], resulting in no corner styling across
 * blocks (similar to [UniformCorners.Sharp]). For completely sharp corners or square blocks, prefer
 * using [UniformCorners.Sharp] directly.
 *
 * Predefined styles:
 * 1. [rounded] -> behaves similar to [OutlineCorners] for abstract geometry
 * 2. [retro] -> behaves similar to [RetroCorners] for abstract geometry
 * 3. [bubble] -> creates a bubble like corner appearance for abstract geometry
 *
 * @param outerCornerStyle The style applied to [CornerType.Outer]
 * @param cornerNeighborCornerStyle The style applied to [CornerType.CornerNeighbor]
 * @param cornerCornerStyle The style applied to [CornerType.Corner]
 * @param jointCornerStyle The style applied to [CornerType.Joint]
 * @see AbstractPosition
 * @see io.github.beankitk.numberbricks.data.CornerType
 */
class AbstractCorners(
    outerCornerStyle: CornerStyle = CornerStyle.None,
    cornerNeighborCornerStyle: CornerStyle = CornerStyle.None,
    cornerCornerStyle: CornerStyle = CornerStyle.None,
    jointCornerStyle: CornerStyle = CornerStyle.None,
) : AutoCornersProvider() {

    /**
     * Creates an [AbstractCorners] provider with corner radius values for key corner types and a
     * common shape.
     *
     * @param outerRadius The radius used for [CornerType.Outer]
     * @param cornerNeighborRadius The radius used for [CornerType.CornerNeighbor]
     * @param cornerRadius The radius used for [CornerType.Corner]
     * @param jointRadius The radius used for [CornerType.Joint]
     * @param cornerShape The shape applied to all styles (defaults to [CornerShape.Round])
     */
    constructor(
        outerRadius: Float = 0f,
        cornerNeighborRadius: Float = 0f,
        cornerRadius: Float = 0f,
        jointRadius: Float = 0f,
        cornerShape: CornerShape = CornerShape.Round,
    ) : this(
        outerCornerStyle = CornerStyle(outerRadius, cornerShape),
        cornerNeighborCornerStyle = CornerStyle(cornerNeighborRadius, cornerShape),
        cornerCornerStyle = CornerStyle(cornerRadius, cornerShape),
        jointCornerStyle = CornerStyle(jointRadius, cornerShape),
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
        cornerProfile: CornerProfile,
    ): CornerProfile =
        when {
            digit == 1 && index in 0..2 -> cornerProfile.copy(bottomLeft = CornerType.Outer)
            digit == 3 && index in 5..7 ->
                cornerProfile.copy(topLeft = CornerType.Outer, bottomLeft = CornerType.Outer)
            else -> super.modifyCornerProfile(digit, index, cornerProfile)
        }

    companion object {
        /**
         * Creates a rounded corner style for abstract digit geometry similar to [OutlineCorners].
         *
         * @param radius The radius used for outer corners
         * @param cornerShape The shape applied to the corners (defaults to [CornerShape.Round])
         * @param radiusY The vertical radius (defaults to [radius])
         */
        fun rounded(
            radius: Float = 0.5f,
            cornerShape: CornerShape = CornerShape.Round,
            radiusY: Float = radius,
        ) = AbstractCorners(outerCornerStyle = CornerStyle(radius, cornerShape, radiusY))

        /**
         * Creates a retro-style corner appearance for abstract digit geometry similar to
         * [RetroCorners].
         *
         * @param outerRadius The radius used for outer corners
         * @param cornerNeighborRadius The radius used for corner-neighbor corners
         * @param cornerShape The shape applied to the corners (defaults to [CornerShape.Round])
         */
        fun retro(
            outerRadius: Float = 0.2f,
            cornerNeighborRadius: Float = 0.85f,
            cornerShape: CornerShape = CornerShape.Round,
        ) =
            AbstractCorners(
                outerRadius = outerRadius,
                cornerNeighborRadius = cornerNeighborRadius,
                cornerShape = cornerShape,
            )

        /**
         * Creates a bubble-style corner appearance for abstract digit geometry.
         *
         * @param outerRadius The radius used for outer corners
         * @param cornerNeighborRadius The radius used for corner-neighbor corners
         * @param cornerRadius The radius used for corner corners
         * @param cornerShape The shape applied to the corners (defaults to [CornerShape.Round])
         */
        fun bubble(
            outerRadius: Float = 1f,
            cornerNeighborRadius: Float = 0.5f,
            cornerRadius: Float = 1f,
            cornerShape: CornerShape = CornerShape.Round,
        ) =
            AbstractCorners(
                outerRadius = outerRadius,
                cornerNeighborRadius = cornerNeighborRadius,
                cornerRadius = cornerRadius,
                cornerShape = cornerShape,
            )
    }
}
