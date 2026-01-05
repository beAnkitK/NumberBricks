package io.github.beankitk.numberbricks.blockdigit.layout.offset

import androidx.compose.ui.geometry.Offset
import io.github.beankitk.numberbricks.core.layout.LayoutScope
import io.github.beankitk.numberbricks.core.layout.ProviderKey

class DefaultOffset private constructor(
    private val offset: Offset
) : OffsetProvider.Adaptive() {

    private var cachedOffsets: List<Offset>? = null

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun LayoutScope.getOrComputeFor(digit: Int): List<Offset> {
        return cachedOffsets ?: List(brickCount) { offset }.also {
            cachedOffsets = it
        }
    }

    companion object {
        val Zero = DefaultOffset(Offset.Zero)

        fun of(offset: Offset): DefaultOffset =
            DefaultOffset(offset)
    }
}