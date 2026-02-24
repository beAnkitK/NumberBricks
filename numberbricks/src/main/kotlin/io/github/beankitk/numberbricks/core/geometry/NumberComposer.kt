package io.github.beankitk.numberbricks.core.geometry

import androidx.collection.IntList
import androidx.collection.IntObjectMap
import androidx.collection.IntSet
import androidx.collection.MutableIntObjectMap
import androidx.collection.buildIntList
import androidx.collection.emptyIntList
import androidx.collection.mutableIntObjectMapOf
import androidx.collection.mutableIntSetOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.beankitk.numberbricks.core.internal.DigitSlotList
import kotlin.math.absoluteValue

interface NumberComposer<T : Brick<T>> {

    val currentNumber: Int

    val previousNumber: Int?

    val properties: GeometryProps

    val layoutConfig: GridConfig

    val digitBuilder: DigitBuilder<T>

    val digitSequence: IntArray

    fun initiate()

    fun updateNumber(number: Int)

    fun getDigitCount(): Int

    fun getDigitSlotAt(index: Int): DigitSlot?

    fun getBricksFor(digit: Int): List<T>?

    fun getDefaultBricks(): List<T>

    fun dispose()
}

class DefaultNumberComposer<T : Brick<T>>(
    initialNumber: Int,
    override val properties: GeometryProps,
    override val digitBuilder: DigitBuilder<T>
) : NumberComposer<T> {

    private var isInitialized = false

    // Digit slot structure tracking per-position transitions
    private val digitSlotList: DigitSlotList = DigitSlotList()
    private var _digitSequence: IntList = emptyIntList()
    private var digitSlotCount by mutableIntStateOf(0)

    // Brick cache: digit → brick list
    private val digitBricksCache: MutableIntObjectMap<List<T>> = mutableIntObjectMapOf()
    private var defaultBricks: List<T>? = null

    //TODO: setting current number to initial number without constructing the builder leads to digit mismatch
    private val _currentNumber = mutableIntStateOf(initialNumber.absoluteValue)
    override val currentNumber: Int
        get() {
            check(isInitialized) { "currentNumber cannot be accessed before NumberComposer is initiated." }
            return _currentNumber.value
        }

    private val _previousNumber = mutableStateOf<Int?>(null)
    override val previousNumber: Int? by _previousNumber

    override val layoutConfig: GridConfig
        get() = properties.config

    override val digitSequence: IntArray
        get() = _digitSequence.toIntArray()

    override fun initiate() {
        require(!isInitialized) { "NumberComposer already initialized" }
        digitBuilder.construct(properties)
        defaultBricks = digitBuilder.defaultBricks()

        applyNumberChange(_currentNumber.value, isFirstUpdate = true)
        isInitialized = true
    }

    override fun updateNumber(number: Int) {
        checkInitialized()
        val absNumber = number.absoluteValue
        if (absNumber == _currentNumber.value) return

        applyNumberChange(absNumber)
    }

    // Applies a number change by parsing digits, updating slots, and caching bricks.
    private fun applyNumberChange(
        newNumber: Int,
        isFirstUpdate: Boolean = false
    ) {
        val newDigitSequence = parseDigitsReversed(newNumber)
        val uniqueDigits = newDigitSequence.asIntSet()

        // Ensure bricks for all digits are cached
        uniqueDigits.forEach { digit ->
            if (!digitBricksCache.containsKey(digit)) {
                val bricks = digitBuilder.getBricksFor(digit)
                digitBricksCache[digit] = bricks
            }
        }

        updateDigitSlotList(newDigitSequence)
        _digitSequence = newDigitSequence

        // Update state tracking
        if (!isFirstUpdate) {
            _previousNumber.value = _currentNumber.value
            _currentNumber.value = newNumber
        }
        digitSlotCount = newDigitSequence.size
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

    private fun updateDigitSlotList(digitList: IntList) {
        val currDigitCount = digitSlotCount
        val newDigitCount = digitList.size

        // Update common positions
        val commonSize = minOf(currDigitCount, newDigitCount)
        for (place in 0 until commonSize) {
            val currDigitSlot = digitSlotList[place]
            val newDigit = digitList[place]

            if (currDigitSlot.currentDigit != newDigit) {
                digitSlotList[place] = currDigitSlot.withCurrent(newDigit)
            }
        }

        when {
            // Add new digit positions
            newDigitCount > currDigitCount -> {
                val newDigitsList = LongArray(newDigitCount - currDigitCount) { index ->
                    val digitIndex = index + currDigitCount
                    DigitSlot(digitList[digitIndex]).packed
                }
                digitSlotList.pushAll(newDigitsList)
            }

            // Remove excess digit positions
            newDigitCount < currDigitCount -> {
                digitSlotList.popRangeFrom(newDigitCount, currDigitCount)
            }
        }
    }

    override fun getDigitCount(): Int {
        checkInitialized()
        return digitSlotCount
    }

    override fun getDigitSlotAt(index: Int): DigitSlot? {
        checkInitialized()
        return digitSlotList[index]
    }

    override fun getBricksFor(digit: Int): List<T>? {
        checkInitialized()
        require(digit in 0..9) { "Digit must be in range 0-9" }
        return digitBricksCache[digit]
    }

    override fun getDefaultBricks(): List<T> {
        checkInitialized()
        return defaultBricks ?: digitBuilder.defaultBricks().also { defaultBricks = it }
    }

    override fun dispose() {
        _digitSequence = emptyIntList()
        digitSlotList.clear()
        digitBricksCache.clear()
        defaultBricks = null
        digitSlotCount = 0
        _previousNumber.value = null
        digitBuilder.destruct()
        isInitialized = false
    }

    private fun checkInitialized() {
        require(isInitialized) { "Composer not initialized. Call initiate() first" }
    }
}

private fun IntList.asIntSet(): IntSet {
    val set = mutableIntSetOf()
    forEach { set.add(it) }
    return set
}

private fun IntList.toIntArray(): IntArray = IntArray(size) { get(it) }