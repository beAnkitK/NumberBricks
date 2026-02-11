package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.core.geometry.AdaptiveProvider
import io.github.beankitk.numberbricks.core.geometry.FixedProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.ProviderStore
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.blockdigit.geometry.offset.OffsetProvider
import io.github.beankitk.numberbricks.blockdigit.geometry.size.SizeProvider
import io.github.beankitk.numberbricks.data.CornerType
import io.github.beankitk.numberbricks.data.CornerProfile
import io.github.beankitk.numberbricks.data.DigitData
import io.github.beankitk.numberbricks.data.ShapeRadius
import io.github.beankitk.numberbricks.utils.getCornerProfile

/** Provides corner radius data for bricks in a digit layout.*/
sealed interface CornersProvider: GeometryProvider<ShapeRadius> {

    companion object {
        /** Provider key for corner radius data. */
        val key = ProviderKey<ShapeRadius>("provider.corners.base")
    }

    /** Base class for corner providers with fixed grid requirements. */
    abstract class Fixed: FixedProvider<ShapeRadius>(), CornersProvider {
        final override val key = CornersProvider.key

        protected override fun onAttachWith(properties: GeometryProps) {}
    }

    /** Base class for corner providers that adapt to any grid configuration. */
    abstract class Adaptive: AdaptiveProvider<ShapeRadius>(), CornersProvider {
        final override val key = CornersProvider.key

        protected override fun onAttachWith(properties: GeometryProps) {}
    }
}

/**
 * Base class for manually defining corner radii per digit.
 *
 * Provides predefined corner radius configurations for all possible combinations
 * of corners in the block. Subclasses implement [DigitData] to specify exact
 * corner radii for each brick in each digit representation.
 *
 * This approach gives full control over corner styling but requires
 * manually defining radii for all bricks across all digits.
 *
 * **Helper radius configurations:**
 * - [zero]: No rounding on any corner
 * - [full]: All corners rounded
 * - [tl], [tr], [br], [bl]: Single corner rounded
 * - [tbl], [tbr], [tlr], [blr]: Two corners rounded
 *
 * @property radiusX The horizontal radius for rounded corners
 * @property radiusY The vertical radius for rounded corners
 */
abstract class CustomCornerProvider(
    val radiusX: Float,
    val radiusY: Float
): CornersProvider.Fixed(), DigitData<List<ShapeRadius>> {

    private val cornerRadius = CornerRadius(radiusX, radiusY)

    /** No corner rounding */
    protected val zero = ShapeRadius()

    /** Top-left corner rounded */
    protected val tl = ShapeRadius(topLeft = cornerRadius)

    /** Top-right corner rounded */
    protected val tr = ShapeRadius(topRight = cornerRadius)

    /** Bottom-right corner rounded */
    protected val br = ShapeRadius(bottomRight = cornerRadius)

    /** Bottom-left corner rounded */
    protected val bl = ShapeRadius(bottomLeft = cornerRadius)

    /** Top-left and bottom-left corners rounded */
    protected val tbl = ShapeRadius(topLeft = cornerRadius, bottomLeft = cornerRadius)

    /** Top-right and bottom-right corners rounded */
    protected val tbr = ShapeRadius(topRight = cornerRadius, bottomRight = cornerRadius)

    /** Top-left and top-right corners rounded */
    protected val tlr = ShapeRadius(topLeft = cornerRadius, topRight = cornerRadius)

    /** Bottom-left and bottom-right corners rounded */
    protected val blr = ShapeRadius(bottomLeft = cornerRadius, bottomRight = cornerRadius)

    /** All corners rounded */
    protected val full = ShapeRadius.all(cornerRadius)

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun getProviderData(digit: Int, providerStore: ProviderStore) = this@CustomCornerProvider[digit]

}

/**
 * Base class for automatically detecting corner radii based on brick adjacency.
 *
 * Uses [CornerDetector] to analyze how bricks connect and applies appropriate
 * corner radii based on neighbor relationships. Each [CornerType] is mapped
 * to a specific corner radius, allowing consistent styling across all digits.
 * Subclasses define radius values for each corner type. Depends on [OffsetProvider]
 * and [SizeProvider] to construct brick rectangles for corner detection.
 *
 * **Customization hooks:**
 * - [modifyCornerProfile]
 * - [modifyShapeRadius]
 *
 * @see CornerType
 * @see CornerDetector
 */
abstract class AutoCornerProvider : CornersProvider.Adaptive() {

    override val dependsOn: Set<ProviderKey<*>>
        get() = setOf(OffsetProvider.key, SizeProvider.key)

    override fun getProviderData(digit: Int, providerStore: ProviderStore): List<ShapeRadius> {
        val offsetList = providerStore.get<Offset>(OffsetProvider.key)
        val sizeList = providerStore.get<Size>(SizeProvider.key)
        val rectsArray = Array(providerConfig.bricks) { index -> Rect(offsetList[index], sizeList[index]) }

        return detectCornerFor(digit, rectsArray)
    }

    private fun detectCornerFor(digit: Int, rects: Array<Rect>): List<ShapeRadius> {
        val cornerProfileArray = getCornerProfile(
            rects = rects,
            modifyProfile = { idx, profile -> modifyCornerProfile(digit, idx, profile) }
        )

        return cornerProfileArray.mapIndexed { index, cp ->
            val shapeRadius = ShapeRadius(
                radiusForType(cp.topLeft),
                radiusForType(cp.topRight),
                radiusForType(cp.bottomRight),
                radiusForType(cp.bottomLeft)
            )

            modifyShapeRadius(digit, index, shapeRadius)
        }
    }

    private fun radiusForType(type: CornerType) =
        when (type) {
            CornerType.Edge -> edgeRadius
            CornerType.Outer -> outerRadius
            CornerType.CornerNeighbor -> cornerNeighborRadius
            CornerType.Corner -> cornerRadius
            CornerType.JointInline -> jointInlineRadius
            CornerType.Joint -> jointRadius
            CornerType.Inner -> innerRadius
            else -> error("Unknown corner-type = $type")
        }

    /**
     * Allows modifying detected corner profiles before radius mapping.
     *
     * Override to adjust corner type classifications for specific bricks or digits.
     *
     * @param digit The digit being processed (0-9, or -1 for default)
     * @param index The brick index
     * @param profile The detected corner profile
     * @return The modified corner profile (or original if unchanged)
     */
    protected open fun modifyCornerProfile(
        digit: Int,
        index: Int,
        profile: CornerProfile
    ): CornerProfile = profile

    /**
     * Allows modifying final corner radii for specific bricks.
     *
     * Override to apply digit-specific or brick-specific adjustments to radii.
     *
     * @param digit The digit being processed (0-9, or -1 for default)
     * @param index The brick index
     * @param shapeRadius The computed shape radius
     * @return The modified shape radius (or original if unchanged)
     */
    protected open fun modifyShapeRadius(
        digit: Int,
        index: Int,
        shapeRadius: ShapeRadius
    ): ShapeRadius = shapeRadius

    /** Corner radius for Edge type corners */
    abstract val edgeRadius: CornerRadius

    /** Corner radius for Outer type corners */
    abstract val outerRadius: CornerRadius

    /** Corner radius for CornerNeighbor type corners */
    abstract val cornerNeighborRadius: CornerRadius

    /** Corner radius for Corner type corners */
    abstract val cornerRadius: CornerRadius

    /** Corner radius for JointInline type corners */
    abstract val jointInlineRadius: CornerRadius

    /** Corner radius for Joint type corners */
    abstract val jointRadius: CornerRadius

    /** Corner radius for Inner type corners */
    abstract val innerRadius: CornerRadius
}