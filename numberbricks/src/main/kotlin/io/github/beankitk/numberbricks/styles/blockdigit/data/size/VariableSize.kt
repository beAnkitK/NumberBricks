package io.github.beankitk.numberbricks.blockdigit.data.size

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

open class VariableSize(
    val eachColWidths: FloatArray,
    val eachRowHeight: FloatArray
): SizeProvider {

    override fun sizeFor(
        index: Int,
        digit: Int,
        position: Offset
    ) = Size(
        width = eachColWidths[position.x.toInt()],
        height = eachRowHeight[position.y.toInt()]
    )
}
