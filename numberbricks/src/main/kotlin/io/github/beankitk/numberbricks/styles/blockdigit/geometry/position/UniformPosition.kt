package io.github.beankitk.numberbricks.blockdigit.geometry.position

import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.core.geometry.buildProviderData

/**
 * Provides a uniform [Position] for all blocks during geometry composition.
 *
 * This [PositionProvider] returns the same position for every block for a given digit. This can be
 * used for default geometry where all blocks share a common position.
 *
 * The computed result is cached after the first invocation and reused on subsequent calls to avoid
 * redundant computation.
 *
 * @param position The uniform grid position applied to all blocks
 */
class UniformPosition(private val position: Position) : PositionProvider.Adaptive() {

    // Safe without synchronization because providers are evaluated sequentially.
    private var cachedPositions: List<Position>? = null

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun ProviderScope.provideData(): List<Position> {
        return cachedPositions ?: buildProviderData { position }.also { cachedPositions = it }
    }

    companion object {
        /** Creates a [UniformPosition] provider that provides zero position for all blocks. */
        fun zero() = UniformPosition(Position.Zero)

        /**
         * Creates a [UniformPosition] provider with a uniform position.
         *
         * @param row The row index for all blocks
         * @param col The column index for all blocks
         * @return A [UniformPosition] provider with the specified position
         */
        fun of(row: Int, col: Int) = UniformPosition(Position(row, col))
    }
}
