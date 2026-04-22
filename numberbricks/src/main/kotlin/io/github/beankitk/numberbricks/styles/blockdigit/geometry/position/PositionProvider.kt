package io.github.beankitk.numberbricks.blockdigit.geometry.position

import io.github.beankitk.numberbricks.core.geometry.AdaptiveProvider
import io.github.beankitk.numberbricks.core.geometry.FixedProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.GeometryProvider
import io.github.beankitk.numberbricks.core.geometry.GridSpec
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.data.DigitData

/**
 * Provides [Position] for each block during geometry composition.
 *
 * A [PositionProvider] is a [GeometryProvider] responsible for defining the grid position (row and
 * column) of each block for a given digit. These values establish the structural geometry of the
 * digit and are used during block assembly to construct the final brick model.
 *
 * Implementations must provide positions aligned with the digit grid, where each block maps to a
 * valid (row, column) within the configured [GridSpec].
 *
 * Two base implementations are provided:
 * - [Fixed] -> for providers targeting a specific grid configuration
 * - [Adaptive] -> for providers supporting any grid configuration
 */
sealed interface PositionProvider : GeometryProvider<Position> {

    companion object {
        /** Provider key for [PositionProvider]. */
        val key = ProviderKey<Position>("provider.position.base")
    }

    /** Base class for [PositionProvider]s with fixed grid requirements. */
    abstract class Fixed : FixedProvider<Position>(), PositionProvider {
        final override val key = PositionProvider.key

        protected override fun onAttachWith(
            digitGridSpec: GridSpec,
            geometryProps: GeometryProps,
        ) {}
    }

    /** Base class for [PositionProvider]s that adapt to any grid configuration. */
    abstract class Adaptive : AdaptiveProvider<Position>(), PositionProvider {
        final override val key = PositionProvider.key

        protected override fun onAttachWith(
            digitGridSpec: GridSpec,
            geometryProps: GeometryProps,
        ) {}
    }
}

/**
 * Base implementation of [PositionProvider] for manually defining per-block position data for each
 * digit.
 *
 * This allows specifying [Position] for all blocks per digit using [DigitData], giving full control
 * over block placement in grid during geometry composition. Subclasses define provider data as a
 * list of [Position] for each digit, aligned with the provider's
 * [grid constraints][providerGridSpec].
 *
 * @param providerGridSpec The [GridSpec] defining the grid constraints this provider is bound to
 *   and must align its position data with
 */
abstract class CustomPositionProvider(providerGridSpec: GridSpec) :
    PositionProvider.Fixed(), DigitData<List<Position>> {

    final override val providerGridSpec = providerGridSpec

    final override val dependsOn = emptySet<ProviderKey<*>>()

    final override fun ProviderScope.provideData(): List<Position> =
        this@CustomPositionProvider[digit]
}
