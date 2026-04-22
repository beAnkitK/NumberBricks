package io.github.beankitk.numberbricks.blockdigit.geometry.size

import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.core.geometry.buildProviderData

/**
 * Provides a uniform [Size] for all blocks during geometry composition.
 *
 * This [SizeProvider] returns the same size for every block for a given digit. This can be used for
 * geometry where all blocks share identical dimensions. The provided [Size] must be defined in
 * grid-relative fractional units, where `1f` represents the size of a single grid cell.
 *
 * The computed result is cached after first invocation and reused to avoid repeated allocations
 *
 * @param size The uniform block size in grid-relative units (1f = one grid cell)
 */
class UniformSize(private val size: Size) : SizeProvider.Adaptive() {

    private var cachedSize: List<Size>? = null

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun ProviderScope.provideData(): List<Size> {
        return cachedSize ?: buildProviderData { size }.also { cachedSize = it }
    }

    companion object {
        /** A [SizeProvider] that provides zero size for all blocks. */
        val Zero = UniformSize(Size.Zero)

        /**
         * Creates a [UniformSize] provider with uniform dimensions.
         *
         * @param width The width of each block (grid-relative)
         * @param height The height of each block (defaults to [width] for square blocks)
         * @return A [UniformSize] provider with the specified uniform dimensions
         */
        fun of(width: Float, height: Float = width) = UniformSize(Size(width, height))
    }
}
