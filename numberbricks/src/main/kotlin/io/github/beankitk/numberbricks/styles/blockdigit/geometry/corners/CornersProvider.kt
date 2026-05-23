package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.blockdigit.geometry.offset.OffsetProvider
import io.github.beankitk.numberbricks.blockdigit.geometry.size.SizeProvider
import io.github.beankitk.numberbricks.core.geometry.AdaptiveGridPolicy
import io.github.beankitk.numberbricks.core.geometry.BaseGeometryProvider
import io.github.beankitk.numberbricks.core.geometry.FixedGridPolicy
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.GridSpec
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.data.CornerProfile
import io.github.beankitk.numberbricks.data.CornerStyle
import io.github.beankitk.numberbricks.data.CornerType
import io.github.beankitk.numberbricks.data.DigitData
import io.github.beankitk.numberbricks.data.RectCorners
import io.github.beankitk.numberbricks.utils.CornerDetector
import io.github.beankitk.numberbricks.utils.getCornerProfile

/**
 * Provides the corners of each block during geometry composition.
 *
 * A [CornersProvider] produces [RectCorners] for every block in the current
 * digit. The returned values define the corner radius and shape of each block.
 *
 * Corner radius values must be expressed in grid-relative units, where `1f`
 * represents the maximum radius permitted by the block size.
 *
 * Extend one of the provided base classes to create a corners provider:
 * - [Fixed] for providers that operate on a predefined grid.
 * - [Adaptive] for providers that adapt to the builder's grid constraints.
 */
sealed class CornersProvider : BaseGeometryProvider<RectCorners>() {

    abstract override val key: CornersProvider.Key

    /**
     * Base class for [CornersProvider]s that operate on a predefined grid.
     *
     * @param gridSpec The fixed grid constraints for this provider.
     */
    abstract class Fixed(gridSpec: GridSpec) : CornersProvider() {
        final override val providerGridPolicy = FixedGridPolicy(gridSpec)
    }

    /**
     * Base class for [CornersProvider]s that adapt to the builder's grid constraints.
     */
    abstract class Adaptive : CornersProvider() {
        final override val providerGridPolicy = AdaptiveGridPolicy
    }

    /**
     * Defines the key type for [CornersProvider]s and the family key for the [CornersProvider]
     * family.
     */
    interface Key : ProviderKey<RectCorners> {
        override val family: CornersProvider.Key
            get() = CornersProvider.Key

        companion object : CornersProvider.Key
    }
}

/**
 * Base implementation of [CornersProvider] for manually defining per-block corner styling for each
 * digit.
 *
 * This allows specifying [RectCorners] for all blocks per digit using [DigitData], giving full
 * control over corner appearance during geometry composition. Subclasses define provider data as a
 * list of [RectCorners] for each digit, aligned with the provider's
 * [grid constraints][providerGridSpec].
 *
 * Predefined presets are provided to simplify common corner combinations based on the supplied
 * [cornerStyle].
 *
 * **Helper presets:**
 * - [none]: No corner styling applied
 * - [all]: All corners styled
 * - [tl], [tr], [br], [bl]: Single corner styled
 * - [tbl], [tbr], [tlr], [blr]: Two corners styled
 *
 * @param gridSpec The [GridSpec] defining the grid constraints this provider is bound to
 *   and must align its corners data with
 * @param cornerStyle The corner style used to construct corner presets
 */
abstract class CustomCornersProvider(
    gridSpec: GridSpec,
    protected val cornerStyle: CornerStyle
) : CornersProvider.Fixed(gridSpec), DigitData<List<RectCorners>> {

    /** No corner styling applied. */
    protected val none = RectCorners()

    /** Applies style to top-left corner. */
    protected val tl = RectCorners(topLeft = cornerStyle)

    /** Applies style to top-right corner. */
    protected val tr = RectCorners(topRight = cornerStyle)

    /** Applies style to bottom-right corner. */
    protected val br = RectCorners(bottomRight = cornerStyle)

    /** Applies style to bottom-left corner. */
    protected val bl = RectCorners(bottomLeft = cornerStyle)

    /** Applies style to top-left and bottom-left corners. */
    protected val tbl = RectCorners(topLeft = cornerStyle, bottomLeft = cornerStyle)

    /** Applies style to top-right and bottom-right corners. */
    protected val tbr = RectCorners(topRight = cornerStyle, bottomRight = cornerStyle)

    /** Applies style to top-left and top-right corners. */
    protected val tlr = RectCorners(topLeft = cornerStyle, topRight = cornerStyle)

    /** Applies style to bottom-left and bottom-right corners. */
    protected val blr = RectCorners(bottomLeft = cornerStyle, bottomRight = cornerStyle)

    /** Applies style to all corners. */
    protected val all = RectCorners(cornerStyle)

    final override val dependsOn = emptySet<ProviderKey<*>>()

    final override fun ProviderScope.provideData() = this@CustomCornersProvider[digit]
}

