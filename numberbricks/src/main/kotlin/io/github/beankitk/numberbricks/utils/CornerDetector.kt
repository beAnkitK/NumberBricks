package io.github.beankitk.numberbricks.utils

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.util.packInts
import io.github.beankitk.numberbricks.data.CornerPosition
import io.github.beankitk.numberbricks.data.CornerProfile
import io.github.beankitk.numberbricks.data.CornerType

private typealias CornerKey = Long

private typealias CornerRegistry = Map<CornerKey, List<CornerEntry>>

/**
 * Computes corner profiles for an array of rectangles.
 *
 * Analyzes the spatial relationships between rectangles and classifies each corner based on its
 * neighbors. Optionally allows custom modification of computed profiles.
 *
 * @param rects Array of rectangles to analyze
 * @param modifyProfile Optional function to customize the computed profile for each rectangle
 * @return Array of corner profiles, one per input rectangle
 * @see CornerDetector
 */
@Suppress("NOTHING_TO_INLINE")
inline fun getCornerProfile(
    rects: Array<Rect>,
    noinline modifyProfile: ((Int, CornerProfile) -> CornerProfile)? = null,
): Array<CornerProfile> {
    return CornerDetector.getCornerProfile(rects, modifyProfile)
}

/**
 * Computes corner topology for a set of rectangles as [CornerProfile] by analyzing how their
 * corners overlap in 2D space.
 *
 * [CornerDetector] inspects how all corner points of the supplied rectangles coincide in 2D space
 * and computes a [CornerProfile] for each rectangle. Each profile describes the structural role of
 * the rectangle’s four corners (for example, outer, edge, joint, or inner) based on neighboring
 * rectangles(horizontal, vertical, and diagonal) that touch the same point.
 *
 * The detector is geometry-focused and rendering-agnostic. It does not perform any drawing or
 * layout itself; instead, it provides semantic information that consumers may use to drive
 * behaviors such as corner rounding, border merging, clipping, or visual continuity in composed
 * layouts.
 *
 * Corner detection is performed for all rectangles as a group. The returned array preserves the
 * input order, producing one [CornerProfile] per rectangle.
 *
 * ### Usage
 * Typical usage is to pass all participating rectangles at once and receive a one-to-one array of
 * [CornerProfile] results:
 * ```kotlin
 * val rects = Array<Rect>(15) { i ->
 *     Rect(
 *         offset = Offset(x = i % 3, y = i / 3)
 *         size = Size(width = i % 3, height = i /3)
 *     )
 * }
 * val profiles = CornerDetector.getCornerProfile(rects)
 * ```
 *
 * @see CornerProfile
 * @see CornerType
 * @see CornerPosition
 */
@Immutable
object CornerDetector {
    private val epsilon = 0.001f
    // Maps each corner to its (horizontal, vertical, diagonal) neighbor corners of other rects.
    private val cornerRelations:
        Map<CornerPosition, Triple<CornerPosition, CornerPosition, CornerPosition>> =
        mapOf(
            CornerPosition.TopLeft to
                Triple(
                    CornerPosition.TopRight,
                    CornerPosition.BottomLeft,
                    CornerPosition.BottomRight,
                ),
            CornerPosition.TopRight to
                Triple(
                    CornerPosition.TopLeft,
                    CornerPosition.BottomRight,
                    CornerPosition.BottomLeft,
                ),
            CornerPosition.BottomRight to
                Triple(CornerPosition.BottomLeft, CornerPosition.TopRight, CornerPosition.TopLeft),
            CornerPosition.BottomLeft to
                Triple(CornerPosition.BottomRight, CornerPosition.TopLeft, CornerPosition.TopRight),
        )

    /**
     * Computes corner profiles for an array of rectangles.
     *
     * Analyzes the spatial relationships between rectangles and classifies each corner based on its
     * neighbors. Optionally allows custom modification of computed profiles.
     *
     * @param rects Array of rectangles to analyze
     * @param modifyProfile Optional function to customize the computed profile for each rectangle
     * @return Array of corner profiles, one per input rectangle
     */
    fun getCornerProfile(
        rects: Array<Rect>,
        modifyProfile: ((Int, CornerProfile) -> CornerProfile)? = null,
    ): Array<CornerProfile> {
        val currentCornerRegistry: CornerRegistry = indexCornersFor(rects)

        return Array(rects.size) { index ->
            val currentRect = rects[index]

            val tl =
                currentCornerRegistry.findCornerType(currentRect, index, CornerPosition.TopLeft)
            val tr =
                currentCornerRegistry.findCornerType(currentRect, index, CornerPosition.TopRight)
            val br =
                currentCornerRegistry.findCornerType(currentRect, index, CornerPosition.BottomRight)
            val bl =
                currentCornerRegistry.findCornerType(currentRect, index, CornerPosition.BottomLeft)

            val profile =
                CornerProfile(
                    topLeft = findCornerNeighbor(tl, tr, bl),
                    topRight = findCornerNeighbor(tr, tl, br),
                    bottomRight = findCornerNeighbor(br, bl, tr),
                    bottomLeft = findCornerNeighbor(bl, br, tl),
                )

            modifyProfile?.invoke(index, profile) ?: profile
        }
    }

