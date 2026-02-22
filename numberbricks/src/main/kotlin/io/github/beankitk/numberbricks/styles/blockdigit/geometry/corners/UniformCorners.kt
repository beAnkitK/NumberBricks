package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import io.github.beankitk.numberbricks.core.geometry.ProviderStore
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.buildProviderData
import io.github.beankitk.numberbricks.data.ShapeRadius

class UniformCorners(
    private val shapeRadius: ShapeRadius
): CornersProvider.Adaptive() {

    private var cachedShapeRadius: List<ShapeRadius>? = null

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun getProviderData(digit: Int, providerStore: ProviderStore): List<ShapeRadius> {
        return cachedShapeRadius ?: buildProviderData { shapeRadius }.also {
            cachedShapeRadius = it
        }
    }

    companion object {
        val Zero = UniformCorners(ShapeRadius.Zero)

        val Full = UniformCorners(ShapeRadius.all(1f))

        fun of(cornerRadius: Float) = UniformCorners(ShapeRadius.all(cornerRadius))
    }
}