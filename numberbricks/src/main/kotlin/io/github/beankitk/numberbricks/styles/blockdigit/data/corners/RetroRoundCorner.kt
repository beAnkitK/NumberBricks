package io.github.beankitk.numberbricks.blockdigit.data.corners

import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.data.ShapeRadius

open class RetroRoundCorners: BlockCorners<Array<ShapeRadius>> {
    
    val nil = ShapeRadius(0f, 0f, 0f, 0f)
    val tl = ShapeRadius(1f, 0f, 0f, 0f)
    val tr = ShapeRadius(0f, 1f, 0f, 0f)
    val br = ShapeRadius(0f, 0f, 1f, 0f)
    val bl = ShapeRadius(0f, 0f, 0f, 1f)
    val tbl = ShapeRadius(1f, 0f, 0f, 1f)
    val tbr = ShapeRadius(0f, 1f, 1f, 0f)
    val tlr = ShapeRadius(1f, 1f, 0f, 0f)
    val blr = ShapeRadius(0f, 0f, 1f, 1f)
    val full = ShapeRadius(1f, 1f, 1f, 1f)
    
    override val digit0 = arrayOf(
        tl, nil, tr,
        nil, nil,
        nil, nil, nil,
        nil, nil,
        bl, nil, br
    )

    override val digit1 = arrayOf(
        tbl, tr, tr,
        nil, nil,
        nil, nil, nil,
        nil, nil,
        tbl, nil, tbr
    )

    override val digit2 = arrayOf(
        tbl, nil, tr,
        nil, nil,
        tl, nil, br,
        nil, nil,
        bl, nil, tbr
    )

    override val digit3 = arrayOf(
        tbl, nil, tr,
        nil, nil,
        tbl, nil, nil,
        nil, nil,
        tbl, nil, br
    )

    override val digit4 = arrayOf(
        tlr, tlr, tlr,
        nil, nil,
        bl, nil, nil,
        nil, nil,
        blr, blr, blr
    )

    override val digit5 = arrayOf(
        tl, nil, tbr,
        nil, tbr,
        bl, nil, tr,
        nil, nil,
        tbl, nil, br
    )

    override val digit6 = arrayOf(
        tl, nil, tbr,
        nil, tbr,
        nil, nil, tr,
        nil, nil,
        bl, nil, br
    )

    override val digit7 = arrayOf(
        tbl, nil, tr,
        nil, nil,
        tbl, tbl, nil,
        nil, nil,
        blr, blr, blr
    )

    override val digit8 = arrayOf(
        tl, nil, tr,
        nil, nil,
        nil, nil, nil,
        nil, nil,
        bl, nil, br
    )

    override val digit9 = arrayOf(
        tl, nil, tr,
        nil, nil,
        bl, nil, nil,
        tbl, nil,
        tbl, nil, br
    )
    
    override val default = Array(13) { full }
    
    override fun radiusFor(digit: Int, index: Int): ShapeRadius {
        val radius = this[digit]
        return radius[index]
    }
}