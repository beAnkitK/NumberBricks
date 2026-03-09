@file:OptIn(ExperimentalProviderMetaApi::class)

package io.github.beankitk.numberbricks.blockdigit.geometry.size

import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.geometry.Consent
import io.github.beankitk.numberbricks.core.geometry.ExperimentalProviderMetaApi
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.GridSpec
import io.github.beankitk.numberbricks.core.geometry.MetaGroup
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderScope
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.blockdigit.geometry.position.PositionProvider
import kotlin.math.abs

open class VariableSize(
    private val eachColWidth: FloatArray,
    private val eachRowHeight: FloatArray
): SizeProvider.Adaptive() {

    private lateinit var normalizedColWidths: FloatArray
    private lateinit var normalizedRowHeights: FloatArray

    final override val dependsOn: Set<ProviderKey<*>>
        get() = setOf(PositionProvider.key)

    final override fun matchesWith(digitGridSpec: GridSpec): Consent {
        if (eachColWidth.size != digitGridSpec.cols) {
            return Consent.Reject("Column widths array size (${eachColWidth.size}) must match layout columns (${digitGridSpec.cols})")
        }
        if (eachRowHeight.size != digitGridSpec.rows) {
            return Consent.Reject("Row heights array size (${eachRowHeight.size}) must match rows (${digitGridSpec.rows})")
        }

        eachColWidth.indexOfFirst { it < 0f }.takeIf { it >= 0 }
            ?.let { return Consent.Reject("Column width at index $it must be non-negative, but was ${eachColWidth[it]}") }

        eachRowHeight.indexOfFirst { it < 0f }.takeIf { it >= 0 }
            ?.let { return Consent.Reject("Row width at index $it must be non-negative, but was ${eachRowHeight[it]}") }

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

        return positions.map { position ->
            val size = Size(
                width = colWidths[position.col],
                height = rowHeights[position.row]
            )

            modifyBlockSize(digit, position, size)
        }
    }

    protected open fun modifyBlockSize(
        digit: Int,
        position: Position,
        baseSize: Size
    ): Size = baseSize

    protected open fun modifyColumnWidths(
        digit: Int,
        colWidths: FloatArray
    ): FloatArray = colWidths

    protected open fun modifyRowHeights(
        digit: Int,
        rowHeights: FloatArray
    ): FloatArray = rowHeights

    // Helper to normalize custom width/height arrays in modification hooks.
    // Base arrays are already normalized; use only if a hook replaces them.
    protected fun normalizeRowHeights(input: FloatArray): FloatArray =
        normalizeArray(input, providerGridSpec.rows.toFloat())

    protected fun normalizeColWidths(input: FloatArray): FloatArray =
        normalizeArray(input, providerGridSpec.cols.toFloat())

    companion object Meta : MetaGroup<VariableSize>() {
        val ColWidths = defineMeta<FloatArray> { FloatArray(5) { 1f } }
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