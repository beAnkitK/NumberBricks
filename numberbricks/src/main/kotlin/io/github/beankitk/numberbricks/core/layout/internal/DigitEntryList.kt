package io.github.beankitk.numberbricks.core.layout.internal

import androidx.collection.MutableLongList
import androidx.collection.mutableLongListOf
import io.github.beankitk.numberbricks.core.layout.DigitEntry
import kotlin.ranges.IntRange

@JvmInline
internal value class DigitEntryList(
    private val longList: MutableLongList = mutableLongListOf()
) {
    public inline val size: Int
        get() = longList.size

    public inline val lastIndex: Int
        get() = longList.lastIndex

    public inline val indices: IntRange
        get() = longList.indices

    public operator fun get(index: Int): DigitEntry {
        return DigitEntry(longList.get(index))
    }

    public operator fun set(index: Int, digitEntry: DigitEntry): DigitEntry {
        val oldDigitEntry = longList.set(index, digitEntry.packed)
        return DigitEntry(oldDigitEntry)
    }

    public fun elementAt(index: Int): DigitEntry {
        return DigitEntry(longList.elementAt(index))
    }

    public fun push(digitEntry: DigitEntry): Boolean {
        return longList.add(digitEntry.packed)
    }

    public fun pushAll(longArray: LongArray): Boolean {
        return longList.addAll(longArray)
    }

    public fun pushAll(digitEntryList: DigitEntryList): Boolean {
        return longList.addAll(digitEntryList.longList)
    }

    public fun popLast(): DigitEntry {
        return DigitEntry(longList.removeAt(longList.lastIndex))
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
            append("{$index: ${DigitEntry(element)}}")
        }
        append("]")
    }
}