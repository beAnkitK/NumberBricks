package io.github.beankitk.numberbricks.blockdigit.layout.size

import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.layout.AdaptiveProvider
import io.github.beankitk.numberbricks.core.layout.FixedProvider
import io.github.beankitk.numberbricks.core.layout.LayoutProvider
import io.github.beankitk.numberbricks.core.layout.ProviderKey

sealed interface SizeProvider : LayoutProvider<Size> {

    companion object {
        val key = ProviderKey<Size>("provider.size.base")
    }

    abstract class Fixed: FixedProvider<Size>(), SizeProvider {
        final override val key = SizeProvider.key
    }

    abstract class Adaptive: AdaptiveProvider<Size>(), SizeProvider {
        final override val key = SizeProvider.key
    }
}