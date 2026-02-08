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

/**
 * Provides variable brick sizes based on column and row dimensions.
 *
 * Each brick's size is determined by its grid position, using per-column
 * widths and per-row heights. This allows for non-uniform brick sizing
 * where different rows or columns have different dimensions.
 *
 * Depends on [OffsetProvider] to determine each brick's grid position.
 *
 * @property eachColWidth Array of widths for each column (must match grid column count)
 * @property eachRowHeight Array of heights for each row (must match grid row count)
 */
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
