package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.core.geometry.buildProviderData
import io.github.beankitk.numberbricks.data.CornerShape
import io.github.beankitk.numberbricks.data.CornerStyle
import io.github.beankitk.numberbricks.data.RectCorners

/**
 * Provides uniform [RectCorners] for all blocks during geometry composition.
 *
 * This [CornersProvider] returns the same corner styling for every block for a given digit. This
 * can be used for consistent visual appearance in a uniform geometry. Corner radius values must be
 * defined in grid-relative fractional units, where `1f` represents the maximum radius constrained
 * by the block size.
 *
 * @param rectCorners The uniform corner styling applied to all blocks
 */
class UniformCorners(private val rectCorners: RectCorners) : CornersProvider.Computed() {

    /**
     * Creates a [UniformCorners] provider with the given [CornerStyle].
     *
     * @param cornerStyle The corner style applied to all corners
     */
    constructor(cornerStyle: CornerStyle) : this(RectCorners(cornerStyle))

    /**
     * Creates a [UniformCorners] provider with uniform corner radius and shape.
     *
     * @param radius The horizontal corner radius (grid-relative)
     * @param shape The corner shape
     * @param radiusY The vertical corner radius (defaults to [radius])
     */
    constructor(
        radius: Float,
        shape: CornerShape = CornerShape.Round,
        radiusY: Float = radius,
    ) : this(CornerStyle(CornerRadius(radius, radiusY), shape))

    override val key: CornersProvider.Key
        get() = UniformCorners.Key

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun ProviderScope.provide(): List<RectCorners> = buildProviderData { rectCorners }

    companion object {
        /** Creates a [UniformCorners] provider that provides sharp corners for all blocks. */
        val Sharp = UniformCorners(RectCorners.Sharp)

        /**
         * Creates a [UniformCorners] provider that provides fully rounded corners for all blocks.
         */
        val Round = UniformCorners(RectCorners(1f, CornerShape.Round))
    }

    /** Key identifying the [UniformCorners] provider within the [CornersProvider] family. */
    object Key : CornersProvider.Key {
        override fun toString(): String = "UniformCorners"
    }
}
