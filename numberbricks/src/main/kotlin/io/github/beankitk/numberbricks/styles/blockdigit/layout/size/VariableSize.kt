package io.github.beankitk.numberbricks.blockdigit.layout.size

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.beankitk.numberbricks.core.layout.LayoutScope
import io.github.beankitk.numberbricks.core.layout.ProviderKey
import io.github.beankitk.numberbricks.blockdigit.layout.offset.OffsetProvider
import io.github.beankitk.numberbricks.utils.toIntOffset

//TODO figure out it to be adpative or fixed. Also to figure out way for better error message to throws here or pass to Layout
class VariableSize(
    val eachColWidth: FloatArray,
    val eachRowHeight: FloatArray
): SizeProvider.Adaptive() {
    /*
    init {
        require(eachColWidths.size == layoutData.cols) {
            "Column widths array size (${eachColWidths.size}) must match cols (${layoutData.cols})"
        }
        require(eachRowHeights.size == layoutData.rows) {
            "Row heights array size (${eachRowHeights.size}) must match rows (${layoutData.rows})"
        }
    }
    
    override fun matchesWith(layoutInfo: LayoutInfo): Boolean {
        rowsCount = layoutInfo.rowsCount
        colsCount = layoutInfo.colsCount
        brickCount = layoutInfo.brickCount
        return true
    }
    */
    
    override val dependsOn: Set<ProviderKey<*>>
        get() = setOf(OffsetProvider.key)
        
    override fun LayoutScope.getOrComputeFor(digit: Int): List<Size> {
        val bricksOffset = getProviderDataFor<Offset>(OffsetProvider.key)
        return List(brickCount) { index ->
            val position = bricksOffset[index].toIntOffset()
            Size(
                width = eachColWidth[position.x],
                height = eachRowHeight[position.y]
            )
        }
    }
}
