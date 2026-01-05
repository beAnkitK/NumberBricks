package io.github.beankitk.numberbricks.blockdigit.layout.offset

import androidx.compose.ui.geometry.Offset
import io.github.beankitk.numberbricks.core.layout.AdaptiveProvider
import io.github.beankitk.numberbricks.core.layout.FixedProvider
import io.github.beankitk.numberbricks.core.layout.LayoutProvider
import io.github.beankitk.numberbricks.core.layout.ProviderKey

sealed interface OffsetProvider: LayoutProvider<Offset> {

    companion object {
        val key = ProviderKey<Offset>("provider.offset.base")
    }

    abstract class Fixed: FixedProvider<Offset>(), OffsetProvider {
        final override val key = OffsetProvider.key
    }

    abstract class Adaptive: AdaptiveProvider<Offset>(), OffsetProvider {
        final override val key = OffsetProvider.key
    }
}