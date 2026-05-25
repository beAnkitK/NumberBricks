package io.github.beankitk.numberbricks.blockdigit.geometry.size

import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.blockdigit.geometry.position.PositionProvider
import io.github.beankitk.numberbricks.core.geometry.Consent
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.GridSpec
import io.github.beankitk.numberbricks.core.geometry.NumberComposer
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.core.geometry.buildProviderData
import io.github.beankitk.numberbricks.core.geometry.defineMeta
import kotlin.math.abs

/**
 * Provides variable [Size] values for blocks based on row and column dimensions.
 *
 * This [SizeProvider] computes block sizes using per-column widths and per-row heights to provide
 * non-uniform sizing across the grid. It uses the block's [Position] to resolve its size from the
 * column-width and row-height arrays. The resulting size is passed through [transformSize] when
 * provided, to produce the final size.
 *
 * **Requirements:**
 * 1. Depends on [PositionProvider] to resolve block positions
 * 2. Size of [eachColWidth] must match the total number of columns
 * 3. Size of [eachRowHeight] must match the total number of rows
 * 4. All values must be finite and greater than or equal to 0
 *
 * Input arrays define relative proportions and are normalized during attachment so that column widths
 * and row heights sum to the total column and row counts respectively. These arrays can be further
 * transformed during geometry composition through [transformColWidths] and [transformRowHeights],
 * and are published as [io.github.beankitk.numberbricks.core.geometry.Meta] for inter-provider access.
 *
 * **Note:** This provider does not observe mutations to the input arrays. Values are resolved during
 * [attach], after which size computation remains unchanged unless transformations are provided.
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
 * @param eachColWidth Relative width per column (normalized to the column count).
 * @param eachRowHeight Relative height per row (normalized to the row count).
 * @param transformColWidths Optional transformation applied to column widths before block sizes
 *   are computed. The transformation receives the digit and normalized column widths, and returns
 *   the column widths to use. The returned array is used directly and must match the column count,
 *   contain only non-negative values, and be normalized. Use [normalizeColWidths] if needed.
 * @param transformRowHeights Optional transformation applied to row heights before block sizes
 *   are computed. The transformation receives the digit and normalized row heights, and returns
 *   the row heights to use. The returned array is used directly and must match the row count,
 *   contain only non-negative values, and be normalized. Use [normalizeRowHeights] if needed.
 * @param transformSize Optional transformation applied to each computed block size. The
 *   transformation receives the digit, block position, and base size, and returns the final size.
 * @see PositionProvider
 */
// TODO: Validate arrays returned from modifying hooks with minimal overhead
class VariableSize(
    private val eachColWidth: FloatArray,
    private val eachRowHeight: FloatArray,
    private val transformColWidths:
        ((digit: Int, colWidths: FloatArray) -> FloatArray)? = null,
    private val transformRowHeights:
        ((digit: Int, rowHeights: FloatArray) -> FloatArray)? = null,
    private val transformSize:
        ((digit: Int, position: Position, baseSize: Size) -> Size)? = null
) : SizeProvider.Adaptive() {

    private lateinit var normalizedColWidths: FloatArray
    private lateinit var normalizedRowHeights: FloatArray

    override val key: SizeProvider.Key
        get() = VariableSize.Key

    override val dependsOn: Set<ProviderKey<*>> = setOf(PositionProvider.Key)

    override fun doMatch(digitGridSpec: GridSpec): Consent {
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
            .firstInvalidIndex()
            .takeIf { it >= 0 }
            ?.let {
                return Consent.Reject(
                    "Column width at index $it must be finite and non-negative, but was ${eachColWidth[it]}"
                )
            }

        eachRowHeight
            .firstInvalidIndex()
            .takeIf { it >= 0 }
            ?.let {
                return Consent.Reject(
                    "Row width at index $it must be finite and non-negative, but was ${eachRowHeight[it]}"
                )
            }

        return Consent.Accept
    }

    override fun onAttach(digitGridSpec: GridSpec, geometryProps: GeometryProps) {
        normalizedColWidths = normalizeArray(eachColWidth, providerGridSpec.cols.toFloat())
        normalizedRowHeights = normalizeArray(eachRowHeight, providerGridSpec.rows.toFloat())
    }

    override fun ProviderScope.provideData(): List<Size> {
        val positions = resultOf<Position>(PositionProvider.Key)
        var colWidths = normalizedColWidths
        var rowHeights = normalizedRowHeights

        if (transformColWidths != null) colWidths = transformColWidths(digit, normalizedColWidths)
        if (transformRowHeights != null) rowHeights = transformRowHeights(digit, normalizedRowHeights)

        provideMeta {
            ColWidths providedBy colWidths
            RowHeights providedBy rowHeights
        }

        return buildProviderData { index ->
            val position = positions[index]
            val baseSize = Size(width = colWidths[position.col], height = rowHeights[position.row])

            transformSize?.invoke(digit, position, baseSize) ?: baseSize
        }
    }

    companion object {
        /** Normalized widths for each column used during size computation. */
        val ColWidths = defineMeta<VariableSize, FloatArray>()

        /** Normalized heights for each row used during size computation. */
        val RowHeights = defineMeta<VariableSize, FloatArray>()
    }

    /** Key identifying the [VariableSize] provider within the [SizeProvider] family. */
    object Key : SizeProvider.Key {
        override fun toString(): String = "VariableSize"
    }
}

/**
 * Scales row height weights proportionally so that their sum equals [NumberComposer.digitGridSpec.rowCount].
 * Each value represents the row's proportional height in grid units, where `1f` equals one row.
 *
 * @param input Row height weights to normalize.
 * @param rowCount The row count from the [NumberComposer.digitGridSpec].
 * @return A [FloatArray] whose values are proportionally scaled and sum to [NumberComposer.digitGridSpec.rowCount].
 */
fun normalizeRowHeights(input: FloatArray, rowCount: Int): FloatArray =
    normalizeArray(input, rowCount.toFloat())

/**
 * Scales column width weights proportionally so that their sum equals [NumberComposer.digitGridSpec.colCount].
 * Each value represents the column's proportional width in grid units, where `1f` equals one column.
 *
 * @param input Column width weights to normalize.
 * @param colCount The column count from the [NumberComposer.digitGridSpec].
 * @return A [FloatArray] whose values are proportionally scaled and sum to [NumberComposer.digitGridSpec.colCount].
 */
fun normalizeColWidths(input: FloatArray, colCount: Int): FloatArray =
    normalizeArray(input, colCount.toFloat())

private const val epsilon = 0.001f

private fun normalizeArray(input: FloatArray, target: Float): FloatArray {
    val size = input.size
    var sum = input.sum()

    if (!sum.isNaN() && abs(sum - target) < epsilon) {
        return input.copyOf()
    }

    if (sum <= 0f || sum.isNaN()) {
        val even = target / size
        return FloatArray(size) { even }
    }

    val scale = target / sum
    return FloatArray(size) { input[it] * scale }
}

private fun FloatArray.firstInvalidIndex(): Int =
    indexOfFirst { it < 0f || it.isNaN() || it.isInfinite() }
