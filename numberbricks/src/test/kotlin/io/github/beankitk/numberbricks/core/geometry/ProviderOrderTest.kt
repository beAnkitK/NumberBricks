
package io.github.beankitk.numberbricks.core.geometry

import io.github.beankitk.numberbricks.testing.createGridSpec
import io.github.beankitk.numberbricks.testing.createProps
import io.github.beankitk.numberbricks.testing.dependencyGraph
import io.github.beankitk.numberbricks.testing.provider
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ProviderOrderTest {
    private val gridSpec = createGridSpec(5, 3, 13)
    private val props = createProps()

    @Test
    fun testGivenLinearDependencies_resolves() = dependencyGraph {
        // A -> B -> C
        //
        // Each provider resolves after its dependency.
        val a = provider()
        val b = provider(a)
        val c = provider(b)

        resolve(gridSpec, props).assertInOrder(setOf(a, b, c))
    }

    @Test
    fun testGivenFanOutDependencies_resolves() = dependencyGraph {
        //     A
        //   / | \
        //  B  C  D
        //
        // A resolves before B, C, and D.
        val a = provider()
        val b = provider(a)
        val c = provider(a)
        val d = provider(a)

        resolve(gridSpec, props).assertBefore(a, setOf(b, c, d))
    }

    @Test
    fun testGivenFanInDependencies_resolves() = dependencyGraph {
        // A  B  C
        //  \ | /
        //    D
        //
        // D resolves after A, B, and C.
        val a = provider()
        val b = provider()
        val c = provider()
        val d = provider(a, b, c)

        resolve(gridSpec, props).assertAfter(d, setOf(a, b, c))
    }

    @Test
    fun testGivenDiamondDependencies_resolves() = dependencyGraph {
        //      A
        //     / \
        //    B   C
        //     \ /
        //      D
        //
        // A resolves before B and C.
        // D resolves after B and C.
        val a = provider()
        val b = provider(a)
        val c = provider(a)
        val d = provider(b, c)

        resolve(gridSpec, props)
            .assertFirst(a)
            .assertBefore(a, setOf(b, c, d))
            .assertAfter(d, setOf(a, b, c))
            .assertLast(d)
    }

    @Test
    fun testGivenMultiLevelTree_resolves() = dependencyGraph {
        //       A
        //     /   \
        //    B     C
        //   / \     \
        //  D   E     F
        //
        // Each provider resolves after its direct dependency.
        val a = provider()
        val b = provider(a)
        val c = provider(a)
        val d = provider(b)
        val e = provider(b)
        val f = provider(c)

        resolve(gridSpec, props)
            .assertBefore(a, setOf(b, c))
            .assertBefore(b, setOf(d, e))
            .assertBefore(c, setOf(f))
    }

    @Test
    fun testGivenDisconnectedProviders_resolves() = dependencyGraph {
        // A -> B
        // C -> D
        //
        // Each dependency tree resolves independently.
        val a = provider()
        val b = provider(a)
        val c = provider()
        val d = provider(c)

        resolve(gridSpec, props)
            .assertInOrder(setOf(a, b))
            .assertInOrder(setOf(c, d))
    }

    @Test
    fun testGivenCircularDependencies_throws() = dependencyGraph {
        // A <-> B
        //
        // A circular dependency cannot be resolved.
        val a = provider()
        val b = provider(a)
        a.dependsOn(setOf(b))

        assertFailsWith<IllegalStateException> { resolve(gridSpec, props) }
    }
}
