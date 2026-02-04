package io.github.beankitk.numberbricks.blockdigit.geometry.corners

import io.github.beankitk.numberbricks.core.geometry.ProviderStore
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.buildProviderData
import io.github.beankitk.numberbricks.data.ShapeRadius

open class DefaultCorners private constructor(
    private val cornerRadius: ShapeRadius
): CornersProvider.Adaptive() {

    private var cachedCornerRadius: List<ShapeRadius>? = null

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun getProviderData(digit: Int, providerStore: ProviderStore): List<ShapeRadius> {
        return cachedCornerRadius ?: buildProviderData { cornerRadius }.also {
            cachedCornerRadius = it
        }
    }

    companion object {
        val zero = DefaultCorners(ShapeRadius.Zero)
        val full = DefaultCorners(ShapeRadius.all(1f))

        fun uniform(cornerRadius: Float) = DefaultCorners(ShapeRadius.all(cornerRadius))
    }
}