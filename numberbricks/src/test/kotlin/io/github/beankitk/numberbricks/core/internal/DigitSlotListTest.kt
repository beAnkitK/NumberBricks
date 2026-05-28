package io.github.beankitk.numberbricks.core.internal

import io.github.beankitk.numberbricks.core.geometry.DigitSlot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DigitSlotListTest {

    @Test
    fun testGivenEmptyList_isEmpty_returnsTrue() {
        val list = DigitSlotList()
        assertTrue(list.isEmpty())
    }

    @Test
    fun testGivenNonEmptyList_isEmpty_returnsFalse() {
        val list = DigitSlotList()
        list.push(DigitSlot(1))

        assertFalse(list.isEmpty())
    }

    @Test
    fun testSize_returnsNumberOfElements() {
        val list = DigitSlotList()
        list.push(DigitSlot(1))
        list.push(DigitSlot(2))

        assertEquals(2, list.size)
    }

    @Test
    fun testIfListIsEmpty_lastIndex_returnsMinusOne() {
        val list = DigitSlotList()
        assertEquals(-1, list.lastIndex)
    }

    @Test
    fun testLastIndex_returnsLastElementIndex() {
        val list = DigitSlotList()
        list.push(DigitSlot(1))
        list.push(DigitSlot(2))

        assertEquals(1, list.lastIndex)
    }

    @Test
    fun testIfListIsEmpty_indices_returnsEmptyRange() {
        val list = DigitSlotList()
        assertEquals(IntRange.EMPTY, list.indices)
    }

    @Test
    fun testIndices_returnsElementIndexRange() {
        val list = DigitSlotList()
        list.push(DigitSlot(1))
        list.push(DigitSlot(2))
        list.push(DigitSlot(3))

        assertEquals(0..2, list.indices)
    }

    @Test
    fun testPush_addsElementToEnd() {
        val list = DigitSlotList()
        list.push(DigitSlot(1))
        list.push(DigitSlot(2))

        assertEquals(1, list[0].currentDigit)
        assertEquals(2, list[1].currentDigit)
    }

    @Test
    fun testPush_returnsTrueWhenElementIsAdded() {
        val list = DigitSlotList()
        assertTrue(list.push(DigitSlot(1)))
    }

    @Test
    fun testPush_preservesPackedDigitSlot() {
        val list = DigitSlotList()
        val slot = DigitSlot(previousDigit = 1, currentDigit = 2)
        list.push(slot)

        assertEquals(slot.packed, list[0].packed)
        assertEquals(slot.currentDigit, list[0].currentDigit)
        assertEquals(slot.previousDigit, list[0].previousDigit)
    }

    @Test
    fun testPushAll_addsElementsFromLongArray() {
        val list = DigitSlotList()
        val values = longArrayOf(
            DigitSlot(1).packed,
            DigitSlot(2).packed,
            DigitSlot(3).packed,
        )

        list.pushAll(values)

        assertEquals(3, list.size)
        assertEquals(1, list[0].currentDigit)
        assertEquals(2, list[1].currentDigit)
        assertEquals(3, list[2].currentDigit)
    }

    @Test
    fun testGivenEmptyLongArray_pushAll_returnsFalse() {
        val list = DigitSlotList()
        assertFalse(list.pushAll(longArrayOf()))
    }

    @Test
    fun testPushAll_addsElementsFromDigitSlotList() {
        val source = DigitSlotList()
        source.push(DigitSlot(1))
        source.push(DigitSlot(2))

        val list = DigitSlotList()
        list.pushAll(source)

        assertEquals(2, list.size)
        assertEquals(1, list[0].currentDigit)
        assertEquals(2, list[1].currentDigit)
    }

    @Test
    fun testGivenEmptyDigitSlotList_pushAll_returnsFalse() {
        val list = DigitSlotList()
        assertFalse(list.pushAll(DigitSlotList()))
    }

    @Test
    fun testGet_returnsElementAtIndex() {
        val list = DigitSlotList()
        list.push(DigitSlot(1))
        list.push(DigitSlot(2))

        assertEquals(DigitSlot(1), list[0])
        assertEquals(DigitSlot(2), list[1])
    }

    @Test
    fun testIfInvalidIndex_get_throws() {
        val list = DigitSlotList()
        list.push(DigitSlot(1))

        assertFailsWith<IndexOutOfBoundsException> { list[-1] }
        assertFailsWith<IndexOutOfBoundsException> { list[1] }
    }

    @Test
    fun testSet_replacesElementAndReturnsPrevious() {
        val list = DigitSlotList()
        list.push(DigitSlot(1))

        val previous = list.set(0, DigitSlot(2))

        assertEquals(DigitSlot(1), previous)
        assertEquals(DigitSlot(2), list[0])
    }

    @Test
    fun testIfInvalidIndex_set_throws() {
        val list = DigitSlotList()
        list.push(DigitSlot(1))

        assertFailsWith<IndexOutOfBoundsException> {
            list.set(-1, DigitSlot(2))
        }

        assertFailsWith<IndexOutOfBoundsException> {
            list.set(1, DigitSlot(2))
        }
    }

    @Test
    fun testElementAt_returnsElementAtIndex() {
        val list = DigitSlotList()
        list.push(DigitSlot(1))
        list.push(DigitSlot(2))

        assertEquals(DigitSlot(1), list.elementAt(0))
        assertEquals(DigitSlot(2), list.elementAt(1))
    }

    @Test
    fun testIfInvalidIndex_elementAt_throws() {
        val list = DigitSlotList()
        list.push(DigitSlot(1))

        assertFailsWith<IndexOutOfBoundsException> {
            list.elementAt(-1)
        }

        assertFailsWith<IndexOutOfBoundsException> {
            list.elementAt(1)
        }
    }

    @Test
    fun testPopLast_returnsLastElement() {
        val list = DigitSlotList()
        list.push(DigitSlot(1))
        list.push(DigitSlot(2))

        assertEquals(DigitSlot(2), list.popLast())
    }

    @Test
    fun testPopLast_removesLastElement() {
        val list = DigitSlotList()
        list.push(DigitSlot(1))
        list.push(DigitSlot(2))

        list.popLast()

        assertEquals(1, list.size)
        assertEquals(DigitSlot(1), list[0])
    }

    @Test
    fun testIfListIsEmpty_popLast_throws() {
        val list = DigitSlotList()

        assertTrue(list.isEmpty())
        assertFailsWith<NoSuchElementException> { list.popLast() }
    }

    @Test
    fun testPopRangeFrom_removesSpecifiedRange() {
        val list = DigitSlotList()
        list.push(DigitSlot(0))
        list.push(DigitSlot(1))
        list.push(DigitSlot(2))
        list.push(DigitSlot(3))
        list.push(DigitSlot(4))

        list.popRangeFrom(1, 3)

        assertEquals(DigitSlot(0), list[0])
        assertEquals(DigitSlot(3), list[1])
        assertEquals(DigitSlot(4), list[2])
    }

    @Test
    fun testIfNoEndIndexIsSpecified_popRangeFrom_removesElementsToEnd() {
        val list = DigitSlotList()
        list.push(DigitSlot(0))
        list.push(DigitSlot(1))
        list.push(DigitSlot(2))
        list.push(DigitSlot(3))

        list.popRangeFrom(2)

        assertEquals(DigitSlot(0), list[0])
        assertEquals(DigitSlot(1), list[1])
    }

    @Test
    fun testIfRangeIsReversed_popRangeFrom_throws() {
        val list = DigitSlotList()
        list.push(DigitSlot(1))
        list.push(DigitSlot(2))

        assertFailsWith<IllegalArgumentException> {
            list.popRangeFrom(1, 0)
        }
    }

    @Test
    fun testIfInvalidStartIndex_popRangeFrom_throws() {
        val list = DigitSlotList()
        list.push(DigitSlot(1))
        list.push(DigitSlot(2))

        assertFailsWith<IndexOutOfBoundsException> {
            list.popRangeFrom(-1, 1)
        }
    }

    @Test
    fun testIfInvalidEndIndex_popRangeFrom_throws() {
        val list = DigitSlotList()
        list.push(DigitSlot(1))
        list.push(DigitSlot(2))

        assertFailsWith<IndexOutOfBoundsException> {
            list.popRangeFrom(0, 3)
        }
    }

    @Test
    fun testClear_removesAllElements() {
        val list = DigitSlotList()
        list.push(DigitSlot(1))
        list.push(DigitSlot(2))
        list.push(DigitSlot(3))

        list.clear()

        assertTrue(list.isEmpty())
        assertEquals(0, list.size)
    }

    @Test
    fun testGivenEmptyList_toString_returnsEmptyRepresentation() {
        val list = DigitSlotList()
        assertEquals("[]", list.toString())
    }

    @Test
    fun testToString_returnsIndexedDigitSlotRepresentation() {
        val list = DigitSlotList()
        list.push(DigitSlot(previousDigit = 3, currentDigit = 5))
        list.push(DigitSlot(currentDigit = 7))

        assertEquals(
            "[{0: [curr = 5, prev = 3]},{1: [curr = 7, prev = null]}]",
            list.toString(),
        )
    }

    @Test
    fun testPushAndPopLast_followLifoOrder() {
        val list = DigitSlotList()
        list.push(DigitSlot(1))
        list.push(DigitSlot(2))
        list.push(DigitSlot(3))

        assertEquals(3, list.popLast().currentDigit)
        assertEquals(2, list.popLast().currentDigit)
        assertEquals(1, list.popLast().currentDigit)
        assertTrue(list.isEmpty())
    }
}
