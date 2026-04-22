@file:OptIn(ExperimentalProviderMetaApi::class)

package io.github.beankitk.numberbricks.blockdigit.geometry.size

import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.blockdigit.geometry.position.PositionProvider
import io.github.beankitk.numberbricks.core.geometry.Consent
import io.github.beankitk.numberbricks.core.geometry.ExperimentalProviderMetaApi
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.GridSpec
import io.github.beankitk.numberbricks.core.geometry.MetaGroup
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.core.geometry.buildProviderData
import kotlin.math.abs

/**
 * Provides variable [Size] for blocks based on row and column dimensions.
 *
 * This [SizeProvider] computes block sizes using per-column widths and per-row heights, enabling
 * non-uniform sizing across the grid. Each block’s size is derived from its [Position], making this
 * provider dependent on [PositionProvider].
 *
 * **Requirements:**
 * 1. Depends on [PositionProvider] to resolve block positions
 * 2. Size of [eachColWidth] must match total columns
 * 3. Size of [eachRowHeight] must match total rows
 * 4. All values must be ≥ 0
 *
 * Input arrays define relative proportions and are normalized during attachment so that column
 * widths and row heights sum to total column and row count respectively. These arrays can be
 * further modified during geometry composition and are published as
 * [io.github.beankitk.numberbricks.core.geometry.Meta] for inter-provider access.
 *
 * **Example:**
 *
 * ```text
 * Grid: 5 rows × 3 columns
 * Input: heights [2f, 3f, 2f, 3f, 2f], widths [3f, 0.5f, 3f]
 * Sum: heights = 12f, widths = 6.5f
 * Target: heights sum to 5, widths sum to 3
 * Normalized: heights [0.833f, 1.25f, 0.833f, 1.25f, 0.833f]
 *             widths [1.385f, 0.231f, 1.385f]
 * ```
 *
 * @param eachColWidth Relative width per column (normalized to column count)
 * @param eachRowHeight Relative height per row (normalized to row count)
 * @see VariableSize.Meta
 */
