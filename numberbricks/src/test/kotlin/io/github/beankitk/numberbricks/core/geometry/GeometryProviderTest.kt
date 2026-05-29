package io.github.beankitk.numberbricks.core.geometry

import io.github.beankitk.numberbricks.testing.AdaptiveTestProvider
import io.github.beankitk.numberbricks.testing.FixedTestProvider
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

    private fun createAdaptiveProvider(
        doMatch: ((GridSpec) -> Consent)? = null,
        onAttach: ((GridSpec, GeometryProps) -> Unit)? = null,
        onDetach: (() -> Unit)? = null,
    ) = AdaptiveTestProvider(
        key = mockKey,
        doMatch = doMatch,
        onAttach = onAttach,
        onDetach = onDetach,
        provideData = { gs -> List(gs.brickCount) { it } }
    )

    private fun createFixedProvider(
        gridSpec: GridSpec = mockGridSpec,
        doMatch: ((GridSpec) -> Consent)? = null,
        onAttach: ((GridSpec, GeometryProps) -> Unit)? = null,
        onDetach: (() -> Unit)? = null,
    ) = FixedTestProvider(
        key = mockKey,
        gridSpec = gridSpec,
        doMatch = doMatch,
        onAttach = onAttach,
        onDetach = onDetach,
        provideData = { gs -> List(gs.brickCount) { it } }
    )

    // endregion

    // region Common provider behavior

    @Test
    fun testIsAdaptive_matchesGridPolicy() {
        val adaptiveProvider = createAdaptiveProvider()
        assertTrue(adaptiveProvider.isAdaptive)

        val fixedProvider = createFixedProvider()
        assertFalse(fixedProvider.isAdaptive)
    }

    @Test
    fun testWhenAlreadyAttached_providerCannotBeMatched() {
        val provider = createAdaptiveProvider()
        provider.matches(mockGridSpec)
        provider.attach(mockGridSpec, props)

        assertFailsWith<IllegalStateException> { provider.matches(mockGridSpec) }
    }

   @Test
    fun testWhenNotMatched_providerCannotAttach() {
        val provider = createAdaptiveProvider()

        assertFailsWith<IllegalStateException> { provider.attach(mockGridSpec, props) }
    }

    @Test
    fun testWhenMatchWasRejected_providerCannotAttach() {
        val provider = createAdaptiveProvider(
            doMatch = { Consent.Reject("Rejected For Test") }
        )
        provider.matches(mockGridSpec)

        assertFailsWith<IllegalStateException> { provider.attach(mockGridSpec, props) }
    }

    @Test
    fun testIfDoMatchThrows_providerCannotAttach() {
        val provider = createAdaptiveProvider(
            doMatch = { TEST_ERROR }
        )
        assertFailsWith<IllegalStateException> { provider.matches(mockGridSpec) }
        assertFailsWith<IllegalStateException> { provider.attach(mockGridSpec, props) }
    }

    @Test
    fun testWhenAlreadyAttached_providerCannotReattach() {
        val provider = createAdaptiveProvider()
        provider.matches(mockGridSpec)
        provider.attach(mockGridSpec, props)

        assertFailsWith<IllegalStateException> { provider.attach(mockGridSpec, props) }
    }

    @Test
    fun testIfOnAttachThrows_providerIsNotAttached() {
        val provider = createAdaptiveProvider(
            onAttach = { _, _ ->  TEST_ERROR }
        )
        provider.matches(mockGridSpec)
        assertFailsWith<IllegalStateException> { provider.attach(mockGridSpec, props) }
    }

    @Test
    fun testWhenNotAttached_providerCannotProvideResult() {
        val provider = createAdaptiveProvider()
        val scope = DefaultProviderScope(digit = 0)

        try {
            assertFailsWith<IllegalStateException> {
                with(provider) { scope.provide() }
            }
        } finally { scope.dispose() }
    }

    @Test
    fun testWhenDetached_providerCanBeReattached() {
        val attachedGridSpec = mockGridSpec
        val provider = createAdaptiveProvider()
        val providerScope = DefaultProviderScope(digit = 0)

        providerScope.use { scope ->
            provider.matches(attachedGridSpec)
            provider.attach(attachedGridSpec, props)

            val firstResult = with(provider) { scope.provide() }
            provider.detach()

            assertFailsWith<IllegalStateException> {
                with(provider) { scope.provide() }
            }

            provider.matches(attachedGridSpec)
            provider.attach(attachedGridSpec, props)

            val secondResult = with(provider) { scope.provide() }
            assertEquals(firstResult, secondResult)
        }
    }

    // endregion

    // region Adaptive provider behavior

    @Test
    fun testAdaptiveProvider_whenNotAttached_providerGridSpecCannotBeAccessed() {
        val adaptiveProvider = createAdaptiveProvider()
        assertFailsWith<IllegalStateException> { adaptiveProvider.providerGridSpec }
    }

    @Test
    fun testAdaptiveProvider_attach_setsProviderGridSpec() {
        val attachedGridSpec = mockGridSpec
        val adaptiveProvider = createAdaptiveProvider()
        adaptiveProvider.matches(attachedGridSpec)
        adaptiveProvider.attach(attachedGridSpec, props)

        assertEquals(attachedGridSpec, adaptiveProvider.providerGridSpec)
    }

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
        val adaptiveProvider = createAdaptiveProvider(
            doMatch = { gs ->
                if (gs.rows == requiredRows) Consent.Accept
                else Consent.Reject("requires $requiredRows rows")
            }
        )

        val consentAccept = adaptiveProvider.matches(createGridSpec(rows = 5, cols = 3, bricks = 13))
        val consentReject = adaptiveProvider.matches(createGridSpec(rows = 3, cols = 3, bricks = 9))

        assertIs<Consent.Accept>(consentAccept)
        assertIs<Consent.Reject>(consentReject)
    }

    @Test
    fun testAdaptiveProvider_whenMatchIsRejected_returnsRejectionReason() {
        val reason = "not compatible"
        val adaptiveProvider = createAdaptiveProvider(
            doMatch = { Consent.Reject(reason) }
        )
        val consent = adaptiveProvider.matches(mockGridSpec)

        assertIs<Consent.Reject>(consent)
        assertEquals(reason, consent.reason)
    }

    @Test
    fun testAdaptiveProvider_detach_clearsProviderGridSpec() {
        val attachedGridSpec = mockGridSpec
        val adaptiveProvider = createAdaptiveProvider()

        adaptiveProvider.matches(attachedGridSpec)
        adaptiveProvider.attach(attachedGridSpec, props)

        assertEquals(attachedGridSpec, adaptiveProvider.providerGridSpec)
        adaptiveProvider.detach()

        assertFailsWith<IllegalStateException> { adaptiveProvider.providerGridSpec }
    }

    // endregion

    // region Fixed provider behavior

    @Test
    fun testFixedProvider_providerGridSpec_remainsPredefined() {
        val predefinedGridSpec = mockGridSpec
        val fixedProvider = createFixedProvider(predefinedGridSpec)

        assertEquals(predefinedGridSpec, fixedProvider.providerGridSpec)

        fixedProvider.matches(predefinedGridSpec)
        fixedProvider.attach(predefinedGridSpec, props)

        assertEquals(predefinedGridSpec, fixedProvider.providerGridSpec)
    }

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

        val rowsDifferConsent = fixedProvider.matches(createGridSpec(rows = 6, cols = 3, bricks = 13))
        val colsDifferConsent = fixedProvider.matches(createGridSpec(rows = 5, cols = 4, bricks = 20))
        val bricksDifferConsent = fixedProvider.matches(createGridSpec(rows = 5, cols = 3, bricks = 15))

        assertIs<Consent.Reject>(rowsDifferConsent)
        assertIs<Consent.Reject>(colsDifferConsent)
        assertIs<Consent.Reject>(bricksDifferConsent)
    }

    @Test
    fun testFixedProvider_whenMatchIsRejected_returnsRejectionReason() {
        val reason = "extra condition failed"
        val predefinedGridSpec = mockGridSpec
        val fixedProvider = createFixedProvider(
            predefinedGridSpec,
            doMatch = { Consent.Reject(reason) }
        )

        val consent = fixedProvider.matches(predefinedGridSpec)

        assertIs<Consent.Reject>(consent)
        assertEquals(reason, consent.reason)
    }

    @Test
    fun testFixedProvider_detach_retainsPredefinedGridSpec() {
        val predefinedGridSpec = mockGridSpec
        val fixedProvider = createFixedProvider(predefinedGridSpec)

        fixedProvider.matches(predefinedGridSpec)
        fixedProvider.attach(predefinedGridSpec, props)

        assertEquals(predefinedGridSpec, fixedProvider.providerGridSpec)
        fixedProvider.detach()

        assertEquals(predefinedGridSpec, fixedProvider.providerGridSpec)
    }

    // endregion
}
