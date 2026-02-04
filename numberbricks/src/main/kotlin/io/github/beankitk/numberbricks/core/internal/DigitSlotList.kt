package io.github.beankitk.numberbricks.core.internal

import androidx.collection.MutableLongList
import androidx.collection.mutableLongListOf
import io.github.beankitk.numberbricks.core.geometry.DigitSlot
import kotlin.ranges.IntRange

@JvmInline
internal value class DigitSlotList(
    private val longList: MutableLongList = mutableLongListOf()
) {
    public inline val size: Int
        get() = longList.size

    public inline val lastIndex: Int
        get() = longList.lastIndex

    public inline val indices: IntRange
        get() = longList.indices

    public operator fun get(index: Int): DigitSlot {
        return DigitSlot(longList.get(index))
    }

    public operator fun set(index: Int, digitSlot: DigitSlot): DigitSlot {
        val oldDigitSlot = longList.set(index, digitSlot.packed)
        return DigitSlot(oldDigitSlot)
    }

    public fun elementAt(index: Int): DigitSlot {
        return DigitSlot(longList.elementAt(index))
    }

    public fun push(digitSlot: DigitSlot): Boolean {
        return longList.add(digitSlot.packed)
    }

    public fun pushAll(longArray: LongArray): Boolean {
        return longList.addAll(longArray)
    }

    public fun pushAll(digitSlotList: DigitSlotList): Boolean {
        return longList.addAll(digitSlotList.longList)
    }

    public fun popLast(): DigitSlot {
        return DigitSlot(longList.removeAt(longList.lastIndex))
    }

    public fun popRangeFrom(
        startInclusive: Int,
        endExclusive: Int = longList.size
    ) = longList.removeRange(startInclusive, endExclusive)

    public fun clear() {
        longList.clear()
    }

    override fun toString() = buildString {
        append("[")
        longList.forEachIndexed { index, element ->
            if (index != 0) append(",")
            append("{$index: ${DigitSlot(element)}}")
        }
        append("]")
    }
}