open class VariableSize(
    private val eachColWidth: FloatArray,
    private val eachRowHeight: FloatArray,
) : SizeProvider.Adaptive() {

    private lateinit var normalizedColWidths: FloatArray
    private lateinit var normalizedRowHeights: FloatArray

    final override val dependsOn: Set<ProviderKey<*>>
        get() = setOf(PositionProvider.key)

    final override fun matchesWith(digitGridSpec: GridSpec): Consent {
        if (eachColWidth.size != digitGridSpec.cols) {
            return Consent.Reject(
                "Column widths array size (${eachColWidth.size}) must match layout columns (${digitGridSpec.cols})"
            )
        }
        if (eachRowHeight.size != digitGridSpec.rows) {
            return Consent.Reject(
                "Row heights array size (${eachRowHeight.size}) must match rows (${digitGridSpec.rows})"
            )
        }

        eachColWidth
            .indexOfFirst { it < 0f }
            .takeIf { it >= 0 }
            ?.let {
                return Consent.Reject(
                    "Column width at index $it must be non-negative, but was ${eachColWidth[it]}"
                )
            }

        eachRowHeight
            .indexOfFirst { it < 0f }
            .takeIf { it >= 0 }
            ?.let {
                return Consent.Reject(
                    "Row width at index $it must be non-negative, but was ${eachRowHeight[it]}"
                )
            }

        return Consent.Accept
    }

    final override fun onAttachWith(digitGridSpec: GridSpec, geometryProps: GeometryProps) {
        normalizedColWidths = normalizeArray(eachColWidth, providerGridSpec.cols.toFloat())
        normalizedRowHeights = normalizeArray(eachRowHeight, providerGridSpec.rows.toFloat())
    }

    final override fun ProviderScope.provideData(): List<Size> {
        val positions = resultOf<Position>(PositionProvider.key)
        val colWidths = modifyColumnWidths(digit, normalizedColWidths)
        val rowHeights = modifyRowHeights(digit, normalizedRowHeights)

        provideMeta {
            Meta.ColWidths providedBy colWidths
            Meta.RowHeights providedBy rowHeights
        }

        return buildProviderData { index ->
            val position = positions[index]
            val size = Size(width = colWidths[position.col], height = rowHeights[position.row])

            modifyBlockSize(digit, position, size)
        }
    }

    /**
     * Allows modification of block size after base size computation.
     *
     * This method is called for each block during geometry composition to allow adjustment of its
     * final size. The provided [baseSize] is the computed size; the returned value is used
     * directly.
     *
     * @param digit The digit being composed
     * @param position The grid position of the block
     * @param baseSize The computed size before modification
     * @return Final size to be used for the block
     */
    protected open fun modifyBlockSize(digit: Int, position: Position, baseSize: Size): Size =
        baseSize

    /**
     * Allows modification of column widths before size computation.
     *
     * This method is called once per digit before geometry composition to allow adjustment of
     * column widths. The provided [colWidths] are normalized; the returned array is used directly.
     *
     * The returned array must:
     * - Match the column count
     * - Contain only non-negative values
     *
     * If overriding, ensure the array is normalized, or use [normalizeColWidths] and cache the
     * result to avoid repeated normalization.
     *
     * @param digit The digit being composed
     * @param colWidths Normalized column widths
     * @return Modified column widths to be used for size computation
     */
    protected open fun modifyColumnWidths(digit: Int, colWidths: FloatArray): FloatArray = colWidths

    /**
     * Allows modification of row heights before size computation.
     *
     * This method is called once per digit before geometry composition to allow adjustment of row
     * heights. The provided [rowHeights] are normalized; the returned array is used directly.
     *
     * The returned array must:
     * - Match the row count
     * - Contain only non-negative values
     *
     * If overriding, ensure the array is normalized, or use [normalizeRowHeights] and cache the
     * result to avoid repeated normalization.
     *
     * @param digit The digit being composed
     * @param rowHeights Normalized row heights
     * @return Modified row heights to be used for size computation
     */
    protected open fun modifyRowHeights(digit: Int, rowHeights: FloatArray): FloatArray = rowHeights

    /**
     * Scales row height weights proportionally so that their total equals the number of rows. Each
     * value then represents its proportional share in grid units (where 1f = one row).
     *
     * @param input The array of row height weights to be normalized
     * @return A FloatArray scaled so its sum equals the row count
     */
    protected fun normalizeRowHeights(input: FloatArray): FloatArray =
        normalizeArray(input, providerGridSpec.rows.toFloat())

    /**
     * Scales column width weights proportionally so that their total equals the number of columns.
     * Each value then represents its proportional share in grid units (where 1f = one column).
     *
     * @param input The array of column width weights to be normalized
     * @return A FloatArray scaled so its sum equals the column count
     */
    protected fun normalizeColWidths(input: FloatArray): FloatArray =
        normalizeArray(input, providerGridSpec.cols.toFloat())

    /**
     * Meta group for [VariableSize], defining all keys published during provider execution.
     *
     * These keys allow other providers to access computed column widths and row heights within the
     * same [ProviderScope].
     */
    companion object Meta : MetaGroup<VariableSize>() {
        /** Normalized widths for each column used during size computation. */
        val ColWidths = defineMeta<FloatArray> { FloatArray(5) { 1f } }

        /** Normalized heights for each row used during size computation. */
        val RowHeights = defineMeta<FloatArray> { FloatArray(5) { 1f } }
    }
}

private const val epsilon = 0.001f

private fun normalizeArray(input: FloatArray, target: Float): FloatArray {
    val size = input.size
    var sum = input.sum()

    if (!sum.isNaN() && abs(sum - target) < epsilon) {
        return input
    }

    if (sum <= 0f || sum.isNaN()) {
        val even = target / size
        return FloatArray(size) { even }
    }

    val scale = target / sum
    return FloatArray(size) { input[it] * scale }
}
