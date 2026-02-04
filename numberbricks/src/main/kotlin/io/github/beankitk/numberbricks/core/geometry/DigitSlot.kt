package io.github.beankitk.numberbricks.core.geometry

import androidx.compose.ui.util.packInts

@JvmInline
value class DigitSlot(val packed: Long) {

    constructor(
        previousDigit: Int,
        currentDigit: Int
    ) : this(packInts(previousDigit, currentDigit))

    constructor(currentDigit: Int): this(Int.MIN_VALUE, currentDigit)

    val previousDigit: Int?
        get() {
            val raw = (packed shr 32).toInt()
            return if (raw == Int.MIN_VALUE) null else raw
        }

    val currentDigit: Int
        get() = (packed and 0xFFFFFFFFL).toInt()

    fun withCurrent(newDigit: Int): DigitSlot =
        DigitSlot(currentDigit, newDigit)

    fun isSame(): Boolean = previousDigit == currentDigit

    override fun toString() = "[curr = $currentDigit, prev = $previousDigit]"
}
