package io.github.beankitk.numberbricks.blockdigit.geometry.position

import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.AdaptiveProvider
import io.github.beankitk.numberbricks.core.geometry.FixedProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.GridSpec
import io.github.beankitk.numberbricks.core.geometry.ProviderKey

sealed interface PositionProvider: GeometryProvider<Position> {

    companion object {
        val key = ProviderKey<Position>("provider.position.base")
    }

    abstract class Fixed: FixedProvider<Position>(), PositionProvider {
        final override val key = PositionProvider.key

        protected override fun onAttachWith(digitGridSpec: GridSpec, geometryProps: GeometryProps) {}
    }

    abstract class Adaptive: AdaptiveProvider<Position>(), PositionProvider {
        final override val key = PositionProvider.key

        protected override fun onAttachWith(digitGridSpec: GridSpec, geometryProps: GeometryProps) {}
    }
}