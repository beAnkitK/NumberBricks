package io.github.beankitk.numberbricks.testing

import io.github.beankitk.numberbricks.data.CornerProfile
import io.github.beankitk.numberbricks.data.CornerType
import kotlin.test.assertEquals
import kotlin.test.fail

/** Asserts that [profiles] contains exactly [size] corner profiles. */
fun assertProfiles(profiles: Array<CornerProfile>, size: Int) {
    assertEquals(size, profiles.size, "Expected $size corner profiles but found ${profiles.size}.")
}

/**
 * Optionally asserts that [profiles] contains exactly [size] corner profiles
 * and provides an assertion scope for validating their corner types.
 *
 * Example:
 * ```kt
 * assertProfiles(profiles, 10) {
 *     rect(0).all().shouldBe(Outer)
 *     eachRect { it.shouldMatch(Inner, Edge, Corner, Outer) }
 *     corners(
 *         rect(1, 4, 5).top().shouldBe(Inner)
 *         rects(7..9).bottom().left().bottomRight().shouldBe(Corner)
 *     )
 * }
 * ```
 */
fun assertProfiles(
    profiles: Array<CornerProfile>,
    size: Int? = null,
    block: CornerProfilesAssertionScope.() -> Unit,
) {
    if (size != null) { assertEquals(size, profiles.size, "Expected $size corner profiles but found ${profiles.size}.") }
    CornerProfilesAssertionScope(profiles).block()
}

/** Allows selecting corner profiles and their corners for asserting expected [CornerType] values. */
class CornerProfilesAssertionScope(internal val profiles: Array<CornerProfile>) {

    /**
     * Selects a single corner profile for the rectangle at [index]. 
     * Example: `rect(0).shouldMatch(Outer, Outer, Outer, Inner)`
     */
    fun rect(index: Int): RectSelector =
        RectSelector(profiles, mutableSetOf(index))

    /**
     * Select multiple corner profiles for the given rectangle [indices].
     * Example: `rects(0, 1).topRight().shouldBe(CornerNeighbor)`
     */
    fun rects(vararg indices: Int): RectSelector {
        require(indices.isNotEmpty()) { "rects() requires at least one index." }
        return RectSelector(profiles, indices.toMutableSet())
    }

    /**
     * Select multiple corner profiles for the rectangles in [range].
     * Example: `rects(0..2).all().shouldBe(Edge)`
     */
    fun rects(range: IntRange): RectSelector {
        require(!range.isEmpty()) { "rects() range must not be empty." }
        return RectSelector(profiles, range.toMutableSet())
    }

    /**
     * Applies the given assertion [block] to each rectangle corner profile.
     * Example: `eachRect { it.all().shouldBe(Outer) }`
     */
    fun eachRect(block: (RectSelector) -> Unit) {
        profiles.indices.forEach { idx ->
            block(RectSelector(profiles, mutableSetOf(idx)))
        }
    }

    /**
     * Combines selected corners across multiple corner profiles to assert them together.
     * Example: `corners(rect(0).bottom(), rect(1).top()).shouldBe(Edge)`
     */
    fun corners(vararg selectors: CornerSelector): CornerSelector {
        require(selectors.isNotEmpty()) { "corners() requires at least one selector." }
        return selectors.reduce { acc, sel -> acc.merge(sel) }
    }
}

/**
 * A Selector holding corner profiles for selected rectangles to assert thier [CornerType] for the
 * complete profile or select specific corners from selected rectangles corner profile.
 */
class RectSelector internal constructor(
    private val profiles: Array<CornerProfile>,
    private val indices: MutableSet<Int>,
) {
    /** Selects the top-left corner from the selected rectangles corner profile. */
    fun topLeft() = cornerSelector(BIT_TL)

    /** Selects the top-right corner from the selected rectangles corner profile. */
    fun topRight() = cornerSelector(BIT_TR)

    /** Selects the bottom-right corner from the selected rectangles corner profile. */
    fun bottomRight() = cornerSelector(BIT_BR)

    /** Selects the bottom-left corner from the selected rectangles corner profile. */
    fun bottomLeft() = cornerSelector(BIT_BL)

    /** Selects both top corners (top-left and top-right) from the selected rectangles corner profile. */
    fun top() = cornerSelector(BIT_TL or BIT_TR)

    /** Selects both right corners (top-right and bottom-right) from the selected rectangles corner profile. */
    fun right() = cornerSelector(BIT_TR or BIT_BR)

    /** Selects both bottom corners (bottom-left and bottom-right) from the selected rectangles corner profile. */
    fun bottom() = cornerSelector(BIT_BL or BIT_BR)

    /** Selects both left corners (top-left and bottom-left) from the selected rectangles corner profile. */
    fun left() = cornerSelector(BIT_TL or BIT_BL)

    /** Selects all four corners from the selected rectangles corner profile. */
    fun all() = cornerSelector(BIT_ALL)

    /** Asserts all four corners from the selected rectangles corner profile match expected types in order. */
    fun shouldMatch(tl: CornerType, tr: CornerType, br: CornerType, bl: CornerType) {
        val sb = StringBuilder()
        var failed = false
        for (idx in indices) {
            val p = profiles.getOrElse(idx) { error("No CornerProfile found for rect at index $idx.") }
            val badBits = (if (p.topLeft != tl) BIT_TL else 0) or
                          (if (p.topRight != tr) BIT_TR else 0) or
                          (if (p.bottomRight != br) BIT_BR else 0) or
                          (if (p.bottomLeft  != bl) BIT_BL else 0)
            if (badBits != 0) {
                if (failed) sb.append("\n\n")
                failed = true
                sb.append("Rect $idx mismatch at ${badBits.shortName()}:\n\n")
                sb.append("  Expected:\n")
                sb.append("    TL=$tl TR=$tr\n")
                sb.append("    BL=$bl BR=$br\n\n")
                sb.append("  Actual:\n")
                sb.append("    TL=${p.topLeft} TR=${p.topRight}\n")
                sb.append("    BL=${p.bottomLeft} BR=${p.bottomRight}")
            }
        }
        if (failed) fail(sb.toString())
    }

    private fun cornerSelector(bits: Int) =
        CornerSelector(profiles, indices.associateWithTo(HashMap(indices.size)) { bits })
}

