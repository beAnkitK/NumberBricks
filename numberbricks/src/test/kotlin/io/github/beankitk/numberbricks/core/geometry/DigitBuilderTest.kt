package io.github.beankitk.numberbricks.core.geometry

import io.github.beankitk.numberbricks.testing.AdaptiveTestProvider
import io.github.beankitk.numberbricks.testing.DEFAULT_OFFSET
import io.github.beankitk.numberbricks.testing.DEFAULT_POSITION
import io.github.beankitk.numberbricks.testing.DEFAULT_SIZE
import io.github.beankitk.numberbricks.testing.TEST_ERROR
import io.github.beankitk.numberbricks.testing.TestDigitBuilder
import io.github.beankitk.numberbricks.testing.UniformOffset
import io.github.beankitk.numberbricks.testing.UniformPosition
import io.github.beankitk.numberbricks.testing.UniformSize
import io.github.beankitk.numberbricks.testing.createGridSpec
import io.github.beankitk.numberbricks.testing.createKey
import io.github.beankitk.numberbricks.testing.createProps
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BaseDigitBuilderTest {

    private val mockKey = createKey<Int>()
    private val gridSpec = createGridSpec(5, 3, 13)
    private val props = createProps()

    @Test
    fun testWhenNotConstructed_builderIsUnconstructed_andCannotBeUsed() {
        val digitBuilder = TestDigitBuilder()

        assertFalse(digitBuilder.isConstructed)
        assertFailsWith<IllegalStateException> { digitBuilder.buildBricks(0) }
        assertFailsWith<IllegalStateException> { digitBuilder.buildDefaultBricks() }
    }

    @Test
    fun testWhenAlreadyConstructed_construct_throws() {
        val digitBuilder = TestDigitBuilder()
        digitBuilder.construct(gridSpec, props)

        assertTrue(digitBuilder.isConstructed)
        assertFailsWith<IllegalStateException> { digitBuilder.construct(gridSpec, props) }
    }

    @Test
    fun testIfDuplicateProviderKeysFound_construct_throwsAndBuilderIsUnconstructed() {
        val provider =
            AdaptiveTestProvider(key = mockKey, provideData = { gs -> List(gs.brickCount) { it } })
        val digitBuilder = TestDigitBuilder(listOf(provider, provider))

        assertFailsWith<IllegalStateException> { digitBuilder.construct(gridSpec, props) }
        assertFalse(digitBuilder.isConstructed)
    }

    @Test
    fun testWhenProviderMatchIsRejected_construct_throwsAndBuilderIsUnconstructed() {
        val provider =
            AdaptiveTestProvider(
                key = mockKey,
                doMatch = { Consent.Reject("Incompatible") },
                provideData = { gs -> List(gs.brickCount) { it } },
            )
        val digitBuilder = TestDigitBuilder(listOf(provider))

        assertFailsWith<IllegalStateException> { digitBuilder.construct(gridSpec, props) }
        assertFalse(digitBuilder.isConstructed)
    }

    @Test
    fun testIfAnyProviderThrowsInOnAttach_allProvidersAreDetached() {
        val providerA =
            AdaptiveTestProvider(
                key = createKey<Int>(),
                provideData = { gs -> List(gs.brickCount) { it } },
            )
        val providerB =
            AdaptiveTestProvider(
                key = createKey<Int>(),
                onAttach = { _, _ -> TEST_ERROR },
                provideData = { gs -> List(gs.brickCount) { it } },
            )
        val providerC =
            AdaptiveTestProvider(
                key = createKey<Int>(),
                provideData = { gs -> List(gs.brickCount) { it } },
            )
        val digitBuilder = TestDigitBuilder(listOf(providerA, providerB, providerC))

        assertFailsWith<IllegalStateException> { digitBuilder.construct(gridSpec, props) }
        assertFalse(providerA.isAttached)
        assertFalse(providerB.isAttached)
        assertFalse(providerC.isAttached)
    }

    @Test
    fun testIfOnConstructedThrows_builderResetsToUnconstructed() {
        val digitBuilder = TestDigitBuilder()

        digitBuilder.onConstructed = { TEST_ERROR }
        assertFailsWith<IllegalStateException> { digitBuilder.construct(gridSpec, props) }
        assertFalse(digitBuilder.isConstructed)
    }

    @Test
    fun testIfOnConstructedThrows_allProvidersAreDetached() {
        val providerA =
            AdaptiveTestProvider(
                key = createKey<Int>(),
                provideData = { gs -> List(gs.brickCount) { it } },
            )
        val providerB =
            AdaptiveTestProvider(
                key = createKey<Int>(),
                provideData = { gs -> List(gs.brickCount) { it } },
            )
        val digitBuilder = TestDigitBuilder(listOf(providerA, providerB))

        digitBuilder.onConstructed = { TEST_ERROR }
        assertFailsWith<IllegalStateException> { digitBuilder.construct(gridSpec, props) }
        assertFalse(providerA.isAttached)
        assertFalse(providerB.isAttached)
    }

    @Test
    fun testWhenConstructed_allProvidersAreAttached() {
        val positionProvider = UniformPosition(DEFAULT_POSITION)
        val offsetProvider = UniformOffset(DEFAULT_OFFSET)
        val sizeProvider = UniformSize(DEFAULT_SIZE)

        val digitBuilder = TestDigitBuilder(listOf(positionProvider, offsetProvider, sizeProvider))
        digitBuilder.construct(gridSpec, props)

        assertTrue(positionProvider.isAttached)
        assertTrue(offsetProvider.isAttached)
        assertTrue(sizeProvider.isAttached)
    }

    @Test
    fun testWhenConstructed_providesGridSpecToAllProviders() {
        val positionProvider = UniformPosition(DEFAULT_POSITION)
        val offsetProvider = UniformOffset(DEFAULT_OFFSET)
        val sizeProvider = UniformSize(DEFAULT_SIZE)

        val digitBuilder = TestDigitBuilder(listOf(positionProvider, offsetProvider, sizeProvider))
        digitBuilder.construct(gridSpec, props)

        assertEquals(gridSpec, positionProvider.providerGridSpec)
        assertEquals(gridSpec, offsetProvider.providerGridSpec)
        assertEquals(gridSpec, sizeProvider.providerGridSpec)
    }

    @Test
    fun testIfCircularProviderDependencyFound_construct_throwsAndBuilderIsUnconstructed() {
        val keyA = createKey<Int>()
        val keyB = createKey<Int>()
        val providerA =
            AdaptiveTestProvider(
                key = keyA,
                dependsOn = setOf(keyB),
                provideData = { gs -> List(gs.brickCount) { it } },
            )
        val providerB =
            AdaptiveTestProvider(
                key = keyB,
                dependsOn = setOf(keyA),
                provideData = { gs -> List(gs.brickCount) { it } },
            )
        val digitBuilder = TestDigitBuilder(listOf(providerA, providerB))

        assertFailsWith<IllegalStateException> { digitBuilder.construct(gridSpec, props) }
        assertFalse(digitBuilder.isConstructed)
    }

    @Test
    fun testGivenValidDigit_buildBricks_returnsExpectedBrickCount() {
        val digitBuilder = TestDigitBuilder()
        digitBuilder.construct(gridSpec, props)

        listOf(0, 5, 9, -1).forEach { digit ->
            assertEquals(
                gridSpec.brickCount,
                digitBuilder.buildBricks(digit).size,
                "Unexpected brick count for digit $digit",
            )
        }
    }

    @Test
    fun testIfInvalidDigitIsGiven_buildBricks_throws() {
        val digitBuilder = TestDigitBuilder()
        digitBuilder.construct(gridSpec, props)

        listOf(-2, -100, 10, 100).forEach { digit ->
            assertFailsWith<IllegalArgumentException> { digitBuilder.buildBricks(digit) }
        }
    }

    @Test
    fun testIfProviderResultSizeMismatchesGridSpec_buildBricks_throws() {
        val wrongSizeProvider =
            AdaptiveTestProvider(key = mockKey, provideData = { _ -> emptyList<Int>() })
        val digitBuilder = TestDigitBuilder(listOf(wrongSizeProvider))
        digitBuilder.construct(gridSpec, props)
        assertFailsWith<IllegalStateException> { digitBuilder.buildBricks(0) }
    }

    @Test
    fun testBuildDefaultBricks_returnsDefaultBrickModel() {
        val digitBuilder = TestDigitBuilder().also { it.construct(gridSpec, props) }
        val bricks = digitBuilder.buildDefaultBricks()

        assertEquals(gridSpec.brickCount, bricks.size)
        bricks.forEach { brick ->
            assertEquals(DEFAULT_POSITION, brick.position)
            assertEquals(DEFAULT_OFFSET, brick.offset)
            assertEquals(DEFAULT_SIZE, brick.size)
        }
    }

    @Test
    fun testWhenNotConstructed_destroy_doesNothing() {
        val builder = TestDigitBuilder()
        builder.destroy()
        assertFalse(builder.isConstructed)
    }

    @Test
    fun testIfOnDestroyingThrows_builderIsUnconstructed() {
        val digitBuilder = TestDigitBuilder()

        digitBuilder.construct(gridSpec, props)
        assertTrue(digitBuilder.isConstructed)

        digitBuilder.onDestroying = { TEST_ERROR }
        assertFailsWith<IllegalStateException> { digitBuilder.destroy() }
        assertFalse(digitBuilder.isConstructed)
    }

    @Test
    fun testWhileOnDestroying_builderIsConstructed() {
        val digitBuilder = TestDigitBuilder()

        digitBuilder.construct(gridSpec, props)
        assertTrue(digitBuilder.isConstructed)

        digitBuilder.onDestroying = { assertTrue(digitBuilder.isConstructed) }
        digitBuilder.destroy()
    }

    @Test
    fun testIfAnyProviderThrowsInOnDetach_builderIsUnconstructed_andAllProvidersAreDetached() {
        val providerA =
            AdaptiveTestProvider(
                key = createKey<Int>(),
                onDetach = { TEST_ERROR },
                provideData = { gs -> List(gs.brickCount) { it } },
            )
        val providerB =
            AdaptiveTestProvider(
                key = createKey<Int>(),
                onDetach = { TEST_ERROR },
                provideData = { gs -> List(gs.brickCount) { it } },
            )
        val providerC =
            AdaptiveTestProvider(
                key = createKey<Int>(),
                provideData = { gs -> List(gs.brickCount) { it } },
            )
        val digitBuilder = TestDigitBuilder(listOf(providerA, providerB, providerC))
        digitBuilder.construct(gridSpec, props)

        assertFailsWith<IllegalStateException> { digitBuilder.destroy() }
        assertFalse(digitBuilder.isConstructed)
        assertFalse(providerA.isAttached)
        assertFalse(providerB.isAttached)
        assertFalse(providerC.isAttached)
    }

    @Test
    fun testDestroy_resetsBuilderToUnconstructed_andCannotBeUsed() {
        val digitBuilder = TestDigitBuilder()
        digitBuilder.construct(gridSpec, props)
        digitBuilder.destroy()

        assertFalse(digitBuilder.isConstructed)
        assertFailsWith<IllegalStateException> { digitBuilder.buildBricks(0) }
        assertFailsWith<IllegalStateException> { digitBuilder.buildDefaultBricks() }
    }

    @Test
    fun testAfterDestroy_builderCanBeConstructedAgain_andUsed() {
        val digitBuilder = TestDigitBuilder()
        digitBuilder.construct(gridSpec, props)
        digitBuilder.destroy()
        assertFalse(digitBuilder.isConstructed)

        digitBuilder.construct(gridSpec, props)
        assertTrue(digitBuilder.isConstructed)
        assertEquals(gridSpec.brickCount, digitBuilder.buildBricks(0).size)
    }
}
