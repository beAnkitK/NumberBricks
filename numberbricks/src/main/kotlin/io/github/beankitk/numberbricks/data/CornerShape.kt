package io.github.beankitk.numberbricks.data

/**
 * Defines the geometric appearance of a corner.
 *
 * `CornerShape` specifies the shape of corner rendered, independent of its radius.
 * It controls whether edges meet sharply or transition smoothly, and supports
 * interpolation between different shape forms.
 *
 * Currently supported shapes:
 * 1. [Square]: sharp, unrounded corners
 * 2. [Round]: smooth, curved corners
 *
 * Additional shapes may be introduced in the future.
 *
 * @see CornerStyle
 * @see RectCorners
 */
@JvmInline
value class CornerShape private constructor(val ordinal: Int) {

    override fun toString(): String = when (this) {
        Square -> "Square"
        Round -> "Round"
        else -> "Unknown($ordinal)"
    }

    companion object {
        /** Sharp rectangular corner with no rounding. */
        val Square = CornerShape(0)

        /** Corner with a circular curve transition. */
        val Round = CornerShape(1)

        /** All currently defined corner shapes. */
        val values: List<CornerShape> =
            listOf(Square, Round)

        /**
         * Returns the [CornerShape] corresponding to the given [ordinal].
         *
         * @param ordinal The integer representation of the shape (0 = Square, 1 = Round)
         * @return The corresponding [CornerShape] instance
         * @throws IllegalArgumentException if the ordinal is not recognized
         */
        fun from(ordinal: Int): CornerShape =
            when (ordinal) {
                0 -> Square
                1 -> Round
                else -> throw IllegalArgumentException(
                    "Unknown ordinal value = $ordinal for CornerShape"
                )
            }
    }
}