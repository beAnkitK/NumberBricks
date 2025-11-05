package io.github.beankitk.numberbricks.blockdigit.data.corners

import androidx.compose.ui.geometry.CornerRadius
import io.github.beankitk.numberbricks.data.ShapeRadius

open class AbstractRoundCorners : RetroRoundCorners {
    override val digit0 = arrayOf(
        tl, tr, tr,
        nil, nil,
        nil, nil, nil,
        nil, nil,
        bl, bl, br
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
}