package io.github.beankitk.numberbricks.blockdigit.geometry.size

import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.geometry.ProviderStore
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.buildProviderData

class DefaultSize private constructor(
    private val blockSize: Size
) : SizeProvider.Adaptive() {

    private var cachedSize: List<Size>? = null

    override val dependsOn = emptySet<ProviderKey<*>>()

    override fun getProviderData(digit: Int, providerStore: ProviderStore): List<Size> {
        return cachedSize ?: buildProviderData { blockSize }.also {
            cachedSize = it
        }
    }

    companion object {
        val Zero = DefaultSize(Size.Zero)

        fun uniform(blockSize: Size) = DefaultSize(blockSize)

        fun uniform(width: Float, height: Float = width) = DefaultSize(Size(width, height))
    }
}