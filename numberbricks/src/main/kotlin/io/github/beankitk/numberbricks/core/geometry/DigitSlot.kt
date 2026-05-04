package io.github.beankitk.numberbricks.core.geometry

import androidx.compose.ui.util.packInts

/**
 * Represents a digit state of a single place in a number.
 *
 * A digit slot tracks the digit state at a specific position in the number (e.g., ones place, tens
 * place, hundreds place). It stores both the previous and current digit values when the number
 * updates.
 *
 * Each digit value must be in the range [0-9]. The previous digit is null when the slot is newly
 * created or represents a position that didn't exist in the previous number (e.g., when a 2-digit
 * number becomes a 3-digit number).
 *
 * In `NumberBricks`, this tracking allows morphing between previous digit to current digit.
 *
 * Example:
 * ```kotlin
 * // Display "42" - ones place shows "2" (no previous state)
 * val onesPlace = DigitSlot(currentDigit = 2)
 *
 * // Update to "43" - ones place now shows "3", previously showed "2"
 * val updatedOnes = onesPlace.withCurrent(3)
 * // updatedOnes.previousDigit = 2, updatedOnes.currentDigit = 3
 * ```
 *
 * @property packed The packed representation of previous and current digit values
 */
@JvmInline
value class DigitSlot internal constructor(val packed: Long) {

    /**
     * Creates a digit slot with both previous and current digit values.
     *
     * @param previousDigit The digit value (0-9) that was at this place before
     * @param currentDigit The digit value (0-9) currently at this place
     * @throws IllegalArgumentException if either digit is not in the range [0-9]
     */
    constructor(
        previousDigit: Int,
        currentDigit: Int,
    ) : this(packInts(previousDigit, currentDigit)) {
        requireDigitRange(previousDigit, "previousDigit")
        requireDigitRange(currentDigit, "currentDigit")
    }

    /**
     * Creates a digit slot with only a current digit value.
     *
     * Use this for newly created digit places or when previous state is not available.
     *
     * @param currentDigit The digit value (0-9) at this place
     * @throws IllegalArgumentException if digit is not in the range [0-9]
     */
    constructor(currentDigit: Int) : this(packInts(NO_PREVIOUS_DIGIT, currentDigit)) {
        requireDigitRange(currentDigit, "currentDigit")
    }

    /**
     * The digit value (0-9) that was at this place before the number update.
     *
     * Returns null if:
     * 1. This is a newly created digit place with no prior state
     * 2. The place didn't exist in the previous number (e.g., 99 -> 100)
     * 3. The slot was initialized without a previous digit
     */
    val previousDigit: Int?
        get() {
            val raw = unpackPreviousDigit(packed)
            return if (raw == NO_PREVIOUS_DIGIT) null else raw
        }

    /** The digit value (0-9) currently at this place. */
    val currentDigit: Int
        get() = unpackCurrentDigit(packed)

    /**
     * Creates a new digit slot by moving the current number to previous and setting the new number
     * as current number.
     *
     * The current digit becomes the previous digit, and the provided value becomes the new current
     * digit.
     *
     * Example:
     * ```kotlin
     * val slot = DigitSlot(currentDigit = 5)
     * val updated = slot.withCurrent(6)
     * // updated.previousDigit = 5, updated.currentDigit = 6
     *
     * val nextUpdate = updated.withCurrent(7)
     * // nextUpdate.previousDigit = 6, nextUpdate.currentDigit = 7
     * ```
     *
     * @param newDigit The new digit value (0-9) for this place
     * @return A new [DigitSlot] with updated state
     * @throws IllegalArgumentException if newDigit is not in the range [0-9]
     */
    fun withCurrent(newDigit: Int): DigitSlot {
        requireDigitRange(newDigit, "newDigit")
        return DigitSlot(currentDigit, newDigit)
    }

    /**
     * Checks if the previous and current digits are the same.
     *
     * @return true if both digits are identical, false if they differ or if there is no previous
     *   digit (previousDigit is null)
     */
    fun isSame(): Boolean = previousDigit == currentDigit

    override fun toString() = "[curr = $currentDigit, prev = $previousDigit]"

    companion object {
        /**
         * Reconstructs a [DigitSlot] from a previously obtained [DigitSlot.packed] value.
         * Use this when storing digit slots as raw longs for efficiency, then rebuilding later.
         *
         * @throws IllegalArgumentException if [packed] does not encode a valid DigitSlot
         */
        fun from(packed: Long): DigitSlot {
            val previousDigit = unpackPreviousDigit(packed)
            val currentDigit = unpackCurrentDigit(packed)

            require(previousDigit == NO_PREVIOUS_DIGIT || previousDigit in MIN_DIGIT..MAX_DIGIT) {
                "previousDigit must be null-sentinel or in range [$MIN_DIGIT-$MAX_DIGIT], was $previousDigit"
            }
            require(currentDigit in MIN_DIGIT..MAX_DIGIT) {
                "currentDigit must be in range [$MIN_DIGIT-$MAX_DIGIT], was $currentDigit"
            }
            return DigitSlot(packed)
        }
    }
}

private const val MIN_DIGIT = 0
private const val MAX_DIGIT = 9
private const val NO_PREVIOUS_DIGIT = Int.MIN_VALUE

private fun unpackPreviousDigit(packed: Long): Int {
    return (packed shr 32).toInt()
}

private fun unpackCurrentDigit(packed: Long): Int {
    return (packed and 0xFFFFFFFFL).toInt()
}

private fun requireDigitRange(value: Int, name: String) {
    require(value in MIN_DIGIT..MAX_DIGIT) {
        "$name must be in range [$MIN_DIGIT-$MAX_DIGIT], got $value"
    }
}
