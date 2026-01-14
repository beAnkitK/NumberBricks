package io.github.beankitk.numberbricks.data

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