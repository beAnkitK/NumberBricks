package io.github.beankitk.numberbricks.core.geometry

import androidx.compose.ui.util.packInts

/**
 * Represents a digit state of a single place in a number.
 *
 * A digit slot tracks the digit state at a specific position in the number
 * (e.g., ones place, tens place, hundreds place). It stores both the previous and current
 * digit values when the number updates.
 *
 * Each digit value must be in the range [0-9]. The previous digit is null when the slot
 * is newly created or represents a position that didn't exist in the previous number
 * (e.g., when a 2-digit number becomes a 3-digit number).
 *
 * In `NumberBricks`, this tracking allows morphing between previous digit to current digit.
 *
 * @property packed The packed representation of previous and current digit values
 *
 * Example usage:
 * ```
 * // Display "42" - ones place shows "2" (no previous state)
 * val onesPlace = DigitSlot(currentDigit = 2)
 *
 * // Update to "43" - ones place now shows "3", previously showed "2"
 * val updatedOnes = onesPlace.withCurrent(3)
 * // updatedOnes.previousDigit = 2, updatedOnes.currentDigit = 3
 * ```
 */
@JvmInline
value class DigitSlot(val packed: Long) {

    /**
     * Creates a digit slot with both previous and current digit values.
     *
     * @param previousDigit The digit value (0-9) that was at this place before
     * @param currentDigit The digit value (0-9) currently at this place
     * @throws IllegalArgumentException if either digit is not in the range [0-9]
     */
    constructor(
        previousDigit: Int,
        currentDigit: Int
    ) : this(packInts(previousDigit, currentDigit)) {
        require(previousDigit in 0..9) { "previousDigit must be in range [0-9], got $previousDigit" }
        require(currentDigit in 0..9) { "currentDigit must be in range [0-9], got $currentDigit" }
    }

    /**
     * Creates a digit slot with only a current digit value.
     *
     * Use this for newly created digit places or when previous state is not available.
     *
     * @param currentDigit The digit value (0-9) at this place
     * @throws IllegalArgumentException if digit is not in the range [0-9]
     */
    constructor(currentDigit: Int): this(packInts(Int.MIN_VALUE, currentDigit)) {
        require(currentDigit in 0..9) { "currentDigit must be in range [0-9], got $currentDigit" }
    }

    /**
     * The digit value (0-9) that was at this place before the number update.
     *
     * Returns null if:
     * - This is a newly created digit place with no prior state
     * - The place didn't exist in the previous number (e.g., 99 → 100)
     * - The slot was initialized without a previous digit
     */
    val previousDigit: Int?
        get() {
            val raw = (packed shr 32).toInt()
            return if (raw == Int.MIN_VALUE) null else raw
        }

    /**
     * The digit value (0-9) currently at this place.
     */
    val currentDigit: Int
        get() = (packed and 0xFFFFFFFFL).toInt()

    /**
     * Creates a new digit slot by moving the current number to previous and setting
     * the new number as current number.
     *
     * The current digit becomes the previous digit, and the provided value becomes
     * the new current digit.
     *
     * @param newDigit The new digit value (0-9) for this place
     * @return A new [DigitSlot] with updated state
     * @throws IllegalArgumentException if newDigit is not in the range [0-9]
     *
     * Example:
     * ```
     * val slot = DigitSlot(currentDigit = 5)
     * val updated = slot.withCurrent(6)
     * // updated.previousDigit = 5, updated.currentDigit = 6
     *
     * val nextUpdate = updated.withCurrent(7)
     * // nextUpdate.previousDigit = 6, nextUpdate.currentDigit = 7
     * ```
     */
    fun withCurrent(newDigit: Int): DigitSlot {
        require(newDigit in 0..9) { "newDigit must be in range [0-9], got $newDigit" }
        return DigitSlot(currentDigit, newDigit)
    }

    /**
     * Checks if the previous and current digits are the same.
     *
     * @return true if both digits are identical, false if they differ or if
     *         there is no previous digit (previousDigit is null)
     */
    fun isSame(): Boolean = previousDigit == currentDigit

    override fun toString() = "[curr = $currentDigit, prev = $previousDigit]"
}