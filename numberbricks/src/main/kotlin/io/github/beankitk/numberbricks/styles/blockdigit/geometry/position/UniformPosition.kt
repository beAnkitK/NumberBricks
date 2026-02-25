package io.github.beankitk.numberbricks.blockdigit.geometry.position

import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderStore
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.buildProviderData

class UniformPosition(
    private val position: Position
) : PositionProvider.Adaptive() {

    private var cachedPositions: List<Position>? = null

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun provideData(digit: Int, providerStore: ProviderStore): List<Position> {
        return cachedPositions ?: buildProviderData { position }.also {
            cachedPositions = it
        }
    }

    companion object {
        val Zero = UniformPosition(Position.Zero)

        fun of(row: Int, col: Int) = UniformPosition(Position(row, col))
    }
}