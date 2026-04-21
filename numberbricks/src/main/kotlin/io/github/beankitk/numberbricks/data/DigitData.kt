package io.github.beankitk.numberbricks.data

/**
 * Data holding helper that maps digit values (0-9) to associated data.
 *
 * Provides a type-safe way to store and retrieve data specific to each digit,
 * with a fallback default value for invalid digits or placeholder states.
 *
 * Used by [io.github.beankitk.numberbricks.core.geometry.GeometryProvider]
 * for storing digit-specific styling (colors, shapes) or geometry data that
 * varies per digit.
 *
 * Example usage:
 * ```kotlin
 * val digitColors = object : DigitData<Color> {
 *     override val digit0 = Color.Red
 *     override val digit1 = Color.Blue
 *     // ... other digits
 *     override val default = Color.Gray
 * }
 *
 * val colorFor5 = digitColors[5]  // Returns digit5 color
 * val colorForInvalid = digitColors[-1]  // Returns default color
 * ```
 *
 * @param T The type of data associated with each digit
 * @see io.github.beankitk.numberbricks.blockdigit.geometry.position.ClassicPosition
 */
interface DigitData<T> {
    val digit0: T
    val digit1: T
    val digit2: T
    val digit3: T
    val digit4: T
    val digit5: T
    val digit6: T
    val digit7: T
    val digit8: T
    val digit9: T
    val default: T

    /**
     * Returns the data for the given digit.
     *
     * @param digit The digit value (0-9), or any other value for default
     * @return The data associated with the digit, or [default] if out of range
     */
    operator fun get(digit: Int) = 
         when (digit) {
            0 -> digit0
            1 -> digit1
            2 -> digit2
            3 -> digit3
            4 -> digit4
            5 -> digit5
            6 -> digit6
            7 -> digit7
            8 -> digit8
            9 -> digit9
            else -> default
         }
}