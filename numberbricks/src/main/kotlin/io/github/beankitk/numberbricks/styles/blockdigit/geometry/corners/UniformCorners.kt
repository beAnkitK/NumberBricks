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
 * The computed result is cached after the first invocation and reused on subsequent calls to avoid
 * redundant computation.
 *
 * @param rectCorners The uniform corner styling applied to all blocks
 */
class UniformCorners(private val rectCorners: RectCorners) : CornersProvider.Adaptive() {

    // Safe without synchronization because providers are evaluated sequentially.
    private var cachedRectCorners: List<RectCorners>? = null

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun ProviderScope.provideData(): List<RectCorners> {
        return cachedRectCorners
            ?: buildProviderData { rectCorners }.also { cachedRectCorners = it }
    }

    companion object {
        /** Creates a [UniformCorners] provider that provides sharp corners for all blocks. */
        fun sharp() = UniformCorners(RectCorners.Sharp)

        /** Creates a [UniformCorners] provider that provides fully rounded corners for all blocks. */
        fun round() = UniformCorners(RectCorners(1f, CornerShape.Round))

        /**
         * Creates a [UniformCorners] provider with the given [CornerStyle].
         *
         * @param cornerStyle The corner style applied to all corners
         */
        fun of(cornerStyle: CornerStyle) = UniformCorners(RectCorners(cornerStyle))

        /**
         * Creates a [UniformCorners] provider with uniform corner radius and shape.
         *
         * @param radius The horizontal corner radius (grid-relative)
         * @param shape The corner shape
         * @param radiusY The vertical corner radius (defaults to [radius])
         */
        fun of(radius: Float, shape: CornerShape = CornerShape.Round, radiusY: Float = radius) =
            of(CornerStyle(CornerRadius(radius, radiusY), shape))
    }
}
