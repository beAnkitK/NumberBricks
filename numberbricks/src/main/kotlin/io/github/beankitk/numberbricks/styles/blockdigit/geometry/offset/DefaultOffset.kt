package io.github.beankitk.numberbricks.blockdigit.geometry.offset

import androidx.compose.ui.geometry.Offset
import io.github.beankitk.numberbricks.core.geometry.ProviderStore
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.buildProviderData

/**
 * Provides uniform offset for all blocks.
 *
 * All blocks in the digit layout receive the same position. Used
 * for the constructing default blocks or as intermediate offset during
 * animations. The offset is cached after first computation.
 */
class DefaultOffset private constructor(
    private val offset: Offset
) : OffsetProvider.Adaptive() {

    private var cachedOffsets: List<Offset>? = null

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun getProviderData(digit: Int, providerStore: ProviderStore): List<Offset> {
        return cachedOffsets ?: buildProviderData { offset }.also {
            cachedOffsets = it
        }
    }

    companion object {
        /** An offset provider with zero offset for all blocks. */
        val Zero = DefaultOffset(Offset.Zero)

        /**
         * Creates an offset provider with uniform position for all blocks.
         *
         * @param offset The offset to apply to all blocks
         * @return An offset provider with the specified uniform offset
         */
        fun of(offset: Offset): DefaultOffset =
            DefaultOffset(offset)
    }
}