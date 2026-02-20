package io.github.beankitk.numberbricks.blockdigit.geometry.offset

import androidx.compose.ui.geometry.Offset
import io.github.beankitk.numberbricks.core.geometry.AdaptiveProvider
import io.github.beankitk.numberbricks.core.geometry.FixedProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.ProviderKey

/**
 * Provides offset data for blocks in a digit layout relative to origin
 * (top-left corner of digit layout).
 */
sealed interface OffsetProvider: GeometryProvider<Offset> {

    companion object {
        /** Provider key for offset data. */
        val key = ProviderKey<Offset>("provider.offset.base")
    }

    /** Base class for offset providers with fixed grid requirements. */
    abstract class Fixed: FixedProvider<Offset>(), OffsetProvider {
        final override val key = OffsetProvider.key

        protected override fun onAttachWith(properties: GeometryProps) {}
    }

    /** Base class for offset providers that adapt to any grid configuration. */
    abstract class Adaptive: AdaptiveProvider<Offset>(), OffsetProvider {
        final override val key = OffsetProvider.key

        protected override fun onAttachWith(properties: GeometryProps) {}
    }
}