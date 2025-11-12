package io.github.beankitk.numberbricks.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import io.github.beankitk.numberbricks.data.CornerPoint
import io.github.beankitk.numberbricks.data.CornerProfile
import io.github.beankitk.numberbricks.data.CornerType

typealias CornerEntry = Pair<Int, CornerPoint>
typealias CornerRegistry = Map<String, List<CornerEntry>>

class CornerDetector {
	
    private val epsilon = 0.001f
    private var cornerRegistry: CornerRegistry = emptyMap()
    private val cornerRelations: Map<CornerPoint, Triple<CornerPoint, CornerPoint, CornerPoint>> =
        mapOf(
            CornerPoint.TopLeft to Triple(
                CornerPoint.TopRight,
                CornerPoint.BottomLeft,
                CornerPoint.BottomRight
            ),
            CornerPoint.TopRight to Triple(
                CornerPoint.TopLeft,
                CornerPoint.BottomRight,
                CornerPoint.BottomLeft
            ),
            CornerPoint.BottomRight to Triple(
                CornerPoint.BottomLeft,
                CornerPoint.TopRight,
                CornerPoint.TopLeft
            ),
            CornerPoint.BottomLeft to Triple(
                CornerPoint.BottomRight,
                CornerPoint.TopLeft,
                CornerPoint.TopRight
            )
        )
    
    fun getCornerProfile(
        rects: Array<Rect>, 
        modifyProfile: ((Int, CornerProfile) -> CornerProfile)? = null
    ): Array<CornerProfile> {
        cornerRegistry = indexCornersFor(rects)
        
        return Array(rects.size) { index ->
            val currentRect = rects[index]
            
            val tl = findCornerType(currentRect, index, CornerPoint.TopLeft)
            val tr = findCornerType(currentRect, index, CornerPoint.TopRight)
            val br = findCornerType(currentRect, index, CornerPoint.BottomRight)
            val bl = findCornerType(currentRect, index, CornerPoint.BottomLeft)
            
            val profile = CornerProfile(
                topLeft = findCornerNeighbor(tl, tr, bl),
                topRight = findCornerNeighbor(tr, tl, br),
                bottomRight = findCornerNeighbor(br, bl, tr),
                bottomLeft = findCornerNeighbor(bl, br, tl)
            )
            
            modifyProfile?.invoke(index, profile) ?: profile
        }
    }
    
    private fun indexCornersFor(rects: Array<Rect>): CornerRegistry = 
        buildMap<String, MutableList<CornerEntry>> {
            rects.forEachIndexed { index, rect ->
                addCorner(index, rect.topLeft, CornerPoint.TopLeft, this)
                addCorner(index, rect.topRight, CornerPoint.TopRight, this)
                addCorner(index, rect.bottomLeft, CornerPoint.BottomLeft, this)
                addCorner(index, rect.bottomRight, CornerPoint.BottomRight, this)
            }
        }
    
    private fun addCorner(
        index: Int,
        offset: Offset,
        cornerPoint: CornerPoint,
        cornerRegistry: MutableMap<String, MutableList<CornerEntry>>
    ) {
        val key = keyFrom(offset)
        cornerRegistry.getOrPut(key) { mutableListOf() }
            .add(index to cornerPoint)
    }
    
    private fun findCornerType(
        rect: Rect,
        rectIndex: Int,
        cornerPoint: CornerPoint
    ): CornerType {
        val cornerOffset = when (cornerPoint) {
            CornerPoint.TopLeft -> rect.topLeft
            CornerPoint.TopRight -> rect.topRight
            CornerPoint.BottomRight -> rect.bottomRight
            CornerPoint.BottomLeft -> rect.bottomLeft
        }
        val (horizontalCorner, verticalCorner, diagonalCorner) = cornerRelations.getValue(cornerPoint)
        
        val cornersAtOffset = cornerRegistry[keyFrom(cornerOffset)] ?: emptyList()
        val cornerNeighbors = cornersAtOffset.filter { (index, _ ) -> index != rectIndex }
        
        val hasHorizontalNeighbor = cornerNeighbors.any { (_, cp) -> cp == horizontalCorner }
        val hasVerticalNeighbor = cornerNeighbors.any { (_, cp) -> cp == verticalCorner }
        val hasDiagonalNeighbor = cornerNeighbors.any { (_, cp) -> cp == diagonalCorner }
        
        return classifyCorner(hasHorizontalNeighbor, hasVerticalNeighbor, hasDiagonalNeighbor)
    }
    
    private fun classifyCorner(h: Boolean, v: Boolean, d: Boolean): CornerType {
        return when {
            h && v         -> if (d) CornerType.Inner else CornerType.Joint
            d && (h xor v) -> CornerType.JointInline
            d              -> CornerType.Corner
            h xor v        -> CornerType.Edge
            else           -> CornerType.Outer
        }
    }
    
    private inline fun findCornerNeighbor(
        currentCorner: CornerType,
        hNeighbor: CornerType,
        vNeighbor: CornerType
    ) = if (currentCorner == CornerType.Outer && setOf(hNeighbor, vNeighbor).contains(CornerType.Corner))
        CornerType.CornerNeighbor else currentCorner
    

    private fun keyFrom(position: Offset): String {
        val x = (position.x / epsilon).toInt() * epsilon
        val y = (position.y / epsilon).toInt() * epsilon
        return "${x}_${y}"
    }
}