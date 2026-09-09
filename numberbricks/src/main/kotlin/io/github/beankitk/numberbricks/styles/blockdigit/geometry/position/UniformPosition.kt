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
 * @param position The uniform grid position applied to all blocks
 */
class UniformPosition(private val position: Position) : PositionProvider.Computed() {

    /**
     * Creates a [UniformPosition] provider with a uniform position.
     *
     * @param row The row index for all blocks
     * @param col The column index for all blocks
     * @return A [UniformPosition] provider with the specified position
     */
    constructor(row: Int, col: Int) : this(Position(row, col))

    override val key: PositionProvider.Key
        get() = UniformPosition.Key

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun ProviderScope.provide(): List<Position> = buildProviderData { position }

    companion object {
        /** Creates a [UniformPosition] provider that provides zero position for all blocks. */
        val Zero = UniformPosition(Position.Zero)
    }

    /** Key identifying the [UniformPosition] provider within the [PositionProvider] family. */
    object Key : PositionProvider.Key {
        override fun toString(): String = "UniformPosition"
    }
}