/**
 * Base [CornersProvider] that derives corner styling automatically from block adjacency and
 * neighbor relationships.
 *
 * This provider analyzes how blocks are connected within the grid using [CornerDetector] and
 * classifies each corner using [CornerType] based on its surrounding neighbors (edges, joints,
 * inner corners, etc.). Each classified type is then mapped to a corresponding [CornerStyle],
 * producing the final [RectCorners] for every block.
 *
 * It depends on [OffsetProvider] and [SizeProvider] to construct block bounds, which are used to
 * evaluate spatial relationships between blocks.
 *
 * Subclasses define a [CornerStyle] for each [CornerType], controlling how different connection
 * cases are styled across the entire geometry.
 *
 * **Customization hooks:**
 * - [modifyCornerProfile] -> adjust detected corner types before mapping
 * - [modifyRectCorners] -> adjust final corner styles per block
 *
 * In most cases, providing styles for each [CornerType] is sufficient. Override hooks only when
 * additional control over specific blocks or digits is required.
 *
 * @see CornerType
 * @see CornerDetector
 */
abstract class AutoCornersProvider : CornersProvider.Adaptive() {

    final override val dependsOn: Set<ProviderKey<*>> = setOf(OffsetProvider.Key, SizeProvider.Key)

    final override fun ProviderScope.provideData(): List<RectCorners> {
        val offsets = resultOf<Offset>(OffsetProvider.Key)
        val sizes = resultOf<Size>(SizeProvider.Key)
        val rects =
            Array(providerGridSpec.brickCount) { index -> Rect(offsets[index], sizes[index]) }

        val cornerProfileArray =
            getCornerProfile(
                rects = rects,
                modifyProfile = { idx, profile -> modifyCornerProfile(digit, idx, profile) },
            )

        return cornerProfileArray.mapIndexed { index, profile ->
            val rectCorners =
                RectCorners(
                    topLeft = findCornerStyle(profile.topLeft),
                    topRight = findCornerStyle(profile.topRight),
                    bottomRight = findCornerStyle(profile.bottomRight),
                    bottomLeft = findCornerStyle(profile.bottomLeft),
                )

            modifyRectCorners(digit, index, rectCorners)
        }
    }

    private fun findCornerStyle(cornerType: CornerType): CornerStyle =
        when (cornerType) {
            CornerType.Edge -> edgeCornerStyle
            CornerType.Outer -> outerCornerStyle
            CornerType.CornerNeighbor -> cornerNeighborCornerStyle
            CornerType.Corner -> cornerCornerStyle
            CornerType.JointInline -> jointInlineCornerStyle
            CornerType.Joint -> jointCornerStyle
            CornerType.Inner -> innerCornerStyle
            else -> error("Unknown corner-type = $cornerType")
        }

    /**
     * Allows modifying detected corner profiles before style mapping.
     *
     * Override to adjust [CornerType] classification for specific blocks or digits.
     *
     * @param digit The digit being composed (0–9, or -1 for default)
     * @param index The block index
     * @param cornerProfile The detected corner profile for the block
     * @return The modified corner profile for the block, or the original if unchanged
     */
    protected open fun modifyCornerProfile(
        digit: Int,
        index: Int,
        cornerProfile: CornerProfile,
    ): CornerProfile = cornerProfile

    /**
     * Allows modifying resolved [RectCorners] after style mapping.
     *
     * Override to apply block-specific or digit-specific adjustments.
     *
     * @param digit The digit being composed (0–9, or -1 for default)
     * @param index The block index
     * @param rectCorners The computed corner styles for the block
     * @return The modified corner styles for the block, or the original if unchanged
     */
    protected open fun modifyRectCorners(
        digit: Int,
        index: Int,
        rectCorners: RectCorners,
    ): RectCorners = rectCorners

    /** Corner Style applied to corners classified as [CornerType.Edge]. */
    abstract val edgeCornerStyle: CornerStyle

    /** Corner Style applied to corners classified as [CornerType.Outer]. */
    abstract val outerCornerStyle: CornerStyle

    /** Corner Style applied to corners classified as [CornerType.CornerNeighbor]. */
    abstract val cornerNeighborCornerStyle: CornerStyle

    /** Corner Style applied to corners classified as [CornerType.Corner]. */
    abstract val cornerCornerStyle: CornerStyle

    /** Corner Style applied to corners classified as [CornerType.JointInline]. */
    abstract val jointInlineCornerStyle: CornerStyle

    /** Corner Style applied to corners classified as [CornerType.Joint]. */
    abstract val jointCornerStyle: CornerStyle

    /** Corner Style applied to corners classified as [CornerType.Inner]. */
    abstract val innerCornerStyle: CornerStyle
}
