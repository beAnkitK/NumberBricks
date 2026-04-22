package io.github.beankitk.numberbricks.core.geometry

import androidx.collection.IntList
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
 * Central stateful coordinator for NumberBricks composable that manages number state and digit
 * geometry.
 *
 * A [NumberComposer] manages the transformation of a number into per-digit geometry. It parses the
 * number into digits, maintains per-position digit state, and provides access to brick model for
 * each digit generated via [DigitBuilder].
 *
 * The composer operates on fixed [digitGridSpec] and [geometryProps], which define the grid
 * constraints and shared geometry configuration used to construct digits.
 *
 * It maintains both [currentNumber] and [previousNumber] to support transition-aware rendering.
 * Digit-level transitions are tracked using [DigitSlot].
 *
 * Before use, [initiate] must be called to construct the builder and prepare internal state. After
 * initialization:
 * - [updateNumber] updates the number and recomputes affected digits
 * - [getDigitSlotAt] provides per-position digit state
 * - [getBricks] returns cached brick model for given digit
 * - [getDefaultBricks] returns a default brick model
 *
 * The composer is stateful and must be released via [dispose] when no longer needed.
 *
 * **Note:** Currently, this composer operates only on non-negative integer values. Any negative
 * input is converted to its absolute value before processing.
 *
 * @param T The concrete [Brick] type used for digit geometry
 */
// TODO: Extend support for signed numbers and non-integer representations (e.g., floating-point).
interface NumberComposer<T : Brick<T>> {

    /**
     * Returns the current number managed by this composer.
     *
     * This value is initialized with the provided initial number (converted to its absolute value)
     * and becomes readable only after [initiate] completes.
     *
     * During updates, the new number is fully processed (digit parsing, slot updates, and geometry
     * composition) before this value is updated, ensuring consistent state for transition-aware
     * consumers.
     *
     * @throws IllegalStateException if accessed before [initiate] is called
     */
    val currentNumber: Int

    /**
     * Returns the previous number before the last update, or `null` if no prior value exists.
     *
     * This remains `null` after [initiate]. It is assigned when [updateNumber] is called with a
     * value different from the current number, capturing the value before the change.
     */
    val previousNumber: Int?

    /** Represents the grid constraints used to construct each digit. */
    val digitGridSpec: GridSpec

    /** Represents shared geometry configuration used across all digits. */
    val geometryProps: GeometryProps

    /** Defines the builder used to construct digit geometry. */
    val digitBuilder: DigitBuilder<T>

    /**
     * Initializes the composer and prepares it for use.
     *
     * This constructs the [digitBuilder], prepares default bricks, and processes the initial number
     * into digit slots while caching required brick models. This must be called before accessing
     * any other API.
     *
     * @throws IllegalStateException if already initialized
     */
    fun initiate()

    /**
     * Updates the current number managed by this composer.
     *
     * The input is converted to its absolute value. If the value has not changed, no work is
     * performed. Otherwise, digits are re-parsed, slots are updated, and required brick data is
     * generated and cached.
     *
     * The new number is first processed by parsing its digits and computing the corresponding brick
     * model before updating [currentNumber] and [previousNumber]. This ensures a consistent
     * transition state during updates.
     *
     * @param number The new number to be managed by this composer as [currentNumber]
     */
    fun updateNumber(number: Int)

    /** Returns the number of digits in the current number. */
    fun getDigitCount(): Int

    /**
     * Returns the [DigitSlot] at the given position.
     *
     * The index is zero-based and represents the digit place in reverse order (least significant
     * first): It can be interpreted as a power of ten:
     * - `0` -> 10^0 = 1 -> ones place
     * - `1` -> 10^1 = 10 -> tens place
     * - `2` -> 10^2 = 100 -> hundreds place
     * - `3` -> 10^3 = 1000 -> thousands place and so on
     *
     * Each [DigitSlot] encapsulates the state for that position, including the current digit and
     * its previous value, enabling transition-aware rendering.
     *
     * The number of valid indices is determined by [getDigitCount]. Accessing an index outside this
     * range returns `null`.
     *
     * @param index The position of the digit in reverse order
     * @return The corresponding [DigitSlot], or `null` if the index is out of bounds
     */
    fun getDigitSlotAt(index: Int): DigitSlot?

    /**
     * Returns the brick model representing the given digit.
     *
     * The composer caches brick model per digit on demand, the first time a digit appears in the
     * number during the current lifecycle.
     *
     * If this method is called after [updateNumber] and the digit is part of the current number, a
     * valid brick model is guaranteed to be returned.
     *
     * Returns `null` only if the digit has not yet been encountered in the current lifecycle of the
     * composer.
     *
     * @param digit The digit value (`0..9`)
     * @return The brick model representing the given digit
     */
    fun getBricks(digit: Int): List<T>?

    /**
     * Returns the default brick model.
     *
     * Used as placeholder representation when brick model for a specific digit is not available.
     * The default bricks are computed once on first access and cached for reuse. Subsequent calls
     * return the cached result to avoid redundant construction.
     *
     * @return The default brick model used as a placeholder representation
     */
    fun getDefaultBricks(): List<T>

