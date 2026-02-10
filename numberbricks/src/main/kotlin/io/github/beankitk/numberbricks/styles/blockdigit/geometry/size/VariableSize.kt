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
 * Each brick's size is determined by its grid position, using per-column widths
 * and per-row heights. This allows non-uniform brick sizing where different rows
 * or columns have different dimensions.
 *
 * **Requirements:**
 * 1. Depends on [OffsetProvider] to determine each brick's grid position. The offset
 *    provider must return positions in grid coordinates (not scaled), where integer
 *    parts represent row/column indices.
 * 2. Input arrays use the [Block] coordinate scale where values are relative proportions.
 *    Arrays are automatically normalized so column widths sum to the column count and
 *    row heights sum to the row count. All elements must be non-negative.
 *
 * **Example:**
 * ```
 * // Grid: 5 rows × 3 columns
 * // Input: heights [2f, 3f, 2f, 3f, 2f], widths [3f, 0.5f, 3f]
 * // Sum: heights = 12f, widths = 6.5f
 * // Target: heights sum to 5, widths sum to 3
 * // Normalized: heights [0.833f, 1.25f, 0.833f, 1.25f, 0.833f]
 * //             widths [1.385f, 0.231f, 1.385f]
 * ```
 *
 * @property eachColWidth Relative widths for each column. The array size must be equals to cols
 *                        and the values will be normalized to sum to cols.
 * @property eachRowHeight Relative heights for each row. The array size must be equals to rows
 *                        and the values will be normalized to sum to rows.
 */
class VariableSize(
    eachColWidth: FloatArray,
    eachRowHeight: FloatArray
): SizeProvider.Adaptive() {

    private val eachColWidth = eachColWidth
    private val eachRowHeight = eachRowHeight
    private lateinit var normalizedColWidth: FloatArray
    private lateinit var normalizedRowHeight: FloatArray

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

        eachColWidth.indexOfFirst { it < 0f }.takeIf { it >= 0 }
            ?.let { return Consent.Reject("Column width at index $it must be non-negative, but was ${eachColWidth[it]}") }

        eachRowHeight.indexOfFirst { it < 0f }.takeIf { it >= 0 }
            ?.let { return Consent.Reject("Row width at index $it must be non-negative, but was ${eachRowHeight[it]}") }

        return Consent.Accept
    }

    override fun onAttachWith(properties: GeometryProps) {
        normalizedColWidth = normalizeArray(eachColWidth, providerConfig.cols.toFloat())
        normalizedRowHeight = normalizeArray(eachRowHeight, providerConfig.rows.toFloat())
    }

    override fun getProviderData(digit: Int, providerStore: ProviderStore): List<Size> {
        val bricksOffset = providerStore.get<Offset>(OffsetProvider.key)
        return buildProviderData { index ->
            val position = bricksOffset[index].toIntOffset()
            Size(
                width = normalizedColWidth[position.x],
                height = normalizedRowHeight[position.y]
            )
        }
    }

    private fun normalizeArray(input: FloatArray, target: Float): FloatArray {
        val size = input.size
        var sum = input.sum()
        val out = FloatArray(size)

        if (sum <= 0f || sum.isNaN()) {
            val even = target / size
            for (i in 0 until size) out[i] = even
            return out
        }

        val scale = target / sum
        for (i in 0 until size) out[i] = input[i] * scale
        return out
    }
}
