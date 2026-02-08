package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import io.github.beankitk.numberbricks.core.geometry.ProviderStore
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.buildProviderData
import io.github.beankitk.numberbricks.data.ShapeRadius

/**
 * Provides uniform corner radii for all bricks.
 *
 * All bricks in the digit layout receive the same corner radii configuration.
 * The radii are cached after first computation for efficiency.
 */
class DefaultCorners private constructor(
    private val cornerRadius: ShapeRadius
): CornersProvider.Adaptive() {

    private var cachedCornerRadius: List<ShapeRadius>? = null

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun getProviderData(digit: Int, providerStore: ProviderStore): List<ShapeRadius> {
        return cachedCornerRadius ?: buildProviderData { cornerRadius }.also {
            cachedCornerRadius = it
        }
    }

    companion object {
        /** A corner provider with no rounding (sharp corners). */
        val zero = DefaultCorners(ShapeRadius.Zero)

        /**
        * Corner provider with full rounding applied to all corners.
        *
        * Uses the maximum radius for every corner to produce a fully rounded shape.
        *
        * **Note:** This configuration assumes a brick size of `1f`. If the actual brick
        * size differs, the radius must be scaled accordingly.
        *
        * @see Block Describes how corner radius scaling is applied.
        */
        val full = DefaultCorners(ShapeRadius.all(1f))

        /**
         * Creates a corner provider with uniform rounding for all brick corners.
         *
         * @param cornerRadius The radius to apply to all corners of all bricks
         * @return A corner provider with the specified uniform radii
         */
        fun uniform(cornerRadius: Float) = DefaultCorners(ShapeRadius.all(cornerRadius))
    }
}