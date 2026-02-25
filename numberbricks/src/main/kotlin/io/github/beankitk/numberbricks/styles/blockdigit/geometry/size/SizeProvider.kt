package io.github.beankitk.numberbricks.blockdigit.geometry.size

import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.geometry.AdaptiveProvider
import io.github.beankitk.numberbricks.core.geometry.FixedProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.GridSpec
import io.github.beankitk.numberbricks.core.geometry.ProviderKey

sealed interface SizeProvider : GeometryProvider<Size> {

    companion object {
        val key = ProviderKey<Size>("provider.size.base")
    }

    abstract class Fixed: FixedProvider<Size>(), SizeProvider {
        final override val key = SizeProvider.key

        protected override fun onAttachWith(digitGridSpec: GridSpec, geometryProps: GeometryProps) {}
    }

    abstract class Adaptive: AdaptiveProvider<Size>(), SizeProvider {
        final override val key = SizeProvider.key

        protected override fun onAttachWith(digitGridSpec: GridSpec, geometryProps: GeometryProps) {}
    }
}