package io.github.beankitk.numberbricks.blockdigit.geometry.offset

import androidx.compose.ui.geometry.Offset
import io.github.beankitk.numberbricks.core.geometry.ProviderStore
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.buildProviderData

class UniformOffset(
    private val offset: Offset
) : OffsetProvider.Adaptive() {

    private var cachedOffsets: List<Offset>? = null

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun provideData(digit: Int, providerStore: ProviderStore): List<Offset> {
        return cachedOffsets ?: buildProviderData { offset }.also {
            cachedOffsets = it
        }
    }

    companion object {
        val Zero = UniformOffset(Offset.Zero)

        fun of(x: Float, y: Float) = UniformOffset(Offset(x, y))
    }
}