    // Creates a spatial index of all rectangle corners. Multiple corners from
    // different rectangles may share the same position.
    private fun indexCornersFor(rects: Array<Rect>): CornerRegistry {
        val registryMap = HashMap<CornerKey, MutableList<CornerEntry>>(rects.size * 4)
        rects.forEachIndexed { index, rect ->
            addCorner(index, rect.topLeft, CornerPosition.TopLeft, registryMap)
            addCorner(index, rect.topRight, CornerPosition.TopRight, registryMap)
            addCorner(index, rect.bottomLeft, CornerPosition.BottomLeft, registryMap)
            addCorner(index, rect.bottomRight, CornerPosition.BottomRight, registryMap)
        }
        return registryMap
    }

    // Adds a corner to the spatial index.
    private fun addCorner(
        index: Int,
        offset: Offset,
        cornerPosition: CornerPosition,
        cornerRegistry: MutableMap<CornerKey, MutableList<CornerEntry>>,
    ) {
        val key = keyFrom(offset)
        cornerRegistry.getOrPut(key) { mutableListOf() }.add(CornerEntry.of(index, cornerPosition))
    }

    // Determines the corner type by analyzing neighbors at the same position.
    private fun CornerRegistry.findCornerType(
        rect: Rect,
        rectIndex: Int,
        cornerPosition: CornerPosition,
    ): CornerType {
        val cornerOffset =
            when (cornerPosition) {
                CornerPosition.TopLeft -> rect.topLeft
                CornerPosition.TopRight -> rect.topRight
                CornerPosition.BottomRight -> rect.bottomRight
                CornerPosition.BottomLeft -> rect.bottomLeft
                else -> error("Unknown cornerPosition : $cornerPosition")
            }
        val (horizontalCorner, verticalCorner, diagonalCorner) =
            cornerRelations.getValue(cornerPosition)

        val currentOffsetKey = keyFrom(cornerOffset)
        val cornersEntryAtOffset = this[currentOffsetKey] ?: return CornerType.Outer

        var hasHorizontalNeighbor = false
        var hasVerticalNeighbor = false
        var hasDiagonalNeighbor = false

        for ((index, cornerPosition) in cornersEntryAtOffset) {
            if (index == rectIndex) continue

            when (cornerPosition) {
                horizontalCorner -> hasHorizontalNeighbor = true
                verticalCorner -> hasVerticalNeighbor = true
                diagonalCorner -> hasDiagonalNeighbor = true
                else -> Unit
            }

            if (hasHorizontalNeighbor && hasVerticalNeighbor && hasDiagonalNeighbor) break
        }

        return classifyCorner(hasHorizontalNeighbor, hasVerticalNeighbor, hasDiagonalNeighbor)
    }

    private fun classifyCorner(h: Boolean, v: Boolean, d: Boolean): CornerType {
        return when {
            h && v -> if (d) CornerType.Inner else CornerType.Joint
            d && (h xor v) -> CornerType.JointInline
            d -> CornerType.Corner
            h xor v -> CornerType.Edge
            else -> CornerType.Outer
        }
    }

    // If this corner is Outer but has a neighboring corner
    // with diagonal contact, it's classified as CornerNeighbor.
    private fun findCornerNeighbor(
        currentCorner: CornerType,
        hNeighbor: CornerType,
        vNeighbor: CornerType,
    ): CornerType {
        return if (
            currentCorner == CornerType.Outer &&
                (hNeighbor == CornerType.Corner || vNeighbor == CornerType.Corner)
        )
            CornerType.CornerNeighbor
        else currentCorner
    }

    private fun keyFrom(position: Offset): CornerKey {
        val xi = (position.x / epsilon).toInt()
        val yi = (position.y / epsilon).toInt()
        return packInts(xi, yi)
    }
}

/**
 * Maps the index of a rectangle to [CornerPosition] packed as Int with the lower 4 bits for
 * [CornerPosition] and upper 28 bits for index of rectangle. Used internally by [CornerDetector] to
 * index the corners of rectangle for spatial analysis.
 */
@JvmInline
private value class CornerEntry(val packed: Int) {
    val rectIndex: Int
        get() = packed ushr 4

    val cornerPosition: CornerPosition
        get() = CornerPosition.from(packed and 0xF)

    operator fun component1(): Int = rectIndex

    operator fun component2(): CornerPosition = cornerPosition

    companion object {
        fun of(rectIndex: Int, cornerPosition: CornerPosition): CornerEntry {
            require(rectIndex >= 0) { "rectIndex must be >= 0" }
            return CornerEntry((rectIndex shl 4) or cornerPosition.ordinal)
        }
    }
}
