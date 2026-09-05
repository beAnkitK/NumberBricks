package io.github.beankitk.numberbricks.core.geometry

import io.github.beankitk.numberbricks.testing.createGridSpec
import io.github.beankitk.numberbricks.testing.createKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class TestMetaProvider : BaseGeometryProvider<Int>() {
    override val key = createKey<Int>()
    override val dependsOn = emptySet<ProviderKey<*>>()
    override val providerGridPolicy = AdaptiveGridPolicy

    override fun ProviderScope.provideData(): List<Int> = buildProviderData { it }

    companion object {
        val IntMeta = defineMeta<TestMetaProvider, Int>()
    }
}

class DefaultProviderScopeTest {

    private val mockKey = createKey<Int>()
    private val mockGridSpec = createGridSpec(5, 3, 13)
    private val mockResult = List(mockGridSpec.brickCount) { it }

    private val metaProvider = TestMetaProvider()

    private fun ProviderScope(digit: Int = 5, gridSpec: GridSpec = mockGridSpec): DefaultProviderScope
        = DefaultProviderScope(digit, gridSpec)

    @Test
    fun testWhenResultIsNotStored_hasResult_returnsFalse() {
        ProviderScope().use { scope -> assertFalse(scope.hasResult(mockKey)) }
    }

    @Test
    fun testWhenResultIsNotStored_resultOf_throws() {
        ProviderScope().use { scope ->
            assertFailsWith<IllegalStateException> { scope.resultOf(mockKey) }
        }
    }

    @Test
    fun testWhenResultIsNotStored_storeResult_storesResult() {
        ProviderScope().use { scope ->
            scope.storeResult(mockKey, mockResult)

            assertTrue(scope.hasResult(mockKey))
            assertEquals(mockResult, scope.resultOf(mockKey))
        }
    }

    @Test
    fun testIfProviderResultSizeMismatchesGridSpec_storeResult_throws() {
        val gs = createGridSpec(7, 6, 27)
        ProviderScope(gridSpec = gs).use { scope ->

            val result1 = emptyList<Int>()
            assertFailsWith<IllegalArgumentException> { scope.storeResult(mockKey, result1) }
            assertFalse(scope.hasResult(mockKey))

            val result2 = List(30) { it }
            assertFailsWith<IllegalArgumentException> { scope.storeResult(mockKey, result2) }
            assertFalse(scope.hasResult(mockKey))
        }
    }

    @Test
    fun testIfProviderResultSizeMatchesGridSpec_storeResult_storesResult() {
        val gs = createGridSpec(7, 6, 27)
        ProviderScope(gridSpec = gs).use { scope ->

            val key1 = createKey<Int>()
            val result1 = List(scope.gridSpec.brickCount) { it }
            scope.storeResult(key1, result1)
            assertTrue(scope.hasResult(key1))

            val key2 = createKey<Int>()
            val result2 = List(gs.brickCount) { it }
            scope.storeResult(key2, result2)
            assertTrue(scope.hasResult(key1))

            val key3 = createKey<Int>()
            val result3 = scope.buildProviderData { it }
            scope.storeResult(key3, result3)
            assertTrue(scope.hasResult(key3))
        }
    }

    @Test
    fun testWhenResultIsStored_resultOf_returnsStoredResult() {
        ProviderScope().use { scope ->
            scope.storeResult(mockKey, mockResult)
            assertEquals(mockResult, scope.resultOf(mockKey))
        }
    }

    @Test
    fun testWhenResultIsStored_removeResult_returnsResultAndRemovesIt() {
        ProviderScope().use { scope ->
            scope.storeResult(mockKey, mockResult)
            val removedResult = scope.removeResult(mockKey)

            assertEquals(mockResult, removedResult)
            assertFalse(scope.hasResult(mockKey))
        }
    }

    @Test
    fun testWhenResultIsNotStored_removeResult_returnsNull() {
        ProviderScope().use { scope -> assertNull(scope.removeResult(mockKey)) }
    }

    @Test
    fun testWhenResultIsStored_storeResult_replacesPreviousResult() {
        ProviderScope().use { scope ->
            val firstResult = List(mockGridSpec.brickCount) { it }
            val secondResult = List(mockGridSpec.brickCount) { it * 2 }

            scope.storeResult(mockKey, firstResult)
            scope.storeResult(mockKey, secondResult)
            assertEquals(secondResult, scope.resultOf(mockKey))
        }
    }

    @Test
    fun testWhenMetaIsNotProvided_hasMeta_returnsFalse() {
        ProviderScope().use { scope ->
            assertFalse(scope.hasMeta(TestMetaProvider.IntMeta))
        }
    }

    @Test
    fun testWhenMetaIsNotProvided_metaOf_returnsNull() {
        ProviderScope().use { scope ->
            assertNull(scope.metaOf(TestMetaProvider.IntMeta))
        }
    }

    @Test
    fun testProvideMeta_storesMeta() {
        ProviderScope().use { scope ->
            with(scope) { metaProvider.provideMeta { TestMetaProvider.IntMeta providedBy 42 } }

            assertTrue(scope.hasMeta(TestMetaProvider.IntMeta))
            assertEquals(42, scope.metaOf(TestMetaProvider.IntMeta))
        }
    }

    @Test
    fun testWhenMetaIsAlreadyProvided_provideMeta_overwritesPreviousValue() {
        ProviderScope().use { scope ->
            with(scope) {
                metaProvider.provideMeta { TestMetaProvider.IntMeta providedBy 10 }
                metaProvider.provideMeta { TestMetaProvider.IntMeta providedBy 20 }
            }

            assertEquals(20, scope.metaOf(TestMetaProvider.IntMeta))
        }
    }

    @Test
    fun testDispose_clearsAllResults_andMeta() {
        val scope = ProviderScope()

        scope.storeResult(mockKey, mockResult)
        with(scope) { metaProvider.provideMeta { TestMetaProvider.IntMeta providedBy 42 } }
        scope.dispose()

        assertFalse(scope.hasResult(mockKey))
        assertFailsWith<IllegalStateException> { scope.resultOf(mockKey) }
        assertFalse(scope.hasMeta(TestMetaProvider.IntMeta))
        assertNull(scope.metaOf(TestMetaProvider.IntMeta))
    }
}
