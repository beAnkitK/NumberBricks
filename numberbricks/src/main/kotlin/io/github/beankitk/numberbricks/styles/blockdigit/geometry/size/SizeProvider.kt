package io.github.beankitk.numberbricks.blockdigit.geometry.size

import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.geometry.AdaptiveProvider
import io.github.beankitk.numberbricks.core.geometry.FixedProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.GeometryProvider
import io.github.beankitk.numberbricks.core.geometry.GridSpec
import io.github.beankitk.numberbricks.core.geometry.ProviderKey

/**
 * Provides size (width and height) for each block during geometry composition.
 *
 * A [SizeProvider] is a [GeometryProvider] responsible for providing the [Size] of each block for a
 * given digit. The produced values are used during block assembly to construct the final brick
 * model.
 *
 * Implementations must provide sizes as grid-relative fractional values, where `1f` represents the
 * size of a single grid cell.
 *
 * Two base implementations are provided:
 * - [Fixed] -> for providers targeting a specific grid configuration
 * - [Adaptive] -> for providers supporting any grid configuration
 */
sealed interface SizeProvider : GeometryProvider<Size> {

    companion object {
        /** Provider key for [SizeProvider]. */
        val key = ProviderKey<Size>("provider.size.base")
    }

    /** Base class for [SizeProvider]s with fixed grid requirements. */
    abstract class Fixed : FixedProvider<Size>(), SizeProvider {
        final override val key = SizeProvider.key

        protected override fun onAttachWith(
            digitGridSpec: GridSpec,
            geometryProps: GeometryProps,
        ) = Unit
    }

    /** Base class for [SizeProvider]s that adapt to any grid configuration. */
    abstract class Adaptive : AdaptiveProvider<Size>(), SizeProvider {
        final override val key = SizeProvider.key

        protected override fun onAttachWith(
            digitGridSpec: GridSpec,
            geometryProps: GeometryProps,
        ) = Unit
    }
}
