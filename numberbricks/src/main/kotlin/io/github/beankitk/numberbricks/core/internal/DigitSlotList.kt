@file:Suppress("NOTHING_TO_INLINE")

package io.github.beankitk.numberbricks.core.internal

import androidx.collection.MutableLongList
import androidx.collection.mutableLongListOf
import io.github.beankitk.numberbricks.core.geometry.DigitSlot
import kotlin.ranges.IntRange

/**
 * Efficient list implementation for storing digit slots in LSD to MSD order.
 *
 * Wraps a [MutableLongList] to store [DigitSlot] instances without boxing overhead. Since DigitSlot
 * is an inline value class backed by a Long, this provides memory-efficient storage for digit
 * transition tracking.
 *
 * Though it is named as List but behaves similiar like a Stack in a way that it only allows to
 * add(push) or remove(pop) elements from the end only. While retains behaviour to get or modify
 * elements at any index or clear the whole list.
 *
 * Used internally by the number composer to maintain the ordered list of digit slots representing
 * each position in the displayed number.
 *
 * @property longList The backing storage for packed digit slot data
 */
@JvmInline
internal value class DigitSlotList(private val longList: MutableLongList = mutableLongListOf()) {
    /** The number of digit slots in this list. */
    public inline val size: Int
        get() = longList.size

    /** The index of the last digit slot, or -1 if the list is empty. */
    public inline val lastIndex: Int
        get() = longList.lastIndex

    /** The range of valid indices for this list. */
    public inline val indices: IntRange
        get() = longList.indices

    /** Returns `true` if the [DigitSlotList] has no elements in it or `false` otherwise. */
    public inline fun isEmpty(): Boolean = size == 0

    /**
     * Returns the digit slot at the specified index.
     *
     * @param index The position in the list
     * @return The digit slot at that position
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    public operator fun get(index: Int): DigitSlot {
        return DigitSlot(longList.get(index))
    }

    /**
     * Replaces the digit slot at the specified index.
     *
     * @param index The position to update
     * @param digitSlot The new digit slot value
     * @return The previous digit slot at that position
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    public operator fun set(index: Int, digitSlot: DigitSlot): DigitSlot {
        val oldDigitSlot = longList.set(index, digitSlot.packed)
        return DigitSlot(oldDigitSlot)
    }

    /**
     * Returns the digit slot at the specified index.
     *
     * Alternative to [get] operator for explicit access.
     *
     * @param index The position in the list
     * @return The digit slot at that position
     * @throws IndexOutOfBoundsException if index is out of bounds
     */
    public fun elementAt(index: Int): DigitSlot {
        return DigitSlot(longList.elementAt(index))
    }

    /**
     * Appends a digit slot to the end of the list.
     *
     * @param digitSlot The digit slot to add
     * @return true
     */
    public fun push(digitSlot: DigitSlot): Boolean {
        return longList.add(digitSlot.packed)
    }

    /**
     * Appends multiple digit slots from a packed long array.
     *
     * @param longArray Array of packed digit slot values
     * @return `true` if [DigitSlotList] was changed or `false` if [longArray] was empty
     */
    public fun pushAll(longArray: LongArray): Boolean {
        return longList.addAll(longArray)
    }

    /**
     * Appends all digit slots from another list.
     *
     * @param digitSlotList The source list to copy from
     * @return `true` if [DigitSlotList] was changed or `false` if [digitSlotList] was empty
     */
    public fun pushAll(digitSlotList: DigitSlotList): Boolean {
        return longList.addAll(digitSlotList.longList)
    }

    /**
     * Removes and returns the last digit slot in the list.
     *
     * @return The removed digit slot
     * @throws NoSuchElementException if the list is empty
     */
    public fun popLast(): DigitSlot {
        if (isEmpty())
            throw NoSuchElementException("Cannot remove last element, digitSlotList is empty")
        return DigitSlot(longList.removeAt(longList.lastIndex))
    }

    /**
     * Removes a range of digit slots from [start] (inclusive) to [end] (exclusive) from the list.
     *
     * @param start The first index to remove (inclusive)
     * @param end The end of the range (exclusive), defaults to list size
     * @throws IndexOutOfBoundsException if either start or end are out of bounds
     * @throws IllegalArgumentException if [start] is greater than [end]
     */
    public fun popRangeFrom(start: Int, end: Int = longList.size) = longList.removeRange(start, end)

    /** Removes all digit slots from the list. */
    public fun clear() {
        longList.clear()
    }

    /**
     * Returns a string representation showing each digit slot with its index.
     *
     * Format: `[{0: [curr = 5, prev = 3]}, {1: [curr = 2, prev = 1]}, ...]`
     */
    override fun toString() = buildString {
        append("[")
        longList.forEachIndexed { index, element ->
            if (index != 0) append(",")
            append("{$index: ${DigitSlot(element)}}")
        }
        append("]")
    }
}
