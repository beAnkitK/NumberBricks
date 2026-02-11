package io.github.beankitk.numberbricks.data

/**
 * Identifies a specific corner of a rectangle.
 */
@JvmInline
value class CornerPoint private constructor(val ordinal: Int) {

    override fun toString(): String = when (this) {
        TopLeft -> "TopLeft"
        TopRight -> "TopRight"
        BottomRight -> "BottomRight"
        BottomLeft -> "BottomLeft"
        else -> "Unknown($ordinal)"
    }

    companion object {
        /** Top-left corner of a rectangle. */
        val TopLeft = CornerPoint(0)

        /** Top-right corner of a rectangle. */
        val TopRight = CornerPoint(1)

        /** Bottom-right corner of a rectangle. */
        val BottomRight = CornerPoint(2)

        /** Bottom-left corner of a rectangle. */
        val BottomLeft = CornerPoint(3)

        /** List of [CornerPoint]. */
        val values: List<CornerPoint> =
            listOf(TopLeft, TopRight, BottomRight, BottomLeft)

        /** Create [CornerPoint] from its ordinal value. */
        fun from(ordinal: Int): CornerPoint =
            when(ordinal) {
                0 -> TopLeft
                1 -> TopRight
                2 -> BottomRight
                3 -> BottomLeft
                else -> throw IllegalArgumentException("Unknown ordinal value = $ordinal for CornerPoint")
            }
    }
}