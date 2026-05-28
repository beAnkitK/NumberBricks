package io.github.beankitk.numberbricks.testing

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
