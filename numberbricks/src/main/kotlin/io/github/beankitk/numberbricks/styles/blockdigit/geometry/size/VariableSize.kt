package io.github.beankitk.numberbricks.blockdigit.geometry.size

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.geometry.Consent
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.ProviderStore
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.buildProviderData
import io.github.beankitk.numberbricks.blockdigit.geometry.offset.OffsetProvider
import io.github.beankitk.numberbricks.utils.toIntOffset

class VariableSize(
    val eachColWidth: FloatArray,
    val eachRowHeight: FloatArray
): SizeProvider.Adaptive() {

    override val dependsOn: Set<ProviderKey<*>>
        get() = setOf(OffsetProvider.key)

    override fun matchesWith(properties: GeometryProps): Consent {
        val layoutConfig = properties.config
        if (eachColWidth.size != layoutConfig.cols) {
            return Consent.Reject("Column widths array size (${eachColWidth.size}) must match layout columns (${layoutConfig.cols})")
        }
        if (eachRowHeight.size != layoutConfig.rows) {
            return Consent.Reject("Row heights array size (${eachRowHeight.size}) must match rows (${layoutConfig.rows})")
        }
        return Consent.Accept
    }

    override fun getProviderData(digit: Int, providerStore: ProviderStore): List<Size> {
        val bricksOffset = providerStore.get<Offset>(OffsetProvider.key)
        return buildProviderData { index ->
            val position = bricksOffset[index].toIntOffset()
            Size(
                width = eachColWidth[position.x],
                height = eachRowHeight[position.y]
            )
        }
    }
}
