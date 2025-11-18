package io.github.beankitk.numberbricks.blockdigit.data.corners

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.data.CornerType
import io.github.beankitk.numberbricks.data.CornerProfile
import io.github.beankitk.numberbricks.data.DigitData
import io.github.beankitk.numberbricks.data.ShapeRadius
import io.github.beankitk.numberbricks.blockdigit.data.ProviderData
import io.github.beankitk.numberbricks.blockdigit.data.createArray
import io.github.beankitk.numberbricks.utils.CornerDetector

interface CornersProvider : ProviderData {
    
    fun radiusFor(digit: Int, bricks: Array<Rect>): Array<ShapeRadius>
}

abstract class CustomCornerProvider(
    val radiusX: Float,
    val radiusY: Float
) : CornersProvider, DigitData<Array<ShapeRadius>> {
    
    private val cornerRadius = CornerRadius(radiusX, radiusY)
    
    val zero = ShapeRadius()
    val tl = ShapeRadius(topLeft = cornerRadius)
    val tr = ShapeRadius(topRight = cornerRadius)
    val br = ShapeRadius(bottomRight = cornerRadius)
    val bl = ShapeRadius(bottomLeft = cornerRadius)
    val tbl = ShapeRadius(topLeft = cornerRadius, bottomLeft = cornerRadius)
    val tbr = ShapeRadius(topRight = cornerRadius, bottomRight = cornerRadius)
    val tlr = ShapeRadius(topLeft = cornerRadius, topRight = cornerRadius)
    val blr = ShapeRadius(bottomLeft = cornerRadius, bottomRight = cornerRadius)
    val full = ShapeRadius.all(cornerRadius)
    
    final override val isAdaptive = false
    override val default = createArray { full }
    
    override fun radiusFor(digit: Int, bricks: Array<Rect>) = this[digit]
    
}

abstract class AutoCornerProvider : CornersProvider {

    private val detector = CornerDetector()
    
    final override val rows = 0
    final override val cols = 0
    final override val brickCount = 0
    final override val isAdaptive = true
    
    override fun radiusFor(digit: Int, bricks: Array<Rect>) =
        detectCornerFor(digit, bricks)
    
    private fun detectCornerFor(digit: Int, bricks: Array<Rect>): Array<ShapeRadius> {
        val cornerProfileArray = detector.getCornerProfile(
            rects = bricks,
            modifyProfile = { idx, profile -> modifyCornerProfile(digit, idx, profile) }
        )
        
        return cornerProfileArray.mapIndexed { index, cp ->
            val shapeRadius = ShapeRadius(
                radiusForType(cp.topLeft),
                radiusForType(cp.topRight),
                radiusForType(cp.bottomRight),
                radiusForType(cp.bottomLeft)
            )
            
            modifyShapeRadius(digit, index, shapeRadius)
        }.toTypedArray()
    }
    
    private fun radiusForType(type: CornerType) =
        when (type) {
            CornerType.Edge -> edgeRadius
            CornerType.Outer -> outerRadius
            CornerType.CornerNeighbor -> cornerNeighborRadius
            CornerType.Corner -> cornerRadius
            CornerType.JointInline -> jointInlineRadius
            CornerType.Joint -> jointRadius
            CornerType.Inner -> innerRadius
        }
    
    protected open fun modifyCornerProfile(
        digit: Int,
        index: Int,
        profile: CornerProfile
    ): CornerProfile = profile
    
    protected open fun modifyShapeRadius(
        digit: Int,
        index: Int,
        shapeRadius: ShapeRadius
    ): ShapeRadius = shapeRadius
    
    
    abstract val edgeRadius: CornerRadius
    abstract val outerRadius: CornerRadius
    abstract val cornerNeighborRadius: CornerRadius
    abstract val cornerRadius: CornerRadius
    abstract val jointInlineRadius: CornerRadius
    abstract val jointRadius: CornerRadius
    abstract val innerRadius: CornerRadius
}