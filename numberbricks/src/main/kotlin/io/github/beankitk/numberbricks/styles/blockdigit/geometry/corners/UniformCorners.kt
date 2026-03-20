package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.buildProviderData
import io.github.beankitk.numberbricks.data.CornerShape
import io.github.beankitk.numberbricks.data.CornerStyle
import io.github.beankitk.numberbricks.data.RectCorners

class UniformCorners(
    private val rectCorners: RectCorners
): CornersProvider.Adaptive() {

    private var cachedRectCorners: List<RectCorners>? = null

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun ProviderScope.provideData(): List<RectCorners> {
        return cachedRectCorners ?: buildProviderData { rectCorners }.also {
            cachedRectCorners = it
        }
    }

    companion object {
        val Sharp = UniformCorners(RectCorners.Sharp)

        val Round = UniformCorners(RectCorners(1f, CornerShape.Round))

        fun of(cornerStyle: CornerStyle) = UniformCorners(RectCorners(cornerStyle))

        fun of(
            radius: Float,
            shape: CornerShape = CornerShape.Round,
            radiusY: Float = radius
        ) = of(CornerStyle(CornerRadius(radius, radiusY), shape))
    }
}