    /**
     * Releases all resources and resets the composer to an uninitialized state.
     *
     * Clears caches, destroys the digit builder, and releases all managed resources. If the
     * composer is not initialized, this call is a no-op.
     *
     * After disposal, [initiate] must be called again before use, otherwise it would throw
     * [IllegalStateException].
     */
    fun dispose()
}

/**
 * Default implementation of [NumberComposer].
 *
 * Manages number state, digit transitions, geometry composition using [DigitBuilder] and brick
 * caching. It maintains a per-position [DigitSlot] structure to track transitions between digits
 * and caches brick model per digit for efficient reuse.
 *
 * Internally:
 * - Digits are stored in reverse order (least significant first)
 * - Brick model is cached per digit (`0..9`)
 * - Default bricks are lazily initialized and reused
 *
 * State is backed by Compose primitives to support reactive updates in UI layers.
 *
 * Lifecycle:
 * 1. Call [initiate] to construct the builder and prepare state
 * 2. Call [updateNumber] to update digits
 * 3. Query digit slots and bricks as needed
 * 4. Call [dispose] to release resources and destruct the builder
 *
 * @param initialNumber Defines the initial number to display
 * @property digitGridSpec Defines the grid constraints used for digit geometry composition
 * @property geometryProps Defines the shared geometry configuration
 * @property digitBuilder Defines the builder used to construct brick model for a digit
 */
class DefaultNumberComposer<T : Brick<T>>(
    initialNumber: Int,
    override val digitGridSpec: GridSpec,
    override val geometryProps: GeometryProps,
    override val digitBuilder: DigitBuilder<T>,
) : NumberComposer<T> {

    private var isInitialized = false

    // Digit slot structure tracking per-position transitions
    private val digitSlotList: DigitSlotList = DigitSlotList()
    private var digitSequence: IntList = emptyIntList()
    private var digitSlotCount by mutableIntStateOf(0)

    // Brick model cache: digit -> brick model
    private val digitBricksCache: MutableIntObjectMap<List<T>> = mutableIntObjectMapOf()
    private var defaultBricks: List<T>? = null

    // Stores the current number internally. It should not be accessed until the composer has been
    // initialized.
    private val _currentNumber = mutableIntStateOf(initialNumber.absoluteValue)
    override val currentNumber: Int
        get() {
            check(isInitialized) {
                "currentNumber cannot be accessed before NumberComposer is initiated."
            }
            return _currentNumber.value
        }

    private val _previousNumber = mutableStateOf<Int?>(null)
    override val previousNumber: Int? by _previousNumber

    override fun initiate() {
        check(!isInitialized) { "NumberComposer already initialized" }
        digitBuilder.construct(digitGridSpec, geometryProps)
        defaultBricks = digitBuilder.buildDefaultBricks()

        applyNumberChange(_currentNumber.value, isInitialUpdate = true)
        isInitialized = true
    }

    override fun updateNumber(number: Int) {
        checkInitialized()
        val absNumber = number.absoluteValue
        if (absNumber == _currentNumber.value) return

        applyNumberChange(absNumber)
    }

    // Applies a number change by parsing digits, updating slots, and caching bricks.
    private fun applyNumberChange(newNumber: Int, isInitialUpdate: Boolean = false) {
        val newDigitSequence = parseDigitsReversed(newNumber)
        val uniqueDigits = newDigitSequence.asIntSet()

        // Ensure bricks for all digits are cached
        uniqueDigits.forEach { digit ->
            if (!digitBricksCache.containsKey(digit)) {
                val bricks = digitBuilder.buildBricks(digit)
                digitBricksCache[digit] = bricks
            }
        }

        updateDigitSlotList(newDigitSequence)
        digitSequence = newDigitSequence

        // Update state tracking
        if (!isInitialUpdate) {
            _previousNumber.value = _currentNumber.value
            _currentNumber.value = newNumber
        }
        digitSlotCount = newDigitSequence.size
    }

    /**
     * Parses a number into individual digits in reverse order. Returns digits from least to most
     * significant (ones place first). Example: 456 -> [6, 5, 4]
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
                val newDigitsList =
                    LongArray(newDigitCount - currDigitCount) { index ->
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

    override fun getBricks(digit: Int): List<T>? {
        checkInitialized()
        require(digit in 0..9) { "Digit must be in range 0-9" }
        return digitBricksCache[digit]
    }

    override fun getDefaultBricks(): List<T> {
        checkInitialized()
        return defaultBricks ?: digitBuilder.buildDefaultBricks().also { defaultBricks = it }
    }

    override fun dispose() {
        if (!isInitialized) return

        digitSequence = emptyIntList()
        digitSlotList.clear()
        digitBricksCache.clear()
        defaultBricks = null
        digitSlotCount = 0
        _previousNumber.value = null
        digitBuilder.destruct()
        isInitialized = false
    }

    private fun checkInitialized() {
        check(isInitialized) { "Composer not initialized. Call initiate() first" }
    }
}

private fun IntList.asIntSet(): IntSet {
    val set = mutableIntSetOf()
    forEach { set.add(it) }
    return set
}
