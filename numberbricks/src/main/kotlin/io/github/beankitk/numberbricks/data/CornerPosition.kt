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

    // Used by [io.github.beankitk.numberbricks.utils.CornerDetector] to access the
    // corresponding neighbor corner positions.
    internal val horizontalNeighbor: CornerPosition
        get() = from(relations and 0b11)

    internal val verticalNeighbor: CornerPosition
        get() = from((relations ushr 2) and 0b11)

    internal val diagonalNeighbor: CornerPosition
        get() = from((relations ushr 4) and 0b11)

    private val relations: Int
        get() =
            when (ordinal) {
                0 -> TL_RELATIONS
                1 -> TR_RELATIONS
                2 -> BR_RELATIONS
                3 -> BL_RELATIONS
                else -> error("Invalid CornerPosition")
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

// Encodes the diagonal (D), vertical (V), and horizontal (H) neighbor CornerPosition
// ordinals for each CornerPosition into a single Int as 0bDD_VV_HH. Used by
// [io.github.beankitk.numberbricks.utils.CornerDetector].
private const val TL_RELATIONS = 0b10_11_01 // D=2, V=3, H=1
private const val TR_RELATIONS = 0b11_10_00 // D=3, V=2, H=0
private const val BR_RELATIONS = 0b00_01_11 // D=0, V=1, H=3
private const val BL_RELATIONS = 0b01_00_10 // D=1, V=0, H=2
