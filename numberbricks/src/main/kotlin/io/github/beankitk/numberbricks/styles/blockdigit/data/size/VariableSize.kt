package io.github.beankitk.numberbricks.blockdigit.data.size

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.blockdigit.data.BlockLayoutData

class VariableSize(
    layoutData: BlockLayoutData,
    val eachColWidths: FloatArray,
    val eachRowHeights: FloatArray
): SizeProvider {
    
    init {
        require(eachColWidths.size == layoutData.cols) {
            "Column widths array size (${eachColWidths.size}) must match cols (${layoutData.cols})"
        }
        require(eachRowHeights.size == layoutData.rows) {
            "Row heights array size (${eachRowHeights.size}) must match rows (${layoutData.rows})"
        }
    }
    
    override val rows = layoutData.rows
    override val cols = layoutData.cols
    override val brickCount = layoutData.brickCount
    
    override fun sizeFor(
        index: Int,
        digit: Int,
        position: Offset
    ) = Size(
        width = eachColWidths[position.x.toInt()],
        height = eachRowHeights[position.y.toInt()]
    )
}
