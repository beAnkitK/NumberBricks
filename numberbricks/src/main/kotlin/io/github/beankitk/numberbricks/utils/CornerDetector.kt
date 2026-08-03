package io.github.beankitk.numberbricks.utils

import androidx.collection.LongObjectMap
import androidx.collection.MutableLongObjectMap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.util.packInts
import io.github.beankitk.numberbricks.data.CornerPosition
import io.github.beankitk.numberbricks.data.CornerProfile
import io.github.beankitk.numberbricks.data.CornerType
import kotlin.math.roundToInt

private typealias CornerKey = Long

private typealias CornerRegistry = LongObjectMap<CornerBucket>

private typealias MutableCornerRegistry = MutableLongObjectMap<CornerBucket>

/**
 * Detects the corner type of every corner in the given rectangles.
 *
 * Analyzes the spatial relationships between the rectangles, determines the neighboring corners,
 * and classifies each corner into a [CornerType]. The detected corner types for each rectangle are
 * returned as a [CornerProfile].
 *
 * @param rects The rectangles to analyze.
 * @return An array of [CornerProfile] values in the same order as the input rectangles.
 * @see CornerDetector
 */
fun detectCorners(rects: Array<Rect>): Array<CornerProfile> {
    val currentCornerRegistry: CornerRegistry = CornerDetector.buildCornerRegistry(rects)
    return Array(rects.size) { index ->
        val currentRect = rects[index]
        CornerDetector.buildCornerProfile(currentRect, currentCornerRegistry)
    }
}

/**
 * Detects the corner type of every corner in the given rectangles.
 *
 * Analyzes the spatial relationships between the rectangles, determines the neighboring corners,
 * and classifies each corner into a [CornerType]. Applies [transform] to the detected
 * [CornerProfile] of each rectangle before returning the result.
 *
 * @param rects The rectangles to analyze.
 * @param transform Transforms the detected [CornerProfile] for each rectangle. Receives the
 *   rectangle index and its detected profile.
 * @return An array of transformed [CornerProfile] values in the same order as the input rectangles.
 * @see CornerDetector
 */
inline fun detectCorners(
    rects: Array<Rect>,
    transform: (Int, CornerProfile) -> CornerProfile,
): Array<CornerProfile> {
    val currentCornerRegistry: CornerRegistry = CornerDetector.buildCornerRegistry(rects)
    return Array(rects.size) { index ->
        val currentRect = rects[index]
        val profile = CornerDetector.buildCornerProfile(currentRect, currentCornerRegistry)
        transform(index, profile)
    }
}

/**
 * Computes corner topology for a set of rectangles as [CornerProfile] by analyzing how their
 * corners connects and overlaps in 2D space.
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
 * **Limitation:** Only coincident corners are classified. Corners that overlap another rectangle's
 * edge or fall inside another rectangle are not considered, which may produce inaccurate results.
 * This can occur with variable-sized or partially overlapping rectangles.
 *
 * ### Usage
 * Use [detectCorners] and pass all participating rectangles at once and receive a one-to-one array
 * of [CornerProfile] results:
 * ```kotlin
 * val rects = Array<Rect>(15) { i ->
 *     Rect(
 *         offset = Offset(x = i % 3, y = i / 3)
 *         size = Size(width = i % 3, height = i / 3)
 *     )
 * }
 * val profiles = detectCorners(rects)
 * ```
 *
 * @see detectCorners
 * @see CornerProfile
 * @see CornerType
 * @see CornerPosition
 */
object CornerDetector {

    // Creates a spatial index of all rectangle corners. Multiple corners from
    // different rectangles may share the same position.
    @PublishedApi
    internal fun buildCornerRegistry(rects: Array<Rect>): CornerRegistry {
        val registryMap = MutableCornerRegistry(2 * rects.size + 2)
        rects.forEach { rect ->
            registryMap.registerCorner(rect.topLeft, CornerPosition.TopLeft)
            registryMap.registerCorner(rect.topRight, CornerPosition.TopRight)
            registryMap.registerCorner(rect.bottomLeft, CornerPosition.BottomLeft)
            registryMap.registerCorner(rect.bottomRight, CornerPosition.BottomRight)
        }
        return registryMap
    }

    // Registers a corner in the spatial index: increments the existing bucket at this key if one
    // is already there, or starts a new one otherwise.
    private fun MutableCornerRegistry.registerCorner(
        cornerOffset: Offset,
        cornerPosition: CornerPosition,
    ) {
        val key = keyFrom(cornerOffset)
        val cornerBucket = this[key] ?: CornerBucket()
        this[key] = cornerBucket.increment(cornerPosition)
    }

