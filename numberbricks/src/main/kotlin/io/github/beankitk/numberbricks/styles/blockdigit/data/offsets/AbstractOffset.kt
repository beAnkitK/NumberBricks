package io.github.beankitk.numberbricks.blockdigit.data.offsets

class AbstractOffset : DefaultOffset() {

    override val digit0 = floatArrayOf(
        0f,0f, 1f,0f, 1f,0f,
        0f,1f, 2f,1f,
        0f,2f, 0f,2f, 2f,2f,
        0f,3f, 2f,3f,
        1f,4f, 1f,4f, 2f,4f
    )
    
    override val digit1 = floatArrayOf(
        0f,0f, 0f,0f, 0f,0f,
        1f,1f, 1f,1f,
        1f,2f, 1f,2f, 1f,2f,
        1f,3f, 1f,3f,
        0f,4f, 1f,4f, 2f,4f
    )

    override val digit2 = floatArrayOf(
        0f,0f, 1f,0f, 1f,0f,
        2f,1f, 2f,1f,
        0f,2f, 1f,2f, 2f,2f,
        0f,3f, 0f,3f,
        1f,4f, 1f,4f, 2f,4f
    )

    override val digit3 = floatArrayOf(
        0f,0f, 1f,0f, 1f,0f,
        2f,1f, 2f,1f,
        0f,2f, 1f,2f, 1f,2f,
        2f,3f, 2f,3f,
        0f,4f, 1f,4f, 1f,4f
    )

    override val digit4 = floatArrayOf(
        0f,0f, 0f,0f, 0f,0f,
        0f,1f, 0f,1f,
        0f,2f, 2f,2f, 2f,2f,
        1f,3f, 2f,3f,
        2f,4f, 2f,4f, 2f,4f
    )

    override val digit5 = floatArrayOf(
        1f,0f, 1f,0f, 2f,0f,
        0f,1f, 0f,1f,
        0f,2f, 1f,2f, 2f,2f,
        2f,3f, 2f,3f,
        0f,4f, 1f,4f, 1f,4f
    )

    override val digit6 = floatArrayOf(
        1f,0f, 1f,0f, 2f,0f,
        0f,1f, 0f,1f,
        0f,2f, 1f,2f, 2f,2f,
        0f,3f, 2f,3f,
        1f,4f, 1f,4f, 2f,4f
    )

    override val digit7 = floatArrayOf(
        0f,0f, 1f,0f, 2f,0f,
        2f,1f, 2f,1f,
        1f,2f, 1f,2f, 1f,2f,
        0f,3f, 0f,3f,
        0f,4f, 0f,4f, 0f,4f
    )

    override val digit8 = floatArrayOf(
        0f,0f, 1f,0f, 2f,0f,
        0f,1f, 2f,1f,
        1f,2f, 1f,2f, 1f,2f,
        0f,3f, 2f,3f,
        0f,4f, 1f,4f, 2f,4f
    )

    override val digit9 = floatArrayOf(
        0f,0f, 1f,0f, 1f,0f,
        0f,1f, 2f,1f,
        0f,2f, 1f,2f, 2f,2f,
        2f,3f, 2f,3f,
        2f,4f, 2f,4f, 2f,4f
    )
}