package io.github.beankitk.numberbricks.blockdigit.geometry.position

import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.AdaptiveProvider
import io.github.beankitk.numberbricks.core.geometry.FixedProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.ProviderKey

/** Provides position data for blocks in a digit layout. */
sealed interface PositionProvider: GeometryProvider<Position> {

    companion object {
        /** Provider key for position data. */
        val key = ProviderKey<Position>("provider.position.base")
    }

    /** Base class for position providers with fixed grid requirements. */
    abstract class Fixed: FixedProvider<Position>(), PositionProvider {
        final override val key = PositionProvider.key

        protected override fun onAttachWith(properties: GeometryProps) {}
    }

    /** Base class for position providers that adapt to any grid configuration. */
    abstract class Adaptive: AdaptiveProvider<Position>(), PositionProvider {
        final override val key = PositionProvider.key

        protected override fun onAttachWith(properties: GeometryProps) {}
    }
}