package io.github.beankitk.numberbricks.data

/**
 * Identifies a specific corner of a rectangle.
 */
enum class CornerPoint {
    /** Top-left corner of a rectangle.*/
    TopLeft,

    /** Top-right corner of a rectangle.*/
    TopRight,

    /** Bottom-right corner of a rectangle.*/
    BottomRight,

    /** Bottom-left corner of a rectangle.*/
    BottomLeft
}

/**
 * Describes the geometric relationship of a rectangle corner to its neighbors.
 *
 * When rectangles are arranged in a grid with overlapping edges, each corner
 * can have up to three types of neighbors:
 * - **Horizontal (h)**: A rectangle sharing the horizontal edge at this corner
 * - **Vertical (v)**: A rectangle sharing the vertical edge at this corner
 * - **Diagonal (d)**: A rectangle touching only at this corner point
 *
 * **Reference diagram:**
 * ```
 *     A─────B   P─────Q
 *     │     │   │     │
 *     D─────C   S─────R
 *     K─────L   X─────Y
 *     │     │   │     │
 *     N─────M   Z─────W
 *
 * Where edges overlap: BC-PS, DC-KL, SR-XY, LM-XZ
 *
 * For [CornerPoint.BottomRight] C of rect ABCD:
 * - [CornerPoint.BottomLeft] S (from PQRS) is the horizontal neighbor (shares edge BC-PS)
 * - [CornerPoint.TopRight] L (from KLMN) is the vertical neighbor (shares edge DC-KL)
 * - [CornerPoint.TopLeft] X (from XYWZ) is the diagonal neighbor (touches at point C)
 * ```
 *
 * The corner type describes the structural role of that corner in the overall
 * brick formation, which determines appropriate styling like corner rounding.
 */
enum class CornerType {
    /**
     * Corner sits on a straight edge shared with exactly one neighbor.
     *
     * **Neighbor pattern:** `(h XOR v) AND !d`
     * - Has either horizontal or vertical neighbor, but not both
     * - No diagonal neighbor
     *
     * This corner is on a flat boundary where two rectangles meet side-by-side.
     */
    Edge,

    /**
     * Corner has no neighboring rectangles.
     *
     * **Neighbor pattern:** `!h AND !v AND !d`
     * - No horizontal, vertical, or diagonal neighbors
     *
     * This is a completely free corner at the outer boundary of the brick formation.
     */
    Outer,

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
    CornerNeighbor,

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
    Corner,

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
    JointInline,

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
    Joint,

    /**
     * Corner is fully enclosed by neighbors on all sides.
     *
     * **Neighbor pattern:** `h AND v AND d`
     * - Has horizontal, vertical, and diagonal neighbors
     *
     * This is an internal corner completely surrounded by the brick formation,
     * typically occurring in the middle of densely packed grids.
     */
    Inner
}

/**
 * Data holder that represents the classified corner topology of a rectangle.
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
data class CornerProfile(
    val topLeft: CornerType,
    val topRight: CornerType,
    val bottomRight: CornerType,
    val bottomLeft: CornerType
)