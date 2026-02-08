package io.github.beankitk.numberbricks.blockdigit.geometry.size

import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.geometry.ProviderStore
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.buildProviderData

/**
 * Provides uniform size for all bricks.
 *
 * All bricks in the digit layout receive the same dimensions. The size
 * is cached after first computation for efficiency.
 */
class DefaultSize private constructor(
    private val brickSize: Size
) : SizeProvider.Adaptive() {

    private var cachedSize: List<Size>? = null

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun getProviderData(digit: Int, providerStore: ProviderStore): List<Size> {
        return cachedSize ?: buildProviderData { brickSize }.also {
            cachedSize = it
        }
    }

    companion object {
        /** A size provider with zero dimensions for all bricks. */
        val Zero = DefaultSize(Size.Zero)

        /**
         * Creates a size provider with uniform dimensions for all bricks.
         *
         * @param brickSize The size to apply to all bricks
         * @return A size provider with the specified uniform size
         */
        fun uniform(brickSize: Size) = DefaultSize(brickSize)

        /**
         * Creates a size provider with uniform dimensions for all bricks.
         *
         * @param width The width for all bricks
         * @param height The height for all bricks (defaults to width for square bricks)
         * @return A size provider with the specified uniform dimensions
         */
        fun uniform(width: Float, height: Float = width) = DefaultSize(Size(width, height))
    }
}