/** A Selector holding selected corners across corner profiles to assert their [CornerType]. */
class CornerSelector internal constructor(
    private val profiles: Array<CornerProfile>,
    private val selection: HashMap<Int, Int>,
) {
    /** Adds the top-left corner to this corner selection. */
    fun topLeft() = also { addBits(BIT_TL) }

    /** Adds the top-right corner to this corner selection. */
    fun topRight() = also { addBits(BIT_TR) }

    /** Adds the bottom-right corner to this corner selection. */
    fun bottomRight() = also { addBits(BIT_BR) }

    /** Adds the bottom-left corner to this corner selection. */
    fun bottomLeft() = also { addBits(BIT_BL) }

    /** Adds both top corners (top-left and top-right) to this corner selection. */
    fun top() = also { addBits(BIT_TL or BIT_TR) }

    /** Adds both right corners (top-right and bottom-right) to this corner selection. */
    fun right() = also { addBits(BIT_TR or BIT_BR) }

    /** Adds both bottom corners (bottom-left and bottom-right) to this corner selection. */
    fun bottom() = also { addBits(BIT_BL or BIT_BR) }

    /** Adds both left corners (top-left and bottom-left) to this corner selection. */
    fun left() = also { addBits(BIT_TL or BIT_BL) }

    /** Adds all four corners to this corner selection. */
    fun all() = also { addBits(BIT_ALL) }

    /** Asserts that every selected corner matches the [expected] corner type. */
    fun shouldBe(expected: CornerType) {
        val sb = StringBuilder()
        var failed = false

        for ((idx, assertedBits) in selection) {
            val p = profiles.getOrElse(idx) { error("No CornerProfile found for rect at index $idx.") }

            var badBits = 0
            if (assertedBits and BIT_TL != 0 && p.topLeft    != expected) badBits = badBits or BIT_TL
            if (assertedBits and BIT_TR != 0 && p.topRight   != expected) badBits = badBits or BIT_TR
            if (assertedBits and BIT_BR != 0 && p.bottomRight != expected) badBits = badBits or BIT_BR
            if (assertedBits and BIT_BL != 0 && p.bottomLeft  != expected) badBits = badBits or BIT_BL

            if (badBits == 0) continue

            if (failed) sb.append("\n\n")
            failed = true

            sb.append("Rect $idx mismatch at ${badBits.shortName()}:\n\n")

            sb.append("  Expected:\n")
            sb.append("    TL=${expectedLabel(p, assertedBits, BIT_TL, expected)}")
            sb.append(" TR=${expectedLabel(p, assertedBits, BIT_TR, expected)}\n")
            sb.append("    BL=${expectedLabel(p, assertedBits, BIT_BL, expected)}")
            sb.append(" BR=${expectedLabel(p, assertedBits, BIT_BR, expected)}\n\n")

            sb.append("  Actual:\n")
            sb.append("    TL=${p.topLeft} TR=${p.topRight}\n")
            sb.append("    BL=${p.bottomLeft} BR=${p.bottomRight}")
        }

        if (failed) fail(sb.toString())
    }

    private fun addBits(bits: Int) {
        for (key in selection.keys) selection[key] = selection[key]!! or bits
    }

    private fun expectedLabel(
        profile: CornerProfile,
        assertedBits: Int,
        bit: Int,
        expected: CornerType,
    ): String = if (assertedBits and bit != 0) "$expected" else "${profile.cornerFor(bit)}"

    internal fun merge(other: CornerSelector): CornerSelector {
        val merged = HashMap<Int, Int>(selection.size + other.selection.size)
        merged.putAll(selection)
        other.selection.forEach { (idx, bits) ->
            merged[idx] = (merged[idx] ?: 0) or bits
        }
        return CornerSelector(profiles, merged)
    }
}

private const val BIT_TL = 1
private const val BIT_TR = 2
private const val BIT_BR = 4
private const val BIT_BL = 8
private const val BIT_ALL = BIT_TL or BIT_TR or BIT_BR or BIT_BL

private fun CornerProfile.cornerFor(bit: Int): CornerType = when (bit) {
    BIT_TL -> topLeft
    BIT_TR -> topRight
    BIT_BR -> bottomRight
    BIT_BL -> bottomLeft
    else   -> error("Unknown corner bit: $bit")
}

private fun Int.shortName(): String {
    val sb = StringBuilder(8)
    if (this and BIT_TL != 0) sb.append("TL ")
    if (this and BIT_TR != 0) sb.append("TR ")
    if (this and BIT_BR != 0) sb.append("BR ")
    if (this and BIT_BL != 0) sb.append("BL ")
    return sb.trimEnd().toString()
}