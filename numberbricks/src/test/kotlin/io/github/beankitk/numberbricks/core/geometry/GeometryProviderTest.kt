package io.github.beankitk.numberbricks.core.geometry

import io.github.beankitk.numberbricks.testing.AdaptiveTestProvider
import io.github.beankitk.numberbricks.testing.ComputedTestProvider
import io.github.beankitk.numberbricks.testing.FixedTestProvider
import io.github.beankitk.numberbricks.testing.TestDigitBuilder
import io.github.beankitk.numberbricks.testing.TEST_ERROR
import io.github.beankitk.numberbricks.testing.createGridSpec
import io.github.beankitk.numberbricks.testing.createKey
import io.github.beankitk.numberbricks.testing.createProps
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class GeometryProviderTest {

    private val mockKey = createKey<Int>()
    private val mockGridSpec = createGridSpec(5, 3, 13)
    private val props = createProps()

    // Test helpers

    private fun createComputedProvider() =
        ComputedTestProvider(
            key = mockKey,
            provide =  { buildProviderData { it } },
        )

    private fun createAdaptiveProvider(
        doMatch: ((GridSpec) -> Consent)? = null,
        onAttach: ((GridSpec, GeometryProps) -> Unit)? = null,
        onDetach: (() -> Unit)? = null,
    ) =
        AdaptiveTestProvider(
            key = mockKey,
            doMatch = doMatch,
            onAttach = onAttach,
            onDetach = onDetach,
            provide = { buildProviderData { it } },
        )

    private fun createFixedProvider(
        gridSpec: GridSpec = mockGridSpec,
        doMatch: ((GridSpec) -> Consent)? = null,
        onAttach: ((GridSpec, GeometryProps) -> Unit)? = null,
        onDetach: (() -> Unit)? = null,
    ) =
        FixedTestProvider(
            key = mockKey,
            gridSpec = gridSpec,
            doMatch = doMatch,
            onAttach = onAttach,
            onDetach = onDetach,
            provide = { buildProviderData { it } },
        )

    // endregion

    // region Computed provider behavior

    @Test
    fun testComputedProvider_ifGivenToDifferentBuilder_provide_returnsResultMatchingBuilderGridSpec() {
        val provider = createComputedProvider()

        val gridSpec1 = createGridSpec(6, 4, 20)
        val digitBuilder1 = TestDigitBuilder(listOf(provider))
        digitBuilder1.construct(gridSpec1, props)
        assertEquals(gridSpec1.brickCount, digitBuilder1.buildBricks(0).size)

        val gridSpec2 = createGridSpec(7, 9, 56)
        val digitBuilder2 = TestDigitBuilder(listOf(provider))
        digitBuilder2.construct(gridSpec2, props)
        assertEquals(gridSpec2.brickCount, digitBuilder2.buildBricks(0).size)
    }

    // endregion

    // region Lifecycle provider common behavior

    @Test
    fun testWhenAlreadyAttached_providerCannotBeMatched() {
        val provider = createAdaptiveProvider()
        provider.matches(mockGridSpec)
        provider.attach(mockGridSpec, props)

        assertTrue(provider.isAttached)
        assertFailsWith<IllegalStateException> { provider.matches(mockGridSpec) }
    }

    @Test
    fun testWhenNotMatched_providerCannotAttach() {
        val provider = createAdaptiveProvider()

        assertFalse(provider.isAttached)
        assertFailsWith<IllegalStateException> { provider.attach(mockGridSpec, props) }
    }

    @Test
    fun testWhenMatchWasRejected_providerCannotAttach() {
        val provider = createAdaptiveProvider(doMatch = { Consent.Reject("Rejected For Test") })
        provider.matches(mockGridSpec)

        assertFalse(provider.isAttached)
        assertFailsWith<IllegalStateException> { provider.attach(mockGridSpec, props) }
    }

    @Test
    fun testIfDoMatchThrows_providerCannotAttach() {
        val provider = createAdaptiveProvider(doMatch = { TEST_ERROR })
        assertFailsWith<IllegalStateException> { provider.matches(mockGridSpec) }
        assertFalse(provider.isAttached)
        assertFailsWith<IllegalStateException> { provider.attach(mockGridSpec, props) }
    }

    @Test
    fun testWhenAlreadyAttached_providerCannotReattach() {
        val provider = createAdaptiveProvider()
        provider.matches(mockGridSpec)
        provider.attach(mockGridSpec, props)

        assertTrue(provider.isAttached)
        assertFailsWith<IllegalStateException> { provider.attach(mockGridSpec, props) }
    }

    @Test
    fun testIfOnAttachThrows_providerIsNotAttached() {
        val provider = createAdaptiveProvider(onAttach = { _, _ -> TEST_ERROR })
        provider.matches(mockGridSpec)
        assertFailsWith<IllegalStateException> { provider.attach(mockGridSpec, props) }
        assertFalse(provider.isAttached)
    }

    @Test
    fun testIfAttachFails_matchIsRequiredBeforeReattach() {
        var attachAction: ((GridSpec, GeometryProps) -> Unit)? = null
        val provider =
            createAdaptiveProvider(
                onAttach = { gridSpec, geometryProps ->
                    attachAction?.invoke(gridSpec, geometryProps)
                }
            )

        attachAction = { _, _ -> TEST_ERROR }
        provider.matches(mockGridSpec)
        assertFailsWith<IllegalStateException> { provider.attach(mockGridSpec, props) }
        assertFalse(provider.isAttached)

        attachAction = null
        assertFailsWith<IllegalStateException> { provider.attach(mockGridSpec, props) }

        provider.matches(mockGridSpec)
        provider.attach(mockGridSpec, props)
        assertTrue(provider.isAttached)
    }

    @Test
    fun testWhenNotAttached_detach_doesNothing() {
        val provider = createAdaptiveProvider()
        provider.detach()
        assertFalse(provider.isAttached)
    }

    @Test
    fun testWhenDetached_providerIsNotAttached() {
        val provider = createAdaptiveProvider()
        provider.matches(mockGridSpec)
        provider.attach(mockGridSpec, props)
        assertTrue(provider.isAttached)

        provider.detach()
        assertFalse(provider.isAttached)
    }

    @Test
    fun testWhenDetached_providerCanBeReattached() {
        val attachedGridSpec = mockGridSpec
        val provider = createAdaptiveProvider()
        val providerScope = DefaultProviderScope(digit = 0, mockGridSpec)

        providerScope.use { scope ->
            provider.matches(attachedGridSpec)
            provider.attach(attachedGridSpec, props)

            val firstResult = with(provider) { scope.provide() }
            provider.detach()

            assertFalse(provider.isAttached)

            provider.matches(attachedGridSpec)
            provider.attach(attachedGridSpec, props)

            assertTrue(provider.isAttached)
            val secondResult = with(provider) { scope.provide() }
            assertEquals(firstResult, secondResult)
        }
    }

    @Test
    fun testIfOnDetachThrows_providerIsDetached() {
        val provider = createAdaptiveProvider(onDetach = { TEST_ERROR })
        provider.matches(mockGridSpec)
        provider.attach(mockGridSpec, props)
        assertTrue(provider.isAttached)

        assertFailsWith<IllegalStateException> { provider.detach() }
        assertFalse(provider.isAttached)
    }

    // endregion

    // region Adaptive provider behavior

    @Test
    fun testAdaptiveProvider_matches_acceptsAnyGridSpecByDefault() {
        val adaptiveProvider = createAdaptiveProvider()

        val defaultGridSpecConsent = adaptiveProvider.matches(createGridSpec(5, 3, 13))
        val compactGridSpecConsent = adaptiveProvider.matches(createGridSpec(1, 1, 1))
        val expandedGridSpecConsent = adaptiveProvider.matches(createGridSpec(10, 10, 50))

        assertIs<Consent.Accept>(defaultGridSpecConsent)
        assertIs<Consent.Accept>(compactGridSpecConsent)
        assertIs<Consent.Accept>(expandedGridSpecConsent)
    }

    @Test
    fun testAdaptiveProvider_matches_acceptsOrRejectsBasedOnGridSpec() {
        val requiredRows = 5
        val adaptiveProvider =
            createAdaptiveProvider(
                doMatch = { gs ->
                    if (gs.rows == requiredRows) Consent.Accept
                    else Consent.Reject("requires $requiredRows rows")
                }
            )

        val consentAccept =
            adaptiveProvider.matches(createGridSpec(rows = 5, cols = 3, bricks = 13))
        val consentReject = adaptiveProvider.matches(createGridSpec(rows = 3, cols = 3, bricks = 9))

        assertIs<Consent.Accept>(consentAccept)
        assertIs<Consent.Reject>(consentReject)
    }

    @Test
    fun testAdaptiveProvider_whenMatchIsRejected_returnsRejectionReason() {
        val reason = "not compatible"
        val adaptiveProvider = createAdaptiveProvider(doMatch = { Consent.Reject(reason) })
        val consent = adaptiveProvider.matches(mockGridSpec)

        assertIs<Consent.Reject>(consent)
        assertEquals(reason, consent.reason)
    }

    // endregion

    // region Fixed provider behavior

    @Test
    fun testFixedProvider_matches_acceptsMatchingGridSpec() {
        val predefinedGridSpec = mockGridSpec
        val fixedProvider = createFixedProvider(predefinedGridSpec)
        val consent = fixedProvider.matches(predefinedGridSpec)

        assertIs<Consent.Accept>(consent)
    }

    @Test
    fun testFixedProvider_matches_rejectsDifferentGridSpec() {
        val predefinedGridSpec = mockGridSpec
        val fixedProvider = createFixedProvider(predefinedGridSpec)

        val rowsDifferConsent =
            fixedProvider.matches(createGridSpec(rows = 6, cols = 3, bricks = 13))
        val colsDifferConsent =
            fixedProvider.matches(createGridSpec(rows = 5, cols = 4, bricks = 20))
        val bricksDifferConsent =
            fixedProvider.matches(createGridSpec(rows = 5, cols = 3, bricks = 15))

        assertIs<Consent.Reject>(rowsDifferConsent)
        assertIs<Consent.Reject>(colsDifferConsent)
        assertIs<Consent.Reject>(bricksDifferConsent)
    }

    @Test
    fun testFixedProvider_whenMatchIsRejected_returnsRejectionReason() {
        val reason = "extra condition failed"
        val predefinedGridSpec = mockGridSpec
        val fixedProvider =
            createFixedProvider(predefinedGridSpec, doMatch = { Consent.Reject(reason) })

        val consent = fixedProvider.matches(predefinedGridSpec)

        assertIs<Consent.Reject>(consent)
        assertEquals(reason, consent.reason)
    }

    // endregion
}
