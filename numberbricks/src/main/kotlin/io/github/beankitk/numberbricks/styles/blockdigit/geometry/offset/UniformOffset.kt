package io.github.beankitk.numberbricks.blockdigit.geometry.offset

import androidx.compose.ui.geometry.Offset
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.core.geometry.buildProviderData

/**
 * Provides a uniform [Offset] for all blocks during geometry composition.
 *
 * This [OffsetProvider] returns the same offset for every block for a given digit. This can be used
 * for geometry where all blocks share a common coordinate in the grid. The provided [Offset] must
 * be defined in grid-relative fractional units, where `1f` represents the size of a single grid
 * cell.
 *
 * The computed result is cached after first invocation and reused to avoid repeated allocations.
 *
 * @param offset The uniform block offset in grid-relative units (1f = one grid cell)
 */
class UniformOffset(private val offset: Offset) : OffsetProvider.Adaptive() {

    private var cachedOffsets: List<Offset>? = null

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun ProviderScope.provideData(): List<Offset> {
        return cachedOffsets ?: buildProviderData { offset }.also { cachedOffsets = it }
    }

    companion object {
        /** A [OffsetProvider] that provides zero offset for all blocks. */
        val Zero = UniformOffset(Offset.Zero)

        /**
         * Creates a [UniformOffset] provider with a uniform offset.
         *
         * @param x The horizontal offset as fraction
         * @param y The vertical offset as fraction
         * @return A [UniformOffset] provider with the specified offset
         */
        fun of(x: Float, y: Float) = UniformOffset(Offset(x, y))
    }
}
