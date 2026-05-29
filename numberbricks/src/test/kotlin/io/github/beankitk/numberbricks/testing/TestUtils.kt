package io.github.beankitk.numberbricks.testing

import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.GridSpec
import io.github.beankitk.numberbricks.core.geometry.ProviderKey

/**
 * Creates a [ProviderKey] for tests. Uses [family] when provided; otherwise,
 * the key is its own family.
 */
fun <R : Any> createKey(family: ProviderKey<R>? = null): ProviderKey<R> {
    return object : ProviderKey<R> {
        override val family = family ?: this
    }
}

/** Creates a minimal [GeometryProps] for tests. */
fun createProps(): GeometryProps = object : GeometryProps {}

/** Creates a [GridSpec] for tests. */
fun createGridSpec(rows: Int, cols: Int, bricks: Int) = GridSpec(rows, cols, bricks)

val TEST_ERROR: Nothing
    get() = error("Test Error")
