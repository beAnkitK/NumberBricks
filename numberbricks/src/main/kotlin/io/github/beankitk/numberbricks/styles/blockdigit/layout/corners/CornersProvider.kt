package io.github.beankitk.numberbricks.blockdigit.layout.corners

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.core.layout.AdaptiveProvider
import io.github.beankitk.numberbricks.core.layout.FixedProvider
import io.github.beankitk.numberbricks.core.layout.LayoutProvider
import io.github.beankitk.numberbricks.core.layout.LayoutScope
import io.github.beankitk.numberbricks.core.layout.ProviderKey
import io.github.beankitk.numberbricks.blockdigit.layout.offset.OffsetProvider
import io.github.beankitk.numberbricks.blockdigit.layout.size.SizeProvider
import io.github.beankitk.numberbricks.data.CornerType
import io.github.beankitk.numberbricks.data.CornerProfile
import io.github.beankitk.numberbricks.data.DigitData
import io.github.beankitk.numberbricks.data.ShapeRadius
import io.github.beankitk.numberbricks.utils.CornerDetector

sealed interface CornersProvider : LayoutProvider<ShapeRadius> {

    companion object {
        val key = ProviderKey<ShapeRadius>("provider.corners.base")
    }

    abstract class Fixed: FixedProvider<ShapeRadius>(), CornersProvider {
        final override val key = CornersProvider.key
    }

    abstract class Adaptive: AdaptiveProvider<ShapeRadius>(), CornersProvider {
        final override val key = CornersProvider.key
    }
}

abstract class CustomCornerProvider(
    val radiusX: Float,
    val radiusY: Float
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

    override fun LayoutScope.getOrComputeFor(digit: Int) = this@CustomCornerProvider[digit]

}

abstract class AutoCornerProvider : CornersProvider.Adaptive() {

    private val detector = CornerDetector()

    override val dependsOn: Set<ProviderKey<*>>
        get() = setOf(OffsetProvider.key, SizeProvider.key)

    override fun LayoutScope.getOrComputeFor(digit: Int): List<ShapeRadius> {
        val offsetList = getProviderDataFor<Offset>(OffsetProvider.key)
        val sizeList = getProviderDataFor<Size>(SizeProvider.key)
        val rectsArray = Array(brickCount) { index -> Rect(offsetList[index], sizeList[index]) }

        return detectCornerFor(digit, rectsArray)
    }

    private fun detectCornerFor(digit: Int, rects: Array<Rect>): List<ShapeRadius> {
        val cornerProfileArray = detector.getCornerProfile(
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