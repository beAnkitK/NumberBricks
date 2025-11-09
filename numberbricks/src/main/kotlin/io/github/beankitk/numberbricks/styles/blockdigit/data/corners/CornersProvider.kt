package io.github.beankitk.numberbricks.blockdigit.data.corners

import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.data.DigitData
import io.github.beankitk.numberbricks.data.ShapeRadius

interface CornersProvider {
    
    fun radiusFor(index: Int, digit: Int): ShapeRadius
}

abstract class BaseCornerProvider(
    val radiusX: Float,
    val radiusY: Float
) : CornersProvider, DigitData<Array<ShapeRadius>> {
    
    private val cornerRadius = CornerRadius(radiusX, radiusY)
    
    val zero = ShapeRadius()
    
    val tl = ShapeRadius(topLeft = cornerRadius)
    val tr = ShapeRadius(topRight = cornerRadius)
    val br = ShapeRadius(bottomRight = cornerRadius)
    val bl = ShapeRadius(bottomLeft = cornerRadius)
    val tbl = ShapeRadius(
        topLeft = cornerRadius,
        bottomLeft = cornerRadius
    )
    val tbr = ShapeRadius(
        topRight = cornerRadius,
        bottomRight = cornerRadius
    )
    val tlr = ShapeRadius(
        topLeft = cornerRadius,
        topRight = cornerRadius
    )
    val blr = ShapeRadius(
        bottomLeft = cornerRadius,
        bottomRight = cornerRadius
    )
    val full = ShapeRadius.all(cornerRadius)
    
    override val default = Array(13) { full }
    
    override fun radiusFor(index: Int, digit: Int) = this[digit][index]
}