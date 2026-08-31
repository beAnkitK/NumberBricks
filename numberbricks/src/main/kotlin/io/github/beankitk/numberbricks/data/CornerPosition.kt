package io.github.beankitk.numberbricks.data

/** Identifies a specific corner of a rectangle. */
@JvmInline
value class CornerPosition private constructor(val ordinal: Int) {

    override fun toString(): String =
        when (this) {
            TopLeft -> "TopLeft"
            TopRight -> "TopRight"
            BottomRight -> "BottomRight"
            BottomLeft -> "BottomLeft"
            else -> "Unknown($ordinal)"
        }

    companion object {
        /** Top-left corner of a rectangle. */
        val TopLeft = CornerPosition(0)

        /** Top-right corner of a rectangle. */
        val TopRight = CornerPosition(1)

        /** Bottom-right corner of a rectangle. */
        val BottomRight = CornerPosition(2)

        /** Bottom-left corner of a rectangle. */
        val BottomLeft = CornerPosition(3)

        /** List of [CornerPosition]. */
        val values: List<CornerPosition> = listOf(TopLeft, TopRight, BottomRight, BottomLeft)

        /** Create [CornerPosition] from its ordinal value. */
        fun from(ordinal: Int): CornerPosition =
            when (ordinal) {
                0 -> TopLeft
                1 -> TopRight
                2 -> BottomRight
                3 -> BottomLeft
                else ->
                    throw IllegalArgumentException(
                        "Unknown ordinal value = $ordinal for CornerPosition"
                    )
            }
    }
}
