package io.github.beankitk.numberbricks.blockdigit.geometry.size

import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.geometry.AdaptiveGridPolicy
import io.github.beankitk.numberbricks.core.geometry.BaseGeometryProvider
import io.github.beankitk.numberbricks.core.geometry.FixedGridPolicy
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.GridSpec
import io.github.beankitk.numberbricks.core.geometry.ProviderKey

/**
 * Provides the size of each block during geometry composition.
 *
 * A [SizeProvider] produces a [Size] for every block in the current digit. The
 * returned sizes are used during geometry composition to determine each block's
 * width and height.
 *
 * Size values must be expressed in grid-relative units, where `1f` represents
 * the width or height of a single grid cell. Values greater than `1f` span multiple
 * cells, while values less than `1f` occupy a fraction of a cell.
 *
 * Extend one of the provided base classes to create a size provider:
 * - [Fixed] for providers that operate on a predefined grid.
 * - [Adaptive] for providers that adapt to the builder's grid constraints.
 */
sealed class SizeProvider : BaseGeometryProvider<Size>() {

    abstract override val key: SizeProvider.Key

    /**
     * Base class for [SizeProvider]s that operate on a predefined grid.
     *
     * @param gridSpec The fixed grid constraints for this provider.
     */
    abstract class Fixed(gridSpec: GridSpec) : SizeProvider() {
        final override val providerGridPolicy = FixedGridPolicy(gridSpec)
    }

	/**
     * Base class for [SizeProvider]s that adapt to the builder's grid constraints.
     */
    abstract class Adaptive : SizeProvider() {
        final override val providerGridPolicy = AdaptiveGridPolicy
    }

    /**
     * Defines the key type for [SizeProvider]s and the family key for the [SizeProvider]
     * family.
     */
    interface Key : ProviderKey<Size> {
        override val family: SizeProvider.Key
            get() = SizeProvider.Key

        companion object : SizeProvider.Key
    }
}
