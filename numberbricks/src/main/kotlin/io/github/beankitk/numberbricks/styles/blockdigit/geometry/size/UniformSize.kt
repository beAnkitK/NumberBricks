package io.github.beankitk.numberbricks.blockdigit.geometry.size

import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.buildProviderData

class UniformSize(
    private val size: Size
) : SizeProvider.Adaptive() {

    private var cachedSize: List<Size>? = null

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun ProviderScope.provideData(): List<Size> {
        return cachedSize ?: buildProviderData { size }.also {
            cachedSize = it
        }
    }

    companion object {
        val Zero = UniformSize(Size.Zero)

        fun of(width: Float, height: Float = width) = UniformSize(Size(width, height))
    }
}