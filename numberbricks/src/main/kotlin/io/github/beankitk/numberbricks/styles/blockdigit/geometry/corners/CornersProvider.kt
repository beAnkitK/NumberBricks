package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
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
import io.github.beankitk.numberbricks.data.CornerStyle
import io.github.beankitk.numberbricks.data.DigitData
import io.github.beankitk.numberbricks.data.RectCorners
import io.github.beankitk.numberbricks.utils.getCornerProfile

sealed interface CornersProvider: GeometryProvider<RectCorners> {

    companion object {
        val key = ProviderKey<RectCorners>("provider.corners.base")
    }

    abstract class Fixed: FixedProvider<RectCorners>(), CornersProvider {
        final override val key = CornersProvider.key

        protected override fun onAttachWith(digitGridSpec: GridSpec, geometryProps: GeometryProps) {}
    }

    abstract class Adaptive: AdaptiveProvider<RectCorners>(), CornersProvider {
        final override val key = CornersProvider.key

        protected override fun onAttachWith(digitGridSpec: GridSpec, geometryProps: GeometryProps) {}
    }
}

abstract class CustomCornersProvider(
    protected val cornerStyle: CornerStyle
): CornersProvider.Fixed(), DigitData<List<RectCorners>> {

    protected val zero = RectCorners()

    protected val tl = RectCorners(topLeft = cornerStyle)

    protected val tr = RectCorners(topRight = cornerStyle)

    protected val br = RectCorners(bottomRight = cornerStyle)

    protected val bl = RectCorners(bottomLeft = cornerStyle)

    protected val tbl = RectCorners(topLeft = cornerStyle, bottomLeft = cornerStyle)

    protected val tbr = RectCorners(topRight = cornerStyle, bottomRight = cornerStyle)

    protected val tlr = RectCorners(topLeft = cornerStyle, topRight = cornerStyle)

    protected val blr = RectCorners(bottomLeft = cornerStyle, bottomRight = cornerStyle)

    protected val full = RectCorners(cornerStyle)

    final override val dependsOn = emptySet<ProviderKey<*>>()

    final override fun ProviderScope.provideData() = this@CustomCornersProvider[digit]
}

abstract class AutoCornersProvider : CornersProvider.Adaptive() {

    final override val dependsOn: Set<ProviderKey<*>>
        get() = setOf(OffsetProvider.key, SizeProvider.key)

    final override fun ProviderScope.provideData(): List<RectCorners> {
        val offsets = resultOf<Offset>(OffsetProvider.key)
        val sizes = resultOf<Size>(SizeProvider.key)
        val rects = Array(providerGridSpec.brickCount) { index -> Rect(offsets[index], sizes[index]) }

        val cornerProfileArray = getCornerProfile(
            rects = rects,
            modifyProfile = { idx, profile -> modifyCornerProfile(digit, idx, profile) }
        )

        return cornerProfileArray.mapIndexed { index, profile ->
            val rectCorners = RectCorners(
                topLeft = findCornerStyle(profile.topLeft),
                topRight = findCornerStyle(profile.topRight),
                bottomRight = findCornerStyle(profile.bottomRight),
                bottomLeft = findCornerStyle(profile.bottomLeft)
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

    protected open fun modifyCornerProfile(
        digit: Int,
        index: Int,
        cornerProfile: CornerProfile
    ): CornerProfile = cornerProfile

    protected open fun modifyRectCorners(
        digit: Int,
        index: Int,
        rectCorners: RectCorners
    ): RectCorners = rectCorners

    abstract val edgeCornerStyle: CornerStyle

    abstract val outerCornerStyle: CornerStyle

    abstract val cornerNeighborCornerStyle: CornerStyle

    abstract val cornerCornerStyle: CornerStyle

    abstract val jointInlineCornerStyle: CornerStyle

    abstract val jointCornerStyle: CornerStyle

    abstract val innerCornerStyle: CornerStyle
}