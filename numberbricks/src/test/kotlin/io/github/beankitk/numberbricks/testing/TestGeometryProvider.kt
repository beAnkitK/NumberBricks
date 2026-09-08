package io.github.beankitk.numberbricks.testing

import io.github.beankitk.numberbricks.core.geometry.AdaptiveProvider
import io.github.beankitk.numberbricks.core.geometry.Consent
import io.github.beankitk.numberbricks.core.geometry.FixedProvider
import io.github.beankitk.numberbricks.core.geometry.GeometryProps
import io.github.beankitk.numberbricks.core.geometry.GeometryProvider
import io.github.beankitk.numberbricks.core.geometry.GridSpec
import io.github.beankitk.numberbricks.core.geometry.ProviderKey
import io.github.beankitk.numberbricks.core.geometry.ProviderScope

/** Configurable [GeometryProvider] implementation for testing. */
sealed interface TestGeometryProvider<T : Any> {

    val delegate: GeometryProvider<T>
        get() = when (this) {
            is FixedTestProvider -> this
            is AdaptiveTestProvider -> this
        }
}

/**
 * Creates a [TestGeometryProvider] with a fixed [GridSpec]. Use [provide] to define the data
 * returned by the provider.
 */
class FixedTestProvider<T : Any>(
    override val key: ProviderKey<T>,
    gridSpec: GridSpec,
    override val dependsOn: Set<ProviderKey<*>> = emptySet(),
    private val doMatch: ((GridSpec) -> Consent)? = null,
    private val onAttach: ((GridSpec, GeometryProps) -> Unit)? = null,
    private val onDetach: (() -> Unit)? = null,
    provide: ProviderScope.() -> List<T>,
) : FixedProvider<T>(gridSpec), TestGeometryProvider<T> {

    private val dataFactory: ProviderScope.() -> List<T> = provide

    override fun doMatch(digitGridSpec: GridSpec): Consent {
        return doMatch?.invoke(digitGridSpec) ?: super.doMatch(digitGridSpec)
    }

    override fun onAttach(digitGridSpec: GridSpec, geometryProps: GeometryProps) {
        onAttach?.invoke(digitGridSpec, geometryProps)
    }

    override fun ProviderScope.provide(): List<T> {
        val result = dataFactory()
        return result
    }

    override fun onDetach() {
        onDetach?.invoke()
    }
}

/**
 * Creates a [TestGeometryProvider] that adpat to any [GridSpec]. Use [provide] to define the
 * data returned by the provider.
 */
class AdaptiveTestProvider<T : Any>(
    override val key: ProviderKey<T>,
    override val dependsOn: Set<ProviderKey<*>> = emptySet(),
    private val doMatch: ((GridSpec) -> Consent)? = null,
    private val onAttach: ((GridSpec, GeometryProps) -> Unit)? = null,
    private val onDetach: (() -> Unit)? = null,
    provide: ProviderScope.() -> List<T>,
): AdaptiveProvider<T>(), TestGeometryProvider<T> {

    private val dataFactory: ProviderScope.() -> List<T> = provide

    override fun doMatch(digitGridSpec: GridSpec): Consent {
        return doMatch?.invoke(digitGridSpec) ?: super.doMatch(digitGridSpec)
    }

    override fun onAttach(digitGridSpec: GridSpec, geometryProps: GeometryProps) {
        onAttach?.invoke(digitGridSpec, geometryProps)
    }

    override fun ProviderScope.provide(): List<T> {
        val result = dataFactory()
        return result
    }

    override fun onDetach() {
        onDetach?.invoke()
    }
}