    @PublishedApi
    internal fun buildCornerProfile(rect: Rect, cornerRegistry: CornerRegistry): CornerProfile {
        val tlKey = keyFrom(rect.topLeft)
        val trKey = keyFrom(rect.topRight)
        val brKey = keyFrom(rect.bottomRight)
        val blKey = keyFrom(rect.bottomLeft)

        val tl = cornerRegistry.findCornerType(CornerPosition.TopLeft, tlKey, trKey, brKey, blKey)
        val tr = cornerRegistry.findCornerType(CornerPosition.TopRight, tlKey, trKey, brKey, blKey)
        val br =
            cornerRegistry.findCornerType(CornerPosition.BottomRight, tlKey, trKey, brKey, blKey)
        val bl =
            cornerRegistry.findCornerType(CornerPosition.BottomLeft, tlKey, trKey, brKey, blKey)

        return CornerProfile(
            topLeft = findCornerNeighbor(tl, tr, bl),
            topRight = findCornerNeighbor(tr, tl, br),
            bottomRight = findCornerNeighbor(br, bl, tr),
            bottomLeft = findCornerNeighbor(bl, br, tl),
        )
    }

    // Determines the corner type by analyzing neighbors at the same position.
    private fun CornerRegistry.findCornerType(
        cornerPosition: CornerPosition,
        topLeftKey: CornerKey,
        topRightKey: CornerKey,
        bottomRightKey: CornerKey,
        bottomLeftKey: CornerKey,
    ): CornerType {

        fun keyOf(position: CornerPosition): CornerKey =
            when (position) {
                CornerPosition.TopLeft -> topLeftKey
                CornerPosition.TopRight -> topRightKey
                CornerPosition.BottomRight -> bottomRightKey
                CornerPosition.BottomLeft -> bottomLeftKey
                else -> error("Unknown CornerPosition: $position")
            }

        val currentCornerKey = keyOf(cornerPosition)
        val bucket = this[currentCornerKey] ?: return CornerType.Outer

        val h = cornerPosition.horizontalNeighbor
        val v = cornerPosition.verticalNeighbor
        val d = cornerPosition.diagonalNeighbor

        val hasHorizontalNeighbor =
            bucket.hasNeighbor(h, excludeSelf = keyOf(h) == currentCornerKey)
        val hasVerticalNeighbor = bucket.hasNeighbor(v, excludeSelf = keyOf(v) == currentCornerKey)
        val hasDiagonalNeighbor = bucket.hasNeighbor(d, excludeSelf = keyOf(d) == currentCornerKey)

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

    // If this corner is Outer but has a neighboring corner with diagonal contact, it's classified
    // as CornerNeighbor.
    private fun findCornerNeighbor(
        currentCorner: CornerType,
        hNeighbor: CornerType,
        vNeighbor: CornerType,
    ): CornerType {
        return if (
            currentCorner == CornerType.Outer &&
                (hNeighbor == CornerType.Corner || vNeighbor == CornerType.Corner)
        ) {
            CornerType.CornerNeighbor
        } else {
            currentCorner
        }
    }

    private fun keyFrom(position: Offset): CornerKey {
        val xi = (position.x / epsilon).roundToInt()
        val yi = (position.y / epsilon).roundToInt()
        return packInts(xi, yi)
    }
}

// Tracks how many rectangles contribute each corner position at a shared point.
// The bucket packs four saturating counters (one per CornerPosition) into a single Int:
//
// Packed layout (MSB → LSB): [BottomLeft][BottomRight][TopRight][TopLeft]
//
// Each counter occupies BITS_PER_COUNT bits and records how many rectangles contribute
// that corner position at this point. Neighbor detection then queries these counters.
@JvmInline
internal value class CornerBucket(private val packed: Int = 0) {

    fun increment(position: CornerPosition): CornerBucket {
        // Extract this corner's counter from the packed value.
        val shift = position.ordinal * BITS_PER_COUNT
        val cornerCount = (packed ushr shift) and COUNT_MASK

        // Saturate instead of overflowing since we only care whether another
        // rectangle is present, not the exact number beyond the representable range.
        if (cornerCount == COUNT_MASK) return this

        // Increment only this corner's packed counter.
        return CornerBucket(packed + (1 shl shift))
    }

    fun hasNeighbor(position: CornerPosition, excludeSelf: Boolean): Boolean {
        // Read the packed counter for the requested corner orientation.
        val cornerCount = (packed ushr (position.ordinal * BITS_PER_COUNT)) and COUNT_MASK

        // Degenerate rectangles (zero width/height) contribute the same corner position
        // multiple times. When excludeSelf is true, require at least one additional
        // contributor before reporting a neighbor.
        return if (excludeSelf) cornerCount > 1 else cornerCount > 0
    }
}

private const val BITS_PER_COUNT = 8
private const val COUNT_MASK = 0xFF
