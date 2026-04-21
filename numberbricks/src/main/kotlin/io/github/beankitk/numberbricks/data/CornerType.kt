package io.github.beankitk.numberbricks.data

/**
 * Describes the geometric relationship of a rectangle corner to its neighbors.
 *
 * When rectangles are arranged in a grid with overlapping edges, each corner
 * can have up to three types of neighbors:
 *
 * - **Horizontal (h)**: A rectangle sharing the horizontal edge at this corner
 * - **Vertical (v)**: A rectangle sharing the vertical edge at this corner
 * - **Diagonal (d)**: A rectangle touching only at this corner point
 *
 * **Reference diagram:**
 * ```text
 *     A─────B P─────Q
 *     │      │ │      │
 *     D─────C S─────R
 *     K─────L X─────Y
 *     │      │ │      │
 *     N─────M Z─────W
 *```
 *
 * Where edges overlap: BC-PS, DC-KL, SR-XY, LM-XZ
 *
 * For [CornerPosition.BottomRight] corner **C** of rectangle **ABCD**, the neighbors are:
 *
 * | Neighbor Type | Neighbor Corner                  | Source Rectangle | Relationship                |
 * |---------------|----------------------------------|------------------|-----------------------------|
 * | Horizontal (h)| [CornerPosition.BottomLeft] -> S | PQRS             | BC <-> PS (shared edge)     |
 * | Vertical (v)  | [CornerPosition.TopRight]  -> L  | KLMN             | DC <-> KL (shared edge)     |
 * | Diagonal (d)  | [CornerPosition.TopLeft]   -> X  | XYWZ             | C -> X (point contact only) |
 *
 * The corner type describes the structural role of that corner in the overall
 * brick formation, which determines appropriate styling like corner rounding.
 */
@JvmInline
value class CornerType private constructor(val ordinal: Int) {

    override fun toString(): String = when (this) {
        Outer -> "Outer"
        Edge -> "Edge"
        CornerNeighbor -> "CornerNeighbor"
        Corner -> "Corner"
        JointInline -> "JointInline"
        Joint -> "Joint"
        Inner -> "Inner"
        else -> "Unknown($ordinal)"
    }

    companion object {
        /**
        * Corner has no neighboring rectangles.
        *
        * **Neighbor pattern:** `!h AND !v AND !d`
        * - No horizontal, vertical, or diagonal neighbors
        *
        * This is a completely free corner at the outer boundary of the brick formation.
        */
        val Outer = CornerType(0)

        /**
        * Corner sits on a straight edge shared with exactly one neighbor.
        *
        * **Neighbor pattern:** `(h XOR v) AND !d`
        * - Has either horizontal or vertical neighbor, but not both
        * - No diagonal neighbor
        *
        * This corner is on a flat boundary where two rectangles meet side-by-side.
        */
        val Edge = CornerType(1)

        /**
        * Outer corner adjacent to a corner-to-corner connection within the same rectangle.
        *
        * **Neighbor pattern:** Initially `Outer`, then refined based on sibling corners
        * - This corner is type `Outer`
        * - Either the horizontal or vertical corner of the same rect is type [Corner]
        *
        * This corner is near a point where two rectangles touch diagonally, affecting
        * how it should be styled to maintain visual consistency with the connection.
        */
        val CornerNeighbor = CornerType(2)

        /**
        * Two rectangles touch at this corner point only, with no edge overlap.
        *
        * **Neighbor pattern:** `d AND !h AND !v`
        * - Has diagonal neighbor
        * - No horizontal or vertical neighbors
        *
        * This represents a corner-to-corner connection where rectangles meet at a
        * single point without sharing edges.
        */
        val Corner = CornerType(3)

        /**
        * Corner participates in a multi-rect junction but isn't the junction center.
        *
        * **Neighbor pattern:** `d AND (h XOR v)`
        * - Has diagonal neighbor
        * - Has either horizontal or vertical neighbor, but not both
        *
        * This corner is adjacent to a junction point, forming part of the connection
        * structure but not serving as the central meeting point.
        */
        val JointInline = CornerType(4)

        /**
        * Corner is the junction point where three rectangles meet along edges.
        *
        * **Neighbor pattern:** `h AND v AND !d`
        * - Has both horizontal and vertical neighbors
        * - No diagonal neighbor
        *
        * This corner forms a L-junction where rectangles connect along their
        * edges without diagonal overlap.
        */
        val Joint = CornerType(5)

        /**
        * Corner is fully enclosed by neighbors on all sides.
        *
        * **Neighbor pattern:** `h AND v AND d`
        * - Has horizontal, vertical, and diagonal neighbors
        *
        * This is an internal corner completely surrounded by the brick formation,
        * typically occurring in the middle of densely packed grids.
        */
        val Inner = CornerType(6)

        /** List of [CornerType]. */
        val values: List<CornerType> =
            listOf(Outer, Edge, CornerNeighbor, Corner, JointInline, Joint, Inner)

        /** Create [CornerType] from its ordinal value. */
        fun from(ordinal: Int): CornerType =
            when(ordinal) {
                0 -> Outer
                1 -> Edge
                2 -> CornerNeighbor
                3 -> Corner
                4 -> JointInline
                5 -> Joint
                6 -> Inner
                else -> throw IllegalArgumentException("Unknown ordinal value = $ordinal for CornerType")
            }
    }
}