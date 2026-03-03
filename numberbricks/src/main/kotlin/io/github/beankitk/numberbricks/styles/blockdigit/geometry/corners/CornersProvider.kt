package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.core.geometry.AdaptiveProvider
import io.github.beankitk.numberbricks.core.geometry.FixedProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.GridSpec
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.blockdigit.geometry.offset.OffsetProvider
import io.github.beankitk.numberbricks.blockdigit.geometry.size.SizeProvider
import io.github.beankitk.numberbricks.data.CornerType
import io.github.beankitk.numberbricks.data.CornerProfile
import io.github.beankitk.numberbricks.data.DigitData
import io.github.beankitk.numberbricks.data.ShapeRadius
import io.github.beankitk.numberbricks.utils.getCornerProfile

sealed interface CornersProvider: GeometryProvider<ShapeRadius> {

    companion object {
        val key = ProviderKey<ShapeRadius>("provider.corners.base")
    }

    abstract class Fixed: FixedProvider<ShapeRadius>(), CornersProvider {
        final override val key = CornersProvider.key

        protected override fun onAttachWith(digitGridSpec: GridSpec, geometryProps: GeometryProps) {}
    }

    abstract class Adaptive: AdaptiveProvider<ShapeRadius>(), CornersProvider {
        final override val key = CornersProvider.key

        protected override fun onAttachWith(digitGridSpec: GridSpec, geometryProps: GeometryProps) {}
    }
}

abstract class CustomCornersProvider(
    val radiusX: Float,
    val radiusY: Float = radiusX
): CornersProvider.Fixed(), DigitData<List<ShapeRadius>> {

    private val cornerRadius = CornerRadius(radiusX, radiusY)

    protected val zero = ShapeRadius()

    protected val tl = ShapeRadius(topLeft = cornerRadius)

    protected val tr = ShapeRadius(topRight = cornerRadius)

    protected val br = ShapeRadius(bottomRight = cornerRadius)

    protected val bl = ShapeRadius(bottomLeft = cornerRadius)

    protected val tbl = ShapeRadius(topLeft = cornerRadius, bottomLeft = cornerRadius)

    protected val tbr = ShapeRadius(topRight = cornerRadius, bottomRight = cornerRadius)

    protected val tlr = ShapeRadius(topLeft = cornerRadius, topRight = cornerRadius)

    protected val blr = ShapeRadius(bottomLeft = cornerRadius, bottomRight = cornerRadius)

    protected val full = ShapeRadius.all(cornerRadius)

    override val dependsOn = emptySet<ProviderKey<*>>()

    final override fun ProviderScope.provideData() = this@CustomCornersProvider[digit]

}

abstract class AutoCornersProvider : CornersProvider.Adaptive() {

    final override val dependsOn: Set<ProviderKey<*>>
        get() = setOf(OffsetProvider.key, SizeProvider.key)

    final override fun ProviderScope.provideData(): List<ShapeRadius> {
        val offsets = resultOf<Offset>(OffsetProvider.key)
        val sizes = resultOf<Size>(SizeProvider.key)
        val rects = Array(providerGridSpec.bricks) { index -> Rect(offsets[index], sizes[index]) }

        return detectCornerFor(digit, rects)
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

    protected open fun modifyCornerProfile(
        digit: Int,
        index: Int,
        profile: CornerProfile
    ): CornerProfile = profile

    protected open fun modifyShapeRadius(
        digit: Int,
        index: Int,
        shapeRadius: ShapeRadius
    ): ShapeRadius = shapeRadius

    abstract val edgeRadius: CornerRadius

    abstract val outerRadius: CornerRadius

    abstract val cornerNeighborRadius: CornerRadius

    abstract val cornerRadius: CornerRadius

    abstract val jointInlineRadius: CornerRadius

    abstract val jointRadius: CornerRadius

    abstract val innerRadius: CornerRadius
}