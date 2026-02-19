package io.github.beankitk.numberbricks.blockdigit.geometry.position

import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderStore
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.buildProviderData

/**
 * Provides uniform position for all bricks.
 *
 * All bricks in the digit layout receive the same position. Used
 * for the constructing default bricks or as intermediate position during
 * animations. The position is cached after first computation.
 */
class DefaultPosition private constructor(
    private val position: Position
) : PositionProvider.Adaptive() {

    private var cachedPositions: List<Position>? = null

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun getProviderData(digit: Int, providerStore: ProviderStore): List<Position> {
        return cachedPositions ?: buildProviderData { position }.also {
            cachedPositions = it
        }
    }

    companion object {
        /** A position provider with zero position for all bricks. */
        val Zero = DefaultPosition(Position.Zero)

        /**
         * Creates a position provider with uniform position for all bricks.
         *
         * @param position The position to apply to all bricks
         * @return An position provider with the specified uniform position
         */
        fun of(position: Position): DefaultPosition =
            DefaultPosition(position)
    }
}