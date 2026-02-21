package io.github.beankitk.numberbricks.blockdigit.geometry.offset

import androidx.compose.ui.geometry.Offset
import io.github.beankitk.numberbricks.core.geometry.ProviderStore
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.buildProviderData

class DefaultOffset private constructor(
    private val offset: Offset
) : OffsetProvider.Adaptive() {

    private var cachedOffsets: List<Offset>? = null

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun getProviderData(digit: Int, providerStore: ProviderStore): List<Offset> {
        return cachedOffsets ?: buildProviderData { offset }.also {
            cachedOffsets = it
        }
    }

    companion object {
        val Zero = DefaultOffset(Offset.Zero)

        fun of(offset: Offset): DefaultOffset =
            DefaultOffset(offset)
    }
}