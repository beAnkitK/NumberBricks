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

    @Test
    fun testWhenResultIsNotStored_hasResult_returnsFalse() {
        DefaultProviderScope(digit = 5).use { scope -> assertFalse(scope.hasResult(mockKey)) }
    }

    @Test
    fun testWhenResultIsNotStored_resultOf_throws() {
        DefaultProviderScope(digit = 5).use { scope ->
            assertFailsWith<IllegalStateException> { scope.resultOf(mockKey) }
        }
    }

    @Test
    fun testWhenResultIsNotStored_storeResult_storesResult() {
        DefaultProviderScope(digit = 5).use { scope ->
            scope.storeResult(mockKey, mockResult)

            assertTrue(scope.hasResult(mockKey))
            assertEquals(mockResult, scope.resultOf(mockKey))
        }
    }

    @Test
    fun testWhenResultIsStored_resultOf_returnsStoredResult() {
        DefaultProviderScope(digit = 5).use { scope ->
            scope.storeResult(mockKey, mockResult)
            assertEquals(mockResult, scope.resultOf(mockKey))
        }
    }

    @Test
    fun testWhenResultIsStored_removeResult_returnsResultAndRemovesIt() {
        DefaultProviderScope(digit = 5).use { scope ->
            scope.storeResult(mockKey, mockResult)
            val removedResult = scope.removeResult(mockKey)

            assertEquals(mockResult, removedResult)
            assertFalse(scope.hasResult(mockKey))
        }
    }

    @Test
    fun testWhenResultIsNotStored_removeResult_returnsNull() {
        DefaultProviderScope(digit = 5).use { scope -> assertNull(scope.removeResult(mockKey)) }
    }

    @Test
    fun testWhenResultIsStored_storeResult_replacesPreviousResult() {
        DefaultProviderScope(digit = 5).use { scope ->
            val firstResult = List(mockGridSpec.brickCount) { it }
            val secondResult = List(mockGridSpec.brickCount) { it * 2 }

            scope.storeResult(mockKey, firstResult)
            scope.storeResult(mockKey, secondResult)
            assertEquals(secondResult, scope.resultOf(mockKey))
        }
    }

    @Test
    fun testWhenMetaIsNotProvided_hasMeta_returnsFalse() {
        DefaultProviderScope(digit = 5).use { scope ->
            assertFalse(scope.hasMeta(TestMetaProvider.IntMeta))
        }
    }

    @Test
    fun testWhenMetaIsNotProvided_metaOf_returnsNull() {
        DefaultProviderScope(digit = 5).use { scope ->
            assertNull(scope.metaOf(TestMetaProvider.IntMeta))
        }
    }

    @Test
    fun testProvideMeta_storesMeta() {
        DefaultProviderScope(digit = 5).use { scope ->
            with(scope) { metaProvider.provideMeta { TestMetaProvider.IntMeta providedBy 42 } }

            assertTrue(scope.hasMeta(TestMetaProvider.IntMeta))
            assertEquals(42, scope.metaOf(TestMetaProvider.IntMeta))
        }
    }

    @Test
    fun testWhenMetaIsAlreadyProvided_provideMeta_overwritesPreviousValue() {
        DefaultProviderScope(digit = 5).use { scope ->
            with(scope) {
                metaProvider.provideMeta { TestMetaProvider.IntMeta providedBy 10 }
                metaProvider.provideMeta { TestMetaProvider.IntMeta providedBy 20 }
            }

            assertEquals(20, scope.metaOf(TestMetaProvider.IntMeta))
        }
    }

    @Test
    fun testDispose_clearsAllResults_andMeta() {
        val scope = DefaultProviderScope(digit = 5)

        scope.storeResult(mockKey, mockResult)
        with(scope) { metaProvider.provideMeta { TestMetaProvider.IntMeta providedBy 42 } }
        scope.dispose()

        assertFalse(scope.hasResult(mockKey))
        assertFailsWith<IllegalStateException> { scope.resultOf(mockKey) }
        assertFalse(scope.hasMeta(TestMetaProvider.IntMeta))
        assertNull(scope.metaOf(TestMetaProvider.IntMeta))
    }
}
