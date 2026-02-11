package io.github.beankitk.numberbricks.data

/** Creates a [CornerProfile] from given [CornerType]s of a rectangle */
fun CornerProfile(
    topLeft: CornerType,
    topRight: CornerType,
    bottomRight: CornerType,
    bottomLeft: CornerType
) = CornerProfile(packCorners(topLeft, topRight, bottomRight, bottomLeft))

/**
 * Data holder that represents the classified corner types of a rectangle.
 *
 * A [CornerProfile] holds the computed [CornerType] for each of the four
 * corners of a rectangle after spatial analysis against neighboring rectangles.
 * Used by [io.github.beankitk.numberbricks.utils.CornerDetector] that inspects how
 * rectangles intersect or align in a layout.
 *
 * @property topLeft The classified type of the top-left corner.
 * @property topRight The classified type of the top-right corner.
 * @property bottomRight The classified type of the bottom-right corner.
 * @property bottomLeft The classified type of the bottom-left corner.
 * @see CornerType
 * @see io.github.beankitk.numberbricks.utils.CornerDetector
 */
@JvmInline
value class CornerProfile internal constructor(private val packed: Int) {

    val topLeft: CornerType
        get() = CornerType.from((packed shr 0) and 0xFF)

    val topRight: CornerType
        get() = CornerType.from((packed shr 8) and 0xFF)

    val bottomRight: CornerType
        get() = CornerType.from((packed shr 16) and 0xFF)

    val bottomLeft: CornerType
        get() = CornerType.from((packed shr 24) and 0xFF)

    override fun toString(): String =
        "CornerProfile(topLeft=$topLeft, topRight=$topRight, bottomRight=$bottomRight, bottomLeft=$bottomLeft)"

    fun copy(
        topLeft: CornerType = this.topLeft,
        topRight: CornerType = this.topRight,
        bottomRight: CornerType = this.bottomRight,
        bottomLeft: CornerType = this.bottomLeft
    ) = CornerProfile(packCorners(topLeft, topRight, bottomRight, bottomLeft))
}

private fun packCorners(
    topLeft: CornerType,
    topRight: CornerType,
    bottomRight: CornerType,
    bottomLeft: CornerType
): Int {
    return (topLeft.ordinal and 0xFF) or
           ((topRight.ordinal and 0xFF) shl 8) or
           ((bottomRight.ordinal and 0xFF) shl 16) or
           ((bottomLeft.ordinal and 0xFF) shl 24)
}