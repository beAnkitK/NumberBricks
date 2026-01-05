package io.github.beankitk.numberbricks.blockdigit.layout.size

import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.layout.LayoutScope
import io.github.beankitk.numberbricks.core.layout.ProviderKey

class DefaultSize private constructor(
    private val brickSize: Size
) : SizeProvider.Adaptive() {

    private var cachedSize: List<Size>? = null

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun LayoutScope.getOrComputeFor(digit: Int): List<Size> {
        return cachedSize ?: List(brickCount) { brickSize }.also {
            cachedSize = it
        }
    }

    companion object {
        val Zero = DefaultSize(Size.Zero)
        
        fun uniform(brickSize: Size) = DefaultSize(brickSize)
        
        fun uniform(width: Float, height: Float = width) = DefaultSize(Size(width, height))
    }
}