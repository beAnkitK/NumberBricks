package io.github.beankitk.numberbricks.testing

import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.GridSpec
import kotlin.test.assertTrue

/**
 * Builds a provider dependency graph and lets you resolve the order and assert on it.
 *
 * Example:
 * ```
 * dependencyGraph {
 *     val a = provider()
 *     val b = provider(a)
 *     val c = provider(b, a)
 *     val d = provider()
 *
 *     resolve(gridSpec, props)
 *         .assertFirst(a)
 *         .assertInOrder(b, c, d)
 * }
 * ```
 */
fun dependencyGraph(block: DependencyGraph.() -> Unit) =
    DependencyGraph().block()

/** References a geometry provider created in a dependency graph. */
@JvmInline
value class ProviderRef internal constructor(internal val index: Int) {
    override fun toString(): String = "Provider#$index"
}

/**
 * Builds a provider dependency graph and lets you resolve the order and assert on it. Providers
 * are shuffled before resolution by default to ensure dependencies determine their order.
 */
class DependencyGraph internal constructor() {
    private var resolving = false
    private var shuffleProviders = true
    private var providerCount = 0
    private var dependencies = arrayOfNulls<IntArray>(4)

    /** Adds a provider with no dependencies to this graph and returns its reference. */
    fun provider(): ProviderRef {
        checkNotResolving()
        ensureCapacity()

        val ref = ProviderRef(providerCount++)
        dependencies[ref.index] = null
        return ref
    }

    /** Adds a provider with [dependsOn] dependencies to this graph and returns its reference. */
    fun provider(dependsOn: Set<ProviderRef>): ProviderRef {
        checkNotResolving()
        ensureCapacity()

        val ref = ProviderRef(providerCount++)
        dependencies[ref.index] = dependsOn.map { it.index }.toIntArray()
        return ref
    }

    /** Makes this provider depend on [dependsOn]. */
    fun ProviderRef.dependsOn(dependsOn: Set<ProviderRef>) {
        checkNotResolving()
        val extra = dependsOn.map { it.index }.toIntArray()
        val current = dependencies[this.index]
        if (current == null) {
            dependencies[this.index] = extra
        } else {
            dependencies[this.index] = current + extra
        }
    }

    /** Controls whether providers are shuffled before resolution. */
    fun shuffleProviders(enabled: Boolean) {
        checkNotResolving()
        this.shuffleProviders = enabled
    }

    /** Resolves the graph and returns assertions for the provider execution order. */
    fun resolve(
        gridSpec: GridSpec,
        props: GeometryProps,
        digit: Int = 0,
    ): ProviderOrderAssertion {
        checkNotResolving()
        require(digit in 0..9 || digit == -1) {
            "Cannot resolve providers for digit $digit: requires 0 to 9 or -1"
        }
        resolving = true
        return try {
            val keys = Array(providerCount) { createKey<Int>() }
            val executionIndex = IntArray(providerCount) { NOT_EXECUTED }
            var executionCount = 0

            val providers = Array(providerCount) { index ->
                val dependencies = dependencies[index]

                AdaptiveTestProvider(
                    key = keys[index],
                    dependsOn = dependencies?.mapTo(HashSet(dependencies.size)) { keys[it] } ?: emptySet(),
                    provideData = { gs ->
                        executionIndex[index] = executionCount++
                        List(gs.brickCount) { it }
                    },
                )
            }
            if (shuffleProviders) providers.shuffle()

            TestDigitBuilder(providers.toList())
                .also { it.construct(gridSpec, props) }
                .buildBricks(digit)

            ProviderOrderAssertion(executionIndex)
        } finally { resolving = false }
    }

    private fun ensureCapacity() {
        if (providerCount == dependencies.size)
            dependencies = dependencies.copyOf(dependencies.size * 2)
    }

    private fun checkNotResolving() =
        check(!resolving) { "Cannot modify the graph while resolve() has been called" }
}

private const val NOT_EXECUTED = -1

/** Assertions for checking the order in which providers were resolved. */
@JvmInline
value class ProviderOrderAssertion internal constructor(private val orderIndex: IntArray) {

    internal val lastPosition: Int
        get() = orderIndex.size - 1

    internal fun positionOf(ref: ProviderRef): Int {
        val position = orderIndex[ref.index]
        check(position >= 0) { "$ref never executed." }
        return position
    }

    internal fun precedes(before: ProviderRef, after: ProviderRef) = assertTrue(
        positionOf(before) < positionOf(after),
        "Expected $before to resolve before $after",
    )

    /** Asserts that [provider] resolves first. */
    fun assertFirst(provider: ProviderRef): ProviderOrderAssertion {
        assertTrue(positionOf(provider) == 0, "Expected $provider to resolve first")
        return this
    }

    /** Asserts that [provider] resolves last. */
    fun assertLast(provider: ProviderRef): ProviderOrderAssertion {
        assertTrue(positionOf(provider) == lastPosition, "Expected $provider to resolve last")
        return this
    }

    /** Asserts that [provider] resolves before every provider in [allOf]. */
    fun assertBefore(provider: ProviderRef, allOf: Set<ProviderRef>): ProviderOrderAssertion {
        allOf.forEach { precedes(provider, it) }
        return this
    }

    /** Asserts that [provider] resolves after every provider in [allOf]. */
    fun assertAfter(provider: ProviderRef, allOf: Set<ProviderRef>): ProviderOrderAssertion {
        allOf.forEach { precedes(it, provider) }
        return this
    }

    /** Asserts that the providers resolve in the given order. */
    fun assertInOrder(providers: Set<ProviderRef>): ProviderOrderAssertion {
        require(providers.size >= 2) { "assertInOrder needs at least 2 providers" }
        providers.zipWithNext { a, b -> precedes(a, b) }
        return this
    }
}

/** Adds a provider that depends on [first] and returns its reference. */
fun DependencyGraph.provider(first: ProviderRef) =
    provider(setOf(first))

/** Adds a provider that depends on [first] and [second] and returns its reference. */
fun DependencyGraph.provider(first: ProviderRef, second: ProviderRef) =
    provider(setOf(first, second))
    
/** Adds a provider that depends on [first], [second], and [third] and returns its reference. */
fun DependencyGraph.provider(
    first: ProviderRef,
    second: ProviderRef,
    third: ProviderRef,
) = provider(setOf(first, second, third))
