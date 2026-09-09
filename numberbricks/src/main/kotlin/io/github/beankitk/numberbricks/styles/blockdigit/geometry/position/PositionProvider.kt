package io.github.beankitk.numberbricks.blockdigit.geometry.position

import io.github.beankitk.numberbricks.core.geometry.AdaptiveProvider
import io.github.beankitk.numberbricks.core.geometry.ComputedProvider
import io.github.beankitk.numberbricks.core.geometry.FixedProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProvider
import io.github.beankitk.numberbricks.core.geometry.GridSpec
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.data.DigitData

/**
 * Provides the position of each block during geometry composition.
 *
 * A [PositionProvider] produces a [Position] for every block in the current digit. The returned
 * positions determine the row and column occupied by each block during geometry composition.
 *
 * Position values must be expressed in grid coordinates, where each position maps to a valid row
 * and column within the [ProviderScope.gridSpec].
 *
 * Extend one of the provided base classes to create a position provider:
 * - [Fixed] for providers that operate on a predefined grid.
 * - [Adaptive] for providers that adapt to the builder's grid constraints.
 */
sealed interface PositionProvider {

    /** Provides access to the underlying [GeometryProvider] represented by this provider family. */
    val delegate: GeometryProvider<Position>
        get() = when (this) {
            is Fixed -> this
            is Adaptive -> this
            is Computed -> this
        }

    /** Identifies this provider within the [PositionProvider] family. */
    abstract val key: PositionProvider.Key

    /**
     * Base class for [PositionProvider]s that operate on a predefined grid.
     *
     * @param gridSpec The fixed grid constraints for this provider.
     */
    abstract class Fixed(gridSpec: GridSpec) : FixedProvider<Position>(gridSpec), PositionProvider

    /** Base class for [PositionProvider]s that adapt to the builder's grid constraints. */
    abstract class Adaptive : AdaptiveProvider<Position>(), PositionProvider

    /**
     * Base class for [PositionProvider]s that computes its result independently of the [GridSpec]
     * and can be shared across different [DigitBuilder].
     */
    abstract class Computed : ComputedProvider<Position>(), PositionProvider

    /**
     * Defines the key type for [PositionProvider]s and the family key for the [PositionProvider]
     * family.
     */
    interface Key : ProviderKey<Position> {
        override val family: PositionProvider.Key
            get() = PositionProvider.Key

        companion object : PositionProvider.Key
    }
}

/**
 * Base implementation of [PositionProvider] for manually defining per-block position data for each
 * digit.
 *
 * This allows specifying [Position] for all blocks per digit using [DigitData], giving full control
 * over block placement in grid during geometry composition. Subclasses define provider data as a
 * list of [Position] for each digit, aligned with the provider's [grid constraints][gridSpec].
 *
 * @param gridSpec The [GridSpec] defining the grid constraints this provider is bound to and must
 *     align its position data with
 */
abstract class CustomPositionProvider(gridSpec: GridSpec) :
    PositionProvider.Fixed(gridSpec), DigitData<List<Position>> {

    final override val dependsOn = emptySet<ProviderKey<*>>()

    final override fun ProviderScope.provide(): List<Position> =
        this@CustomPositionProvider[digit]
}
