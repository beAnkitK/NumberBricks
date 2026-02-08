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

/**
 * Central stateful coordinator for NumberBricks composable that manages number state and digit geometry.
 *
 * The [NumberComposer] is the stateful coordinator of the number state and digit geometry system, responsible
 * for tracking number transitions, managing digit slots, and caching brick data. It sits directly below
 * the composable layer and coordinates with the digit builder to provide efficient brick access.
 *
 * @param T The concrete brick type produced by the digit builder
 */
interface NumberComposer<T : Brick<T>> {

    /**
     * The current number being managed by the composer. It must be a absolute value.
     */
    val currentNumber: Int

    /**
     * The number managed by the composer before the last update, or null for the initial state.
     */
    val previousNumber: Int?

    /**
     * The geometry properties this composer is bound to. This holds the layout configuration
     * as well other properties that defines the geometric appearance of digits and the overall number.
     *
     * @see GeometryProps
     */
    val properties: GeometryProps

    /**
     * The grid configuration from the geometry properties describing the layout attributes such
     * as  row, column and brick count for each digit of number. This should not mutate after
     * the composer is initiated. This is required for providing correct configuration to the
     * digit builder and providers.
     */
    val layoutConfig: GridConfig

    /**
     * The digit builder used to construct and assemble the bricks defining the visual appearance
     * of digit in the form of `List<Bricks>`.
     */
    val digitBuilder: DigitBuilder<T>

    /**
     * The individual digits of the current number in reverse order (least to most significant).
     *
     * Example: 456 becomes [6, 5, 4] where index 0 is the ones place.
     */
    val digitSequence: IntArray

    /**
     * Initializes the composer and prepares for number display.
     *
     * Constructs the digit builder, generates default bricks, and processes the initial number.
     * Must be called before any other operations.
     *
     * @throws IllegalStateException if already initialized
     */
    fun initiate()

    /**
     * Updates the currently managed number by this composer.
     *
     * Parses the new number into digits, updates transition slots, and ensures bricks
     * for all required digits are cached. No-op if the number hasn't changed.
     *
     * @param number The new number to display (converted to absolute value)
     */
    fun updateNumber(number: Int)

    /**
     * Returns the count of digits in the current number.
     */
    fun getDigitCount(): Int

    /**
     * Retrieves the digit slot at a specific position.
     *
     * @param index The digit position (0 = ones, 1 = tens, etc.)
     * @return The slot tracking current and previous digit at this position, or null if out of bounds
     */
    fun getDigitSlotAt(index: Int): DigitSlot?

    /**
     * Retrieves cached bricks for a specific digit.
     *
     * @param digit The digit value (0-9)
     * @return The cached brick list, or null if not yet generated
     */
    fun getBricksFor(digit: Int): List<T>?

    /**
     * Gets the default list of brick used to show initial or intermediary state.
     *
     * @return The default bricks used as placeholder or fallback state
     */
    fun getDefaultBricks(): List<T>

    /**
     * Cleans up all resources and resets to uninitialized state.
     *
     * Clears caches, destroys the digit builder, and releases all managed resources.
     * After disposal, [initiate] must be called again before use.
     */
    fun dispose()
}

/**
 * Default implementation of [NumberComposer].
 *
 * This is the primary stateful, long-lived object in the geometry system. It manages:
 * - Current and previous number state (as Compose mutable state)
 * - Digit slot structure for tracking transitions
 * - Per-digit brick cache for efficient lookup
 * - Digit builder lifecycle and resource cleanup
 *
 * **Lifecycle:**
 * 1. Creation: Construct with initial number, properties, and builder
 * 2. Initialization: Call [initiate] to prepare for display
 * 3. Updates: Call [updateNumber] as needed
 * 4. Disposal: Call [dispose] to clean up resources
 *
 * **State Management:**
 * - [currentNumber] and [previousNumber] are backed by Compose state for reactive updates
 * - Digit slots track per-position transitions (previous → current digit)
 * - Brick cache stores generated bricks per digit (0-9) for reuse
 *
 * @param initialNumber The starting number to display
 * @param properties The geometry configuration this composer is bound to
 * @param digitBuilder The builder used to construct digit bricks
 */
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
    override val currentNumber: Int by _currentNumber

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
        isInitialized = true

        applyNumberChange(_currentNumber.value, isFirstUpdate = true)
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
        } else {
            _previousNumber.value = null
        }
        _currentNumber.value = newNumber
        digitSlotCount = newDigitSequence.size
    }

    /**
     * Parses a number into individual digits in reverse order.
     *
     * Returns digits from least to most significant (ones place first).
     * Example: 456 → [6, 5, 4]
     */
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

    /**
     * Updates the digit slot list to reflect the new digit sequence.
     *
     * Handles three cases:
     * 1. Common positions: Update existing slots with new current digit (preserving previous)
     * 2. New positions: Add new slots with no previous digit
     * 3. Removed positions: Remove slots from the end
     */
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