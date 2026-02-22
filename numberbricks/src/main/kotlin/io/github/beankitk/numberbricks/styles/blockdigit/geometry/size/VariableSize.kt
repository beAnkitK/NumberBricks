package io.github.beankitk.numberbricks.blockdigit.geometry.size

import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.geometry.Consent
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.Position
import io.github.beankitk.numberbricks.core.geometry.ProviderStore
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.blockdigit.geometry.position.PositionProvider

class VariableSize(
    private val eachColWidth: FloatArray,
    private val eachRowHeight: FloatArray
): SizeProvider.Adaptive() {

    private lateinit var normalizedColWidth: FloatArray
    private lateinit var normalizedRowHeight: FloatArray

    override val dependsOn: Set<ProviderKey<*>>
        get() = setOf(PositionProvider.key)

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
        val positions = providerStore.get<Position>(PositionProvider.key)
        return positions.map { position ->
            Size(
                width = normalizedColWidth[position.col],
                height = normalizedRowHeight[position.row]
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
