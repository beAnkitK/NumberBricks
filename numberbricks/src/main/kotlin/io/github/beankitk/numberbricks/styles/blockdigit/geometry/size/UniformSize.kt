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
 * The computed result is cached after the first invocation and reused on subsequent calls to avoid
 * redundant computation.
 *
 * @param size The uniform block size in grid-relative units (1f = one grid cell)
 */
class UniformSize(private val size: Size) : SizeProvider.Adaptive() {

    /**
     * Creates a [UniformSize] provider with uniform dimensions.
     *
     * @param width The width of each block (grid-relative)
     * @param height The height of each block (defaults to [width] for square blocks)
     * @return A [UniformSize] provider with the specified uniform dimensions
     */
    constructor(width: Float, height: Float = width) : this(Size(width, height))

    // Safe without synchronization because providers are evaluated sequentially.
    private var cachedSize: List<Size>? = null

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun ProviderScope.provideData(): List<Size> {
        return cachedSize ?: buildProviderData { size }.also { cachedSize = it }
    }

    companion object {
        /** Creates a [UniformSize] provider that provides zero size for all blocks. */
        fun zero() = UniformSize(Size.Zero)
    }
}
