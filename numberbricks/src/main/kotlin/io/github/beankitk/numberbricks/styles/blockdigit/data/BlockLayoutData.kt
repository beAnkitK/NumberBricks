package io.github.beankitk.numberbricks.blockdigit.data

import io.github.beankitk.numberbricks.core.data.LayoutData

data class BlockLayoutData private constructor(
    val rows: Int,
    val cols: Int,
    val brickCount: Int
) {
    
    companion object {
        val default = BlockLayoutData(
            rows = 5,
            cols = 3,
            brickCount = 13
        )
        
        fun custom(rows: Int, cols: Int, brickCount: Int): BlockLayoutData {
            require(rows > 0 && cols > 0 && brickCount > 0) { "Rows, cols or brickCount must be positive" }
            require(brickCount <= rows * cols) { "Brick count cannot be greater than rows * cols" }
            
            return BlockLayoutData(
                rows = rows,
                cols = cols,
                brickCount = brickCount
            )
        }
    }
}