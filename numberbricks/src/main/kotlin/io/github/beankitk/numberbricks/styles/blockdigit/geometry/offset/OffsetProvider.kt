package io.github.beankitk.numberbricks.blockdigit.geometry.offset

import androidx.compose.ui.geometry.Offset
import io.github.beankitk.numberbricks.core.geometry.AdaptiveProvider
import io.github.beankitk.numberbricks.core.geometry.ComputedProvider
import io.github.beankitk.numberbricks.core.geometry.FixedProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProvider
import io.github.beankitk.numberbricks.core.geometry.GridSpec
import io.github.beankitk.numberbricks.core.geometry.ProviderKey

/**
 * Provides the offset of each block during geometry composition.
 *
 * An [OffsetProvider] produces an [Offset] for every block in the current digit. The returned
 * offsets determine each block's rendered position relative to its grid position.
 *
 * Offset values must be expressed in grid-relative units, where `1f` represents the width or height
 * of a single grid cell. Values greater than `1f` offset the block by more than one cell, while
 * values less than `1f` offset it by a fraction of a cell.
 *
 * Extend one of the provided base classes to create an offset provider:
 * - [Fixed] for providers that operate on a predefined grid.
 * - [Adaptive] for providers that adapt to the builder's grid constraints.
 */
sealed interface OffsetProvider {

    /** Provides access to the underlying [GeometryProvider] represented by this provider family. */
    val delegate: GeometryProvider<Offset>
        get() = when (this) {
            is Fixed -> this
            is Adaptive -> this
            is Computed -> this
        }

    /** Identifies this provider within the [OffsetProvider] family. */
    abstract val key: OffsetProvider.Key

    /**
     * Base class for [OffsetProvider]s that operate on a predefined grid.
     *
     * @param gridSpec The fixed grid constraints for this provider.
     */
    abstract class Fixed(gridSpec: GridSpec) : FixedProvider<Offset>(gridSpec), OffsetProvider

    /** Base class for [OffsetProvider]s that adapt to the builder's grid constraints. */
    abstract class Adaptive : AdaptiveProvider<Offset>(), OffsetProvider

    /**
     * Base class for [OffsetProvider]s that computes its result independently of the [GridSpec]
     * and can be shared across different [DigitBuilder].
     */
    abstract class Computed : ComputedProvider<Offset>(), OffsetProvider

    /**
     * Defines the key type for [OffsetProvider]s and the family key for the [OffsetProvider]
     * family.
     */
    interface Key : ProviderKey<Offset> {
        override val family: OffsetProvider.Key
            get() = OffsetProvider.Key

        companion object : OffsetProvider.Key
    }
}
