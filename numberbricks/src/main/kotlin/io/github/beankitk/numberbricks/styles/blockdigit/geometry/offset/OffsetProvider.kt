package io.github.beankitk.numberbricks.blockdigit.geometry.offset

import androidx.compose.ui.geometry.Offset
import io.github.beankitk.numberbricks.core.geometry.AdaptiveProvider
import io.github.beankitk.numberbricks.core.geometry.FixedProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.ProviderKey

sealed interface OffsetProvider: GeometryProvider<Offset> {

    companion object {
        val key = ProviderKey<Offset>("provider.offset.base")
    }

    abstract class Fixed: FixedProvider<Offset>(), OffsetProvider {
        final override val key = OffsetProvider.key

        protected override fun onAttachWith(properties: GeometryProps) {}
    }

    abstract class Adaptive: AdaptiveProvider<Offset>(), OffsetProvider {
        final override val key = OffsetProvider.key

        protected override fun onAttachWith(properties: GeometryProps) {}
    }
}