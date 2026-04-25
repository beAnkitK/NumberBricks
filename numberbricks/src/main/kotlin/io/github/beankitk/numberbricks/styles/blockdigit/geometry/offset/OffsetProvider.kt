package io.github.beankitk.numberbricks.blockdigit.geometry.offset

import androidx.compose.ui.geometry.Offset
import io.github.beankitk.numberbricks.core.geometry.AdaptiveProvider
import io.github.beankitk.numberbricks.core.geometry.FixedProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.GeometryProvider
import io.github.beankitk.numberbricks.core.geometry.GridSpec
import io.github.beankitk.numberbricks.core.geometry.ProviderKey

/**
 * Provides [Offset] for each block during geometry composition.
 *
 * An [OffsetProvider] is a [GeometryProvider] responsible for defining the rendered position
 * (top-left offset) of each block relative to the digit origin. The produced values are used during
 * block assembly to construct the final brick model.
 *
 * Implementations must provide offsets as grid-relative fractional values, where `1f` represents
 * the size of a single grid cell.
 *
 * Two base implementations are provided:
 * - [Fixed] -> for providers targeting a specific grid configuration
 * - [Adaptive] -> for providers supporting any grid configuration
 */
sealed interface OffsetProvider : GeometryProvider<Offset> {

    companion object {
        /** Provider key for [OffsetProvider]. */
        val key = ProviderKey<Offset>("provider.offset.base")
    }

    /** Base class for [OffsetProvider]s with fixed grid requirements. */
    abstract class Fixed : FixedProvider<Offset>(), OffsetProvider {
        final override val key = OffsetProvider.key

        protected override fun onAttachWith(
            digitGridSpec: GridSpec,
            geometryProps: GeometryProps,
        ) = Unit
    }

    /** Base class for [OffsetProvider]s that adapt to any grid configuration. */
    abstract class Adaptive : AdaptiveProvider<Offset>(), OffsetProvider {
        final override val key = OffsetProvider.key

        protected override fun onAttachWith(
            digitGridSpec: GridSpec,
            geometryProps: GeometryProps,
        ) = Unit
    }
}
