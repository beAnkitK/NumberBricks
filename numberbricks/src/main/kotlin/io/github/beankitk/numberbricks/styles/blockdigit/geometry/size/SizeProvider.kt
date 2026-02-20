package io.github.beankitk.numberbricks.blockdigit.geometry.size

import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.geometry.AdaptiveProvider
import io.github.beankitk.numberbricks.core.geometry.FixedProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.ProviderKey

/** Provides the dimensions (width and height) for blocks in a digit layout. */
sealed interface SizeProvider : GeometryProvider<Size> {

    companion object {
        /** Provider key for size data. */
        val key = ProviderKey<Size>("provider.size.base")
    }

    /** Base class for size providers with fixed grid requirements. */
    abstract class Fixed: FixedProvider<Size>(), SizeProvider {
        final override val key = SizeProvider.key

        protected override fun onAttachWith(properties: GeometryProps) {}
    }

    /** Base class for size providers that adapt to any grid configuration. */
    abstract class Adaptive: AdaptiveProvider<Size>(), SizeProvider {
        final override val key = SizeProvider.key

        protected override fun onAttachWith(properties: GeometryProps) {}
    }
}