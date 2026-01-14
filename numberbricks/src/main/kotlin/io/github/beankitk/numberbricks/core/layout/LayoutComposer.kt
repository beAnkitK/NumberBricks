package io.github.beankitk.numberbricks.core.layout

import androidx.collection.IntList
import androidx.collection.IntObjectMap
import androidx.collection.IntSet
import androidx.collection.MutableIntList
import androidx.collection.MutableIntObjectMap
import androidx.collection.MutableIntSet
import androidx.collection.buildIntList
import androidx.collection.emptyIntList
import androidx.collection.mutableIntListOf
import androidx.collection.mutableIntObjectMapOf
import androidx.collection.mutableIntSetOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.packInts
import io.github.beankitk.numberbricks.core.layout.internal.DigitEntryList
import kotlin.math.absoluteValue

interface LayoutComposer<T : BrickItem<T>> {

    val currentNumber: Int

    val previousNumber: Int?

    val properties: LayoutProperties

    val layoutConfig: LayoutConfig

    val layoutBuilder: LayoutBuilder<T>

    val digitSequence: IntArray

    fun initiate()

    fun updateNumber(number: Int)

    fun getDigitCount(): Int

    fun getDigitEntryAt(index: Int): DigitEntry?

    fun getBrickItems(digit: Int): List<T>?

    fun getDefaultBrickItems(): List<T>

    fun dispose()
}

class DefaultLayoutComposer<T : BrickItem<T>>(
    initialNumber: Int,
    override val properties: LayoutProperties,
    override val layoutBuilder: LayoutBuilder<T>
) : LayoutComposer<T> {

    private var isInitialized = false

    private val digitEntryList: DigitEntryList = DigitEntryList()
    private var _digitSequence: IntList = emptyIntList()
    private var digitEntryCount by mutableIntStateOf(0)

    private val digitBricksCache: MutableIntObjectMap<List<T>> = mutableIntObjectMapOf()
    private var defaultBrickItems: List<T>? = null

    //TODO: setting current number to initial number without constructing the builder leads to digit mismatch
    private val _currentNumber = mutableIntStateOf(initialNumber.absoluteValue)
    override val currentNumber: Int by _currentNumber

    private val _previousNumber = mutableStateOf<Int?>(null)
    override val previousNumber: Int? by _previousNumber

    override val layoutConfig: LayoutConfig
        get() = properties.config

    override val digitSequence: IntArray
        get() = _digitSequence.toIntArray()

    override fun initiate() {
        require(!isInitialized) { "Layout Composer already initialized. Cannot be initiated" }
        layoutBuilder.construct(properties)
        defaultBrickItems = layoutBuilder.defaultBrickItems()
        isInitialized = true

        applyNumberChange(_currentNumber.value, isFirstUpdate = true)
    }

    override fun updateNumber(number: Int) {
        checkInitialized()
        val absNumber = number.absoluteValue
        if (absNumber == _currentNumber.value) return

        applyNumberChange(absNumber)
    }

    private fun applyNumberChange(
        newNumber: Int,
        isFirstUpdate: Boolean = false
    ) {
        val newDigitSequence = parseDigitsReversed(newNumber)
        val uniqueDigits = newDigitSequence.asIntSet()

        uniqueDigits.forEach { digit ->
            if (!digitBricksCache.containsKey(digit)) {
                val brickItems = layoutBuilder.getBrickItemsFor(digit)
                digitBricksCache[digit] = brickItems
            }
        }

        updateDigitEntryList(newDigitSequence)
        _digitSequence = newDigitSequence
        if (!isFirstUpdate) {
            _previousNumber.value = _currentNumber.value
        } else {
            _previousNumber.value = null
        }
        _previousNumber.value = _currentNumber.value
        _currentNumber.value = newNumber
        digitEntryCount = newDigitSequence.size
    }

    private fun parseDigitsReversed(number: Int): IntList {
        return buildIntList {
            if (number == 0) {
                add(0)
                return@buildIntList
            }

            var n = number
            while (n > 0) {
                add(n % 10)
                n /= 10
            }
        }
    }

    private fun updateDigitEntryList(digitList: IntList) {
        val currDigitCount = digitEntryCount
        val newDigitCount = digitList.size

        val commonSize = minOf(currDigitCount, newDigitCount)
        for (place in 0 until commonSize) {
            val currDigitEntry = digitEntryList[place]
            val newDigit = digitList[place]

            if (currDigitEntry.currentDigit != newDigit) {
                digitEntryList[place] = currDigitEntry.withCurrent(newDigit)
            }
        }

        when {
            newDigitCount > currDigitCount -> {
                val newDigitsList = LongArray(newDigitCount - currDigitCount) { index ->
                    val digitIndex = index + currDigitCount
                    DigitEntry(digitList[digitIndex]).packed
                }
                digitEntryList.pushAll(newDigitsList)
            }

            newDigitCount < currDigitCount -> {
                digitEntryList.popRangeFrom(newDigitCount, currDigitCount)
            }
        }
    }

    override fun getDigitCount(): Int {
        checkInitialized()
        return digitEntryCount
    }

    override fun getDigitEntryAt(index: Int): DigitEntry? {
        checkInitialized()
	    return digitEntryList[index]
    }

    override fun getBrickItems(digit: Int): List<T>? {
        checkInitialized()
        require(digit in 0..9) { "Digit must be in range 0-9" }
        return digitBricksCache[digit]
    }

    override fun getDefaultBrickItems(): List<T> {
        checkInitialized()
        return defaultBrickItems ?: layoutBuilder.defaultBrickItems().also { defaultBrickItems = it }
    }

    override fun dispose() {
        _digitSequence = emptyIntList()
        digitEntryList.clear()
        digitBricksCache.clear()
        defaultBrickItems = null
        digitEntryCount = 0
        _previousNumber.value = null
        layoutBuilder.destruct()
        isInitialized = false
    }

    private fun checkInitialized() {
        require(isInitialized) { "Composer not initialized. Call initiate() first" }
    }
}

@JvmInline
value class DigitEntry(val packed: Long) {

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

    fun withCurrent(newDigit: Int): DigitEntry =
        DigitEntry(currentDigit, newDigit)

    fun isSame(): Boolean = previousDigit == currentDigit

    override fun toString() = "[curr = $currentDigit, prev = $previousDigit]"
}

private fun IntList.asIntSet(): IntSet {
    val set = mutableIntSetOf()
    forEach { set.add(it) }
    return set
}

private fun IntList.toIntArray(): IntArray = IntArray(size) { get